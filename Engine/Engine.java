package Engine;

import GUI.GUI;
import GUI.GUI_2D;

import static Engine.LevelsProcessor.*;
import Engine.CollisionsProcessor.*;
import Engine.GameObjects.*;

import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;

import java.util.HashSet;
import java.util.concurrent.locks.ReentrantLock;

import jdk.jshell.spi.ExecutionControl.NotImplementedException;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Engine extends WindowAdapter implements KeyListener
{
    public Engine(LevelsProcessor.GameLevel inputLevel, Runnable setLauncherVisibleInput)
    {
        this.setLauncherVisible = setLauncherVisibleInput;
        this.currentLevel = inputLevel;
        this.levelUpdater = new LevelUpdater();
    }

//
// KeyListener implementations
//

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

    private final KeysInfo keysInfo = new KeysInfo();

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

    private class LevelUpdater
    {
        private class GameObjectsSpawner
        {
            // private GameObjectsSpawner()
            // {
            //    // Spawn initial interface objects here
            // }

            // 'currentLevel' reference shortening
            private final LevelsProcessor.GameLevel currentLevel = Engine.this.currentLevel;

            /**
             * Fire per this amount of milliseconds
             *
             * Improvement: change to "per this amount of game loop iterations"
             **/
            private static final int PLAYER_FIRING_FREQUENCY = 50;
            /**
             * Volley per amount of game loop iterations
             */
            private static final long SPHERE_BOSS_VOLLEY_FREQUENCY = 15;

            private boolean playerIsFiring = true;
            /**
             * "Player firing was switched" by currently pressed 'Z' and this key wasn't
             * released yet
             **/
            private boolean playerFiringWasSwitched = false;

            private void spawnGameOverInscription()
            {
                this.currentLevel.interfaceObjects.add(
                    new GameOverInscription(new int[] {145, 326, 0}));
            }

            private void spawnCompletedInscription()
            {
                this.currentLevel.interfaceObjects.add(
                    new CompletedInscription(new int[] {132, 326, 0}));
            }

            private void spawnPlayerProjectiles()
            {
                if (Engine.this.keysInfo.keysReleased.contains(KeyEvent.VK_Z))
                {
                    Engine.this.keysInfo.keysReleasedLock.lock();
                    try
                    {
                        Engine.this.keysInfo.keysReleased.remove(KeyEvent.VK_Z);
                    }
                    finally
                    {
                        Engine.this.keysInfo.keysReleasedLock.unlock();
                    }

                    this.playerFiringWasSwitched = false;
                }

                if (Engine.this.keysInfo.keysPressed.contains(KeyEvent.VK_Z)
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
                       - this.currentLevel.player.getLastProjectileWasFiredTime()
                       > PLAYER_FIRING_FREQUENCY)
                {
                    // BasicProjectile is a circle that should be spawned in front of
                    // player when fired
                    int[] BasicProjectileSpawnLocation = new int[]{
                        this.currentLevel.player.getCurrentLocation()[0] - 5,
                        this.currentLevel.player.getCurrentLocation()[1] - 10,
                        this.currentLevel.player.getCurrentLocation()[2]};
                    this.currentLevel.projectiles.add(
                        new BasicProjectile(
                            BasicProjectileSpawnLocation,
                            this.currentLevel.player));

                    this.currentLevel.player.setLastProjectileWasFiredTime(
                        System.currentTimeMillis());
                }
            }

            private void spawnSphereBossProjectiles(@NotNull SphereBoss sphereBoss)
            {
                // Spawn SphereBoss' projectiles based on:
                // 1. If SphereBoss is currently on surface
                // 2. When last projectile was fired
                if (sphereBoss.getCurrentLocation()[2] == 0
                    && Engine.this.gameLoopIterationsCounter
                       - sphereBoss.getLastVolleyIteration()
                       > SPHERE_BOSS_VOLLEY_FREQUENCY)
                {
                    int[] spawnLocationsModifiers = new int[]{
                        15 - 5,         100 + 15,   // Left tower
                        100 - 5,        30,         // Top tower
                        200 - 15 - 5,   100 + 15,   // Right tower
                        100 - 5,        200,        // Bottom tower
                        100 - 5,        100 + 15    // Center tower
                    };

                    for (int i = 0; i < 5; i++)
                        this.currentLevel.projectiles.add(
                            new BasicProjectile(
                                new int[] {
                                    sphereBoss.getCurrentLocation()[0]
                                    + spawnLocationsModifiers[i * 2],
                                    sphereBoss.getCurrentLocation()[1]
                                    + spawnLocationsModifiers[i * 2 + 1],
                                    sphereBoss.getCurrentLocation()[2]
                                },
                                sphereBoss));

                    sphereBoss.setLastVolleyIteration(
                        Engine.this.gameLoopIterationsCounter);
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
            // 'currentLevel' reference shortening
            private final LevelsProcessor.GameLevel currentLevel = Engine.this.currentLevel;
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
                Engine.this.keysInfo.keysPressedLock.lock();
                try
                {
                    for (int keyCode : Engine.this.keysInfo.keysPressed)
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
                    Engine.this.keysInfo.keysPressedLock.unlock();
                }

                return inputMoveVector;
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

            private void updatePlayerState()
            {
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

                        // Improvement: teleport player to the right position, whether
                        //  player deep out of bounds or not. Not just leave player
                        //  standing still
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
                            inputMoveVector[0] = inputMoveVector[1] = 0;
                            break;
                        }

                        case PLAYER_COLLIDED_BASIC_PROJECTILE:
                        {
                            if (!((BasicProjectile) playerCollision.collidedObject)
                                    .firedByPlayer)
                            {
                                this.currentLevel.player
                                    .receiveDamageFromBasicProjectile();
                                playerCollision.collidedObject.setShouldBeDespawned();
                            }

                            break;
                        }

                        case PLAYER_COLLIDED_SPHERE_BOSS:
                        case PLAYER_COLLIDED_SPHERE_MOB:
                        {
                            this.currentLevel.player.receiveDamageFromCollisionWithMob();

                            break;
                        }

                        default:
                            throw new IllegalArgumentException(
                                "Unknown collision received while moving Player: "
                                + playerCollision.event.toString());
                    }

                    this.currentLevel.player.modifyLocation(inputMoveVector);
                }
            }

            private void updateBasicProjectileState(BasicProjectile basicProjectile)
            {
                int[] projectileMoveVector = this.getBasicProjectileAutoMoveVector(
                    basicProjectile);
                Collision basicProjectileCollision =
                    this.collisionsProcessor.getCollision(
                        this.currentLevel, projectileMoveVector, basicProjectile);

                switch (basicProjectileCollision.event)
                {
                    case OK:
                    {
                        basicProjectile.modifyLocation(projectileMoveVector);
                        break;
                    }

                    case BASIC_PROJECTILE_IS_OUT:
                    {
                        basicProjectile.setShouldBeDespawned();
                        break;
                    }

                    case BASIC_PROJECTILE_COLLIDED_PLAYER:
                    {
                        if (basicProjectile.firedByPlayer)
                            basicProjectile.modifyLocation(projectileMoveVector);
                        else
                        {
                            this.currentLevel.player.receiveDamageFromBasicProjectile();
                            basicProjectile.setShouldBeDespawned();
                        }

                        break;
                    }

                    case BASIC_PROJECTILE_COLLIDED_SPHERE_MOB:
                    case BASIC_PROJECTILE_COLLIDED_SPHERE_BOSS:
                    {
                        if (!basicProjectile.firedByPlayer)
                            basicProjectile.modifyLocation(projectileMoveVector);
                        else
                        {
                            ((MortalObject) basicProjectileCollision.collidedObject)
                                .receiveDamageFromBasicProjectile();
                            basicProjectile.setShouldBeDespawned();
                        }

                        break;
                    }

                    default:
                        throw new IllegalArgumentException(
                            "Unknown collision received while updating state of "
                            + "BasicProjectile: "
                            + basicProjectileCollision.event.toString());
                }
            }

            private void updateSphereMobState(@NotNull SphereMob sphereMob)
            {
                Collision sphereMobCollision = this.collisionsProcessor.getCollision(
                    this.currentLevel, sphereMob.autoMovingVector, sphereMob);
                switch (sphereMobCollision.event)
                {
                    case OK:
                    {
                        if (!sphereMob.getBorderWasCrossed())
                            sphereMob.setBorderWasCrossed();
                        break;
                    }

                    case SPHERE_MOB_COLLIDED_BASIC_PROJECTILE:
                    {
                        if (((BasicProjectile) sphereMobCollision.collidedObject)
                            .firedByPlayer)
                        {
                            sphereMob.receiveDamageFromBasicProjectile();
                            sphereMobCollision.collidedObject.setShouldBeDespawned();
                            if (sphereMob.isDead())
                                sphereMob.setShouldBeDespawned();
                        }
                        break;
                    }

                    case SPHERE_MOB_COLLIDED_PLAYER:
                    {
                        ((Player) sphereMobCollision.collidedObject)
                            .receiveDamageFromCollisionWithMob();
                        break;
                    }

                    case SPHERE_MOB_IS_OUT_HORIZONTAL:
                    case SPHERE_MOB_IS_OUT_VERTICAL:
                    {
                        if (sphereMob.getBorderWasCrossed())
                            sphereMob.setShouldBeDespawned();
                        break;
                    }

                    default:
                        throw new IllegalArgumentException(
                            "Unknown collision received while updating state of "
                            + "SphereMob: "
                            + sphereMobCollision.event.toString());
                }

                sphereMob.modifyLocation(sphereMob.autoMovingVector);
                sphereMob.setCurrentLocationZ(this.getCyclicZChange());
            }

            private void updateSphereBossState(@NotNull SphereBoss sphereBoss)
            {
                Collision sphereBossCollision = this.collisionsProcessor.getCollision(
                    this.currentLevel, sphereBoss.autoMovingVector, sphereBoss);
                switch (sphereBossCollision.event)
                {
                    case OK:
                    {
                        if (sphereBoss.autoMovingVector[1] == MINIMAL_MOVING_SPEED)
                        {
                            sphereBoss.autoMovingVector[0] = SPHERE_BOSS_MOVING_SPEED;
                            sphereBoss.autoMovingVector[1] = 0;
                        }

                        sphereBoss.setCurrentLocationZ(this.getCyclicZChange());
                        break;
                    }

                    case SPHERE_BOSS_IS_GOING: break;

                    case SPHERE_BOSS_IS_OUT_HORIZONTAL:
                    {
                        sphereBoss.autoMovingVector[0] *= -1;
                        sphereBoss.setCurrentLocationZ(this.getCyclicZChange());
                        break;
                    }

                    case SPHERE_BOSS_COLLIDED_PLAYER:
                    {
                        this.currentLevel.player.receiveDamageFromCollisionWithMob();
                        break;
                    }

                    case SPHERE_BOSS_COLLIDED_BASIC_PROJECTILE:
                    {
                        if (((BasicProjectile) sphereBossCollision.collidedObject)
                            .firedByPlayer)
                        {
                            sphereBoss.receiveDamageFromBasicProjectile();
                            if (sphereBoss.isDead())
                                sphereBoss.setShouldBeDespawned();
                        }
                        break;
                    }

                    default:
                        throw new IllegalArgumentException(
                            "Unknown collision received while updating state of "
                            + "SphereBoss: "
                            + sphereBossCollision.event.toString());
                }

                sphereBoss.modifyLocation(sphereBoss.autoMovingVector);
            }

            private void updateProjectilesState()
            {
                // State updating
                for (MovableObject projectileObject : this.currentLevel.projectiles)
                {
                    switch (projectileObject.getClass().getSimpleName())
                    {
                        case "BasicProjectile":
                        {
                            this.updateBasicProjectileState(
                                (BasicProjectile) projectileObject);
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
            }

            private void updateMobsState()
            {
                for (MovableObject mob : this.currentLevel.mobs)
                {
                    switch (mob.getClass().getSimpleName())
                    {
                        case "SphereMob":
                        {
                            this.updateSphereMobState((SphereMob) mob);
                            break;
                        }

                        case "SphereBoss":
                        {
                            this.updateSphereBossState((SphereBoss) mob);
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
            }

        }

        private final GameObjectsSpawner gameObjectsSpawner = new GameObjectsSpawner();
        private final StateUpdater stateUpdater = new StateUpdater();

        private void despawnProjectiles()
        {
            Engine.this.currentLevel.projectiles.removeIf(
                GameObject::getShouldBeDespawned);
        }

        private void despawnMobs()
        {
            Engine.this.currentLevel.mobs.removeIf(GameObject::getShouldBeDespawned);
        }

        private void updateLevel()
        {
            // Improvement: check if pause is active (Latin 'P' was pressed)

            // Move player
            this.stateUpdater.updatePlayerState();

            // Despawn projectiles that were collided with player AND were collided with
            // mobs on previous updating when 'updateMobsState' happened. The latter
            // implies that Player cannot do a thing to itself with it's projectiles, so
            // 'despawnProjectiles' can be performed here, not just after
            // 'updateMobsState'
            this.despawnProjectiles();

            this.stateUpdater.updateProjectilesState();

            // Despawn projectiles that were collided
            this.despawnProjectiles();
            // Despawn mobs that were killed
            this.despawnMobs();

            this.stateUpdater.updateMobsState();

            // Despawn mobs that were killed
            this.despawnMobs();

            // Spawn all projectiles
            this.gameObjectsSpawner.spawnProjectiles();

            // Spawn all mobs
            this.gameObjectsSpawner.spawnMobs();

            if (Engine.this.currentLevel.player.isDead())
                this.gameObjectsSpawner.spawnGameOverInscription();

            if (Engine.this.currentLevel.boss.isDead())
                this.gameObjectsSpawner.spawnCompletedInscription();
        }
    }

    private final LevelsProcessor.GameLevel currentLevel;
    private final LevelUpdater levelUpdater;
    /**
     * Change main GUI here: GUI_2D or GUI_3D
     **/
    private final GUI gui = new GUI_2D();

    private long gameLoopIterationsCounter = 0L;

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
            if (!this.currentLevel.player.isDead() && !this.currentLevel.boss.isDead())
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

            // Wait a little for time alignment. It is needed for CPU power saving
            this.timeAlignment();

            this.gameLoopIterationsCounter += 1;
        }
    }
}
