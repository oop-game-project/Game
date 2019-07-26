package Game.Engine;

import Game.GUI.GUI;
import Game.GUI.GUI_2D;
import Game.Engine.LevelsProcessor.SinglePlayerLevel;
import Game.Engine.CollisionsProcessor.*;
import Game.Launcher;
import Game.Engine.GameObjects.*;

import jdk.jshell.spi.ExecutionControl.NotImplementedException;

import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.locks.ReentrantLock;

public class Engine extends WindowAdapter implements KeyListener
{
    private final SinglePlayerLevel currentLevel;

    private HashSet<Integer> keysPressed = new HashSet<>();
    /**
     * Is this lock really needed?
     **/
    private ReentrantLock keysPressedLock = new ReentrantLock();

    /**
     * Needed for toggling things. Keycodes from this set should be removed
     * when used
     **/
    private HashSet<Integer> keysReleased = new HashSet<>();
    /**
     * Lock is needed because of "keysReleased" modifications in multiple places
     */
    private ReentrantLock keysReleasedLock = new ReentrantLock();

    private boolean closeGame = false;
    /**
     * Lock for rendering safety. If rendering starts when game is closing,
     * then GUI thread breaks
     */
    private ReentrantLock closeGameLock = new ReentrantLock();

    public Engine(
        SinglePlayerLevel inputLevel,
        Runnable setLauncherVisibleInput)
    {
        this.setLauncherVisible = setLauncherVisibleInput;
        this.currentLevel = inputLevel;

        this.collisionsProcessor =
            new CollisionsProcessor(this.currentLevel.gameFieldSize);
    }

//
// KeyListener implementation
//

    public void keyTyped(KeyEvent keyEvent) { }

    public void keyPressed(KeyEvent keyEvent)
    {
        this.keysPressedLock.lock();
        try
        {
            this.keysPressed.add(keyEvent.getKeyCode());
        }
        finally
        {
            this.keysPressedLock.unlock();
        }
    }

    public void keyReleased(KeyEvent keyEvent)
    {
        this.keysPressedLock.lock();
        this.keysReleasedLock.lock();
        try
        {
            this.keysPressed.remove(keyEvent.getKeyCode());

            this.keysReleased.add(keyEvent.getKeyCode());
        }
        finally
        {
            this.keysPressedLock.unlock();
            this.keysReleasedLock.unlock();
        }
    }

//
// WindowAdapter overrides
//

    private final Runnable setLauncherVisible;

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
// Auto moving functions section.
//

    // Constants here do not participate in process of level making. For
    // example, this is why "BASIC_PROJECTILE_MOVING_SPEED" is not in
    // "LevelsProcessor"

    private final int BASIC_PROJECTILE_MOVING_SPEED = 10;

    private int[] getBasicProjectileMoveVector(
        BasicProjectile basicProjectile)
    {
        if (basicProjectile.firedByPlayer)
            return new int[] { 0, -BASIC_PROJECTILE_MOVING_SPEED, 0 };
        else
        {
            try
            {
                throw new NotImplementedException("");
            }
            catch (NotImplementedException exception)
            {
                exception.printStackTrace();
            }

            return null;
        }

        // TODO: Fired not by player check
    }

    /**
     * Based on when SphereMob was spawned, this method returns if it is needed
     * for engine to toggle third coordinate of SphereMob
     **/
    private int[] getSphereMobZChange()
    {
        // TODO: Every N seconds SphereMob instance's third coordinate toggles
        //  cyclically in order: 0 -> -1 -> 0 -> 1 -> 0
        return null;
    }

//
// Level update main section
//

    /**
     * Pixels per level update
     **/
    private final int PLAYER_MOVE_SPEED = 5;

    /**
     * Fire per this amount of milliseconds
     **/
    private final int PLAYER_FIRING_FREQUENCY = 50;

    private boolean playerIsFiring = false;

    private final CollisionsProcessor collisionsProcessor;
    private final GameObjects gameObjects = new GameObjects();

    private int[] getInputMoveVector()
    {
        int[] inputMoveVector = new int[] { 0, 0, 0 };
        this.keysPressedLock.lock();
        try
        {
            for (int keyCode : keysPressed)
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
            this.keysPressedLock.unlock();
        }

        return inputMoveVector;
    }

    private void updatePlayerState()
    {
        // TODO: check hitpoints here (make method for that)

        // TODO: All code below to another method
        int[] inputMoveVector = this.getInputMoveVector();

        if (inputMoveVector[0] != 0
            || inputMoveVector[1] != 0
            || inputMoveVector[2] != 0)
        {
            // Collisions check
            Collision playerCollision = collisionsProcessor.getCollision(
                this.currentLevel, inputMoveVector, this.currentLevel.player);
            switch (playerCollision.event)
            {
                case OK:
                    break;

                // [Would Be Better]
                // Teleport player to the right position, whether
                // player deep out of bounds or not. Not just leave
                // player standing still
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


    /**
     * Despawn or move ONLY projectiles here
     **/
    private void updateProjectilesState()
    {
        ArrayList<MovableObject> projectilesForDespawning = new ArrayList<>();
        for (MovableObject projectileObject : this.currentLevel.projectiles)
        {
            if (projectileObject instanceof BasicProjectile)
            {
                int[] projectileMoveVector = this.getBasicProjectileMoveVector(
                    (BasicProjectile)projectileObject);
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
        }

        // Despawning
        this.currentLevel.projectiles.removeAll(projectilesForDespawning);
    }

    private void updateMobsState()
    {
        for (MovableObject mobObject : this.currentLevel.mobs)
        {
            if (mobObject instanceof SphereMob)
                mobObject.modifyLocation(
                    ((SphereMob) mobObject).autoMovingVector);

            // TODO: Collisions check
        }

        // TODO: Despawning
    }

    private void spawnProjectiles()
    {
        this.keysReleasedLock.lock();
        try
        {
            // Key is removing from "keysReleased" when used
            if (this.keysReleased.contains(KeyEvent.VK_Z))
            {
                this.playerIsFiring = !this.playerIsFiring;
                this.keysReleased.remove(KeyEvent.VK_Z);
            }
        }
        finally
        {
            this.keysReleasedLock.unlock();
        }

        // Spawn player's projectile based on when last projectile was fired
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
        // TODO : Spawn mobs' projectiles
    }

    private void updateLevel()
    {
        // [Would Be Better]
        // Check if pause is active (Latin 'P' was pressed)

        // Move player
        updatePlayerState();

        // Despawn or move projectiles
        updateProjectilesState();

        // Despawn or move mobs
        updateMobsState();

        // Spawn all projectiles
        spawnProjectiles();
    }

//
// Game loop main section
//

    private final Thread gameLoopThread = new Thread(this::gameLoop);
    /**
     * Change main GUI here: GUI_2D or GUI_3D
     **/
    private final GUI gui = new GUI_2D();

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

    public void runGameLoop()
    {
        this.gui.init(this, this.currentLevel);

        this.gameLoopThread.start();
    }

    public void gameLoop()
    {
        while (!this.closeGame)
        {
            // Update
            this.updateLevel();

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
        }
    }
}
