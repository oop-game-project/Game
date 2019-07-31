package Engine;

import GUI.GUI;
import GUI.GUI_2D;
import Engine.LevelsProcessor.SinglePlayerLevel;
import Engine.CollisionsProcessor.*;
import Engine.GameObjects.*;

import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.locks.ReentrantLock;

import jdk.jshell.spi.ExecutionControl.NotImplementedException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Engine extends WindowAdapter implements KeyListener
{
    public Engine(SinglePlayerLevel inputLevel, Runnable setLauncherVisibleInput)
    {
        this.setLauncherVisible = setLauncherVisibleInput;
        this.currentLevel = inputLevel;
        this.levelUpdater = new LevelUpdater();
    }

//
// KeyListener implementations
//

    private final KeysInfo keysInfo = new KeysInfo();

    private static class KeysInfo
    {
        private HashSet<Integer> keysPressed = new HashSet<>();
        /**
         * Is this lock really needed?
         **/
        private ReentrantLock keysPressedLock = new ReentrantLock();

        private HashSet<Integer> keysReleased = new HashSet<>();
        private ReentrantLock keysReleasedLock = new ReentrantLock();
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) { }

    @Override
    public void keyPressed(@NotNull KeyEvent keyEvent)
    {
        this.keysInfo.keysPressedLock.lock();
        try
        {
            this.keysInfo.keysPressed.add(keyEvent.getKeyCode());
        }
        finally
        {
            this.keysInfo.keysPressedLock.unlock();
        }
    }

    @Override
    public void keyReleased(@NotNull KeyEvent keyEvent)
    {
        this.keysInfo.keysPressedLock.lock();
        this.keysInfo.keysReleasedLock.lock();
        try
        {
            this.keysInfo.keysPressed.remove(keyEvent.getKeyCode());

            this.keysInfo.keysReleased.add(keyEvent.getKeyCode());
        }
        finally
        {
            this.keysInfo.keysPressedLock.unlock();
            this.keysInfo.keysReleasedLock.unlock();
        }
    }

//
// WindowAdapter overrides
//

    private final Runnable setLauncherVisible;

    private boolean closeGame = false;
    /**
     * Lock for rendering safety. If rendering starts when game is closing,
     * then GUI thread breaks
     */
    private ReentrantLock closeGameLock = new ReentrantLock();

    @Override
    public void windowClosing(WindowEvent windowEvent)
    {
        this.closeGameLock.lock();
        try
        {
            this.closeGame = true;
        }
        finally
        {
            this.closeGameLock.unlock();
        }

        this.setLauncherVisible.run();
    }

//
// Main game loop section
//

    private final SinglePlayerLevel currentLevel;
    private final LevelUpdater levelUpdater;

    private long gameLoopIterationsCounter = 0L;

    private class LevelUpdater
    {
        private final GameObjectsSpawner gameObjectsSpawner = new GameObjectsSpawner();
        private final StateUpdater stateUpdater = new StateUpdater();

        private class GameObjectsSpawner
        {
            private final SinglePlayerLevel currentLevel = Engine.this.currentLevel;
            private final KeysInfo keysInfo = Engine.this.keysInfo;
            private final GameObjects gameObjects = new GameObjects();

            /**
             * Fire per this amount of milliseconds
             *
             * WouldBeBetter change to "per this amount of game loop iterations"
             **/
            private static final int PLAYER_FIRING_FREQUENCY = 50;
            /**
             * Volley per amount of game loop iterations
             */
            private static final long SPHERE_BOSS_VOLLEY_FREQUENCY = 10;

            private boolean playerIsFiring = false;
            /**
             * "Player firing was switched" by currently pressed 'Z' and this key wasn't
             * released yet
             **/
            private boolean playerFiringWasSwitched = false;

            private void spawnPlayerProjectiles()
            {
                if (this.keysInfo.keysReleased.contains(KeyEvent.VK_Z))
                {
                    this.keysInfo.keysReleasedLock.lock();
                    try
                    {
                        this.keysInfo.keysReleased.remove(KeyEvent.VK_Z);
                    }
                    finally
                    {
                        this.keysInfo.keysReleasedLock.unlock();
                    }

                    this.playerFiringWasSwitched = false;
                }

                if (this.keysInfo.keysPressed.contains(KeyEvent.VK_Z)
                    && !this.playerFiringWasSwitched)
                {
                    this.playerIsFiring = !this.playerIsFiring;
                    this.playerFiringWasSwitched = true;
                }

                // Spawn player's projectile based on:
                // 1. If player's firing was toggled
                // 2. When last projectile was fired
                if (this.playerIsFiring
                    && System.currentTimeMillis()
                       - this.currentLevel.player.lastProjectileWasFiredTime
                       > PLAYER_FIRING_FREQUENCY)
                {
                    // BasicProjectile is a circle that should be spawned in front of
                    // player when fired
                    int[] BasicProjectileSpawnLocation = new int[]{
                        this.currentLevel.player.currentLocation[0] - 4,
                        this.currentLevel.player.currentLocation[1] - 7,
                        this.currentLevel.player.currentLocation[2]};
                    this.currentLevel.projectiles.add(
                        this.gameObjects.new BasicProjectile(
                            BasicProjectileSpawnLocation,
                            this.currentLevel.player));

                    this.currentLevel.player.lastProjectileWasFiredTime =
                        System.currentTimeMillis();
                }
            }

            private void spawnSphereBossProjectiles(@NotNull SphereBoss sphereBoss)
            {
                if (Engine.this.gameLoopIterationsCounter
                    - sphereBoss.lastVolleyIteration
                        > SPHERE_BOSS_VOLLEY_FREQUENCY)
                {
                    // TODO
                }
            }

            private void spawnMobsProjectiles()
            {
                for (MortalObject mob : this.currentLevel.mobs)
                    if (mob instanceof SphereBoss)
                        spawnSphereBossProjectiles((SphereBoss) mob);
            }

            private void spawnProjectiles()
            {
                this.spawnPlayerProjectiles();

                this.spawnMobsProjectiles();
            }

            private void spawnMobs()
            {
                HashSet<Long> nonSpawnedMobsKeySet =
                    new HashSet<>(this.currentLevel.nonSpawnedMobs.keySet());
                for (long spawnIteration : nonSpawnedMobsKeySet)
                    if (Engine.this.gameLoopIterationsCounter > spawnIteration)
                        this.currentLevel.mobs.addAll(
                            this.currentLevel.nonSpawnedMobs.remove(spawnIteration));
            }
        }

        private class StateUpdater
        {
            private final SinglePlayerLevel currentLevel = Engine.this.currentLevel;
            private final KeysInfo keysInfo = Engine.this.keysInfo;
            private final CollisionsProcessor collisionsProcessor =
                new CollisionsProcessor(this.currentLevel.gameFieldSize);

            /**
             * Pixels per level update
             **/
            private static final int PLAYER_MOVE_SPEED = 5;

            @NotNull
            private int[] getInputMoveVector()
            {
                int[] inputMoveVector = new int[]{0, 0, 0};
                this.keysInfo.keysPressedLock.lock();
                try
                {
                    for (int keyCode : this.keysInfo.keysPressed)
                        switch (keyCode)
                        {
                            case KeyEvent.VK_RIGHT:
                                inputMoveVector[0] += PLAYER_MOVE_SPEED;
                                break;

                            case KeyEvent.VK_DOWN:
                                inputMoveVector[1] += PLAYER_MOVE_SPEED;
                                break;

                            case KeyEvent.VK_LEFT:
                                inputMoveVector[0] -= PLAYER_MOVE_SPEED;
                                break;

                            case KeyEvent.VK_UP:
                                inputMoveVector[1] -= PLAYER_MOVE_SPEED;
                                break;
                        }
                }
                finally
                {
                    this.keysInfo.keysPressedLock.unlock();
                }

                return inputMoveVector;
            }

            private void updatePlayerState()
            {
                // TODO: check hitpoints here (make method for that). All code below
                //  to another method
                int[] inputMoveVector = this.getInputMoveVector();

                if (inputMoveVector[0] != 0
                    || inputMoveVector[1] != 0
                    || inputMoveVector[2] != 0)
                {
                    // Collisions check
                    Collision playerCollision = this.collisionsProcessor.getCollision(
                        this.currentLevel, inputMoveVector, this.currentLevel.player);
                    switch (playerCollision.event)
                    {
                        case OK:
                            break;

                        // WouldBeBetter teleport player to the right position, whether player
                        //  deep out of bounds or not. Not just leave player standing still
                        case OUT_HORIZONTAL:
                        {
                            inputMoveVector[0] = 0;
                            break;
                        }

                        case OUT_VERTICAL:
                        {
                            inputMoveVector[1] = 0;
                            break;
                        }

                        case OUT_DIAGONAL:
                        {
                            inputMoveVector[0] = 0;
                            inputMoveVector[1] = 0;
                            break;
                        }

                        default:
                            throw new IllegalArgumentException(
                                "Unknown collision received while moving Player");
                    }

                    this.currentLevel.player.modifyLocation(inputMoveVector);
                }
            }

            @NotNull
            @Contract(value = "_ -> new", pure = true)
            private int[] getBasicProjectileAutoMoveVector(
                @NotNull BasicProjectile basicProjectile)
            {
                if (basicProjectile.firedByPlayer)
                    return new int[]{ 0, -10, 0 };
                else
                    return new int[]{ 0, 10, 0 };
            }

            /**
             * This method every second change output Z position based on order in an
             * array "CYCLIC_Z_CHANGE"
             **/
            @Contract(pure = true)
            private int getCyclicZChange()
            {
                return new int[]{ 0, -1, 0, 1 }
                    [(int) (Engine.this.gameLoopIterationsCounter / 100 % 4)];
            }

            private void updateBasicProjectileState(
                BasicProjectile projectileObject,
                ArrayList<MovableObject> projectilesForDespawning)
            {
                int[] projectileMoveVector = this.getBasicProjectileAutoMoveVector(
                    projectileObject);
                Collision projectileCollision =
                    this.collisionsProcessor.getCollision(
                        this.currentLevel, projectileMoveVector, projectileObject);

                switch (projectileCollision.event)
                {
                    case OK:
                    {
                        projectileObject.modifyLocation(projectileMoveVector);
                        break;
                    }

                    case BASIC_PROJECTILE_IS_OUT:
                    {
                        projectilesForDespawning.add(projectileObject);
                        break;
                    }

                    default:
                        throw new IllegalArgumentException(
                            "Unknown collision received");
                }
            }

            private void updateProjectilesState()
            {
                ArrayList<MovableObject> projectilesForDespawning = new ArrayList<>();
                for (MovableObject projectileObject : this.currentLevel.projectiles)
                {
                    switch (projectileObject.getClass().getSimpleName())
                    {
                        case "BasicProjectile":
                        {
                            this.updateBasicProjectileState(
                                (BasicProjectile) projectileObject,
                                projectilesForDespawning);
                            break;
                        }

                        default:
                        {
                            try
                            {
                                throw new NotImplementedException(
                                    "\""
                                    + projectileObject.getClass().getSimpleName()
                                    + "\": cannot update state of this object");
                            }
                            catch (NotImplementedException occurredExc)
                            {
                                occurredExc.printStackTrace();
                            }
                        }
                    }
                }

                // Despawning
                this.currentLevel.projectiles.removeAll(projectilesForDespawning);
            }

            private void updateSphereMobState(
                @NotNull SphereMob mob,
                ArrayList<MortalObject> mobsForDespawning)
            {
                // TODO: Check collisions here

                mob.modifyLocation(mob.autoMovingVector);
                mob.currentLocation[2] = this.getCyclicZChange();
            }

            private void updateSphereBossState(
                SphereBoss mob,
                ArrayList<MortalObject> mobsForDespawning)
            {
                // TODO: Check collisions. Boss moves horizontally to the right and to the left.
                //  Do "autoMovingVector" direction swapping if boss hit vertical border
            }

            private void updateMobsState()
            {
                ArrayList<MortalObject> mobsForDespawning = new ArrayList<>();
                for (MovableObject mob : this.currentLevel.mobs)
                {
                    switch (mob.getClass().getSimpleName())
                    {
                        case "SphereMob":
                        {
                            this.updateSphereMobState((SphereMob) mob, mobsForDespawning);
                            break;
                        }

                        case "SphereBoss":
                        {
                            this.updateSphereBossState(
                                (SphereBoss) mob,
                                mobsForDespawning);
                            break;
                        }

                        default:
                        {
                            try
                            {
                                throw new NotImplementedException(
                                    "\""
                                    + mob.getClass().getSimpleName()
                                    + "\": cannot update state of this object");
                            }
                            catch (NotImplementedException occurredExc)
                            {
                                occurredExc.printStackTrace();
                            }
                        }
                    }
                }

                // TODO: Despawning
            }
        }

        /**
         * WouldBeBetter actually think about order of doing things here
         **/
        private void updateLevel()
        {
            // WouldBeBetter check if pause is active (Latin 'P' was pressed)

            // Move player
            this.stateUpdater.updatePlayerState();

            // Despawn or move projectiles
            this.stateUpdater.updateProjectilesState();

            // Despawn or move mobs
            this.stateUpdater.updateMobsState();

            // Spawn all projectiles
            this.gameObjectsSpawner.spawnProjectiles();

            // Spawn all mobs
            this.gameObjectsSpawner.spawnMobs();
        }
    }

    /**
     * Change main GUI here: GUI_2D or GUI_3D
     **/
    private final GUI gui = new GUI_2D();

    public void runGameLoop()
    {
        this.gui.init(this, this.currentLevel);

        new Thread(this::gameLoop).start();
    }

    /**
     * Игра работает в режиме 60 итераций игрового цикла (обновление
     * И рендер уровня в одной итерации) в секунду.
     *
     * По сути, секунда разбита на 60 частей. Выравнивание происходит
     * таким образом, что в начале каждой 1\60 части секунды должна начинаться
     * КАЖДАЯ итерация игрового цикла. НЕТ гарантии, что при таком подходе
     * не будет потеряна одна из 1\60-ой частей секунды
     *
     * Таким образом, каждое обновление уровня происходит с рассчетом
     * ТОЛЬКО на текущую 1/60 часть секунды. Это позволяет избавиться от
     * дробных величин при модификации позиции движущихся объектов.
     **/
    public void timeAlignment()
    {
        long millisInCurrentSecond = System.currentTimeMillis() % 1000;
        try
        {
            Thread.sleep(1000 / 60 - millisInCurrentSecond % (1000 / 60));
        }
        catch (InterruptedException exception)
        {
            exception.printStackTrace();
        }
    }

    public void gameLoop()
    {
        while (!this.closeGame)
        {
            // Update
            this.levelUpdater.updateLevel();

            // Render
            this.closeGameLock.lock();
            try
            {
                if (!this.closeGame)
                    gui.render();
            }
            finally
            {
                this.closeGameLock.unlock();
            }

            // Wait a little for time alignment. It is needed for CPU power
            // saving
            this.timeAlignment();

            this.gameLoopIterationsCounter += 1;
        }
    }
}
