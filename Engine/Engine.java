package Game.Engine;

import Game.GUI.GUI;
import Game.GUI.GUI_2D;
import Game.Engine.LevelsProcessor.SinglePlayerLevel;
import Game.Engine.CollisionsProcessor.*;
import Game.Launcher;
import Game.Engine.GameObjects.*;

import javax.swing.*; // TODO: get rid of '*'

import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;

import java.lang.reflect.InvocationTargetException;

import java.util.HashSet;
import java.util.concurrent.locks.ReentrantLock;

public class Engine extends WindowAdapter implements KeyListener
{
    private final GameObjects gameObjects = new GameObjects();
    private final SinglePlayerLevel currentLevel;

    private HashSet<Integer> keysPressed = new HashSet<>();
    private ReentrantLock keysPressedLock = new ReentrantLock();

    private boolean closeGame = false;
    private ReentrantLock closeGameLock = new ReentrantLock();

    public Engine(SinglePlayerLevel inputLevel, Launcher inputLauncher)
    {
        this.launcher = inputLauncher;
        this.currentLevel = inputLevel;

        this.collisionsProcessor = new CollisionsProcessor(this.currentLevel.gameFieldSize);
    }

//
//  KeyListener implementation
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
        try
        {
            this.keysPressed.remove(keyEvent.getKeyCode());
        }
        finally
        {
            this.keysPressedLock.unlock();
        }
    }

//
//  WindowAdapter overrides
//

    private final Launcher launcher;

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

        this.launcher.setVisible(true);
    }

//
//  Auto moving functions section
//
    //  final int SCREEN_MOVING_SPEED
    private final int BASIC_PROJECTILE_MOVING_SPEED = 2;

    private void moveObjectForward(MovableObject movableObject)
    {

    }

//
//  Level update main section
//

    private final int PLAYER_MOVE_SPEED = 5;
    private final CollisionsProcessor collisionsProcessor;

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

    private void updatePlayerPosition()
    {
        int[] inputMoveVector = this.getInputMoveVector();

        if (inputMoveVector[0] != 0
            || inputMoveVector[1] != 0
            || inputMoveVector[2] != 0)
        {
            Collision[] playerCollisions = collisionsProcessor.getCollision(
                this.currentLevel, inputMoveVector, this.currentLevel.player);
            switch (playerCollisions[0].event)
            {
                case OK:
                {
                    this.currentLevel.player.modifyLocation(inputMoveVector);
                    break;
                }

                case OUT_OF_BOUNDS:
                {
                    //  Would Be Better:
                    //      Teleport player to the right position, whether
                    //  player deep out of bounds or not. Not just leave
                    //  player standing still
                    int[] modifiedInputMoveVector = inputMoveVector.clone();
                    if (this.collisionsProcessor.playerOutOfVerticalBorders(
                        inputMoveVector,
                        this.currentLevel.player))
                        modifiedInputMoveVector[1] = 0;
                    if (this.collisionsProcessor.playerOutOfHorizontalBorders(
                        inputMoveVector,
                        this.currentLevel.player))
                        modifiedInputMoveVector[0] = 0;

                    if (modifiedInputMoveVector[0] != 0
                        || modifiedInputMoveVector[1] != 0
                        || modifiedInputMoveVector[2] != 0)
                        this.currentLevel.player.modifyLocation(
                            modifiedInputMoveVector);

                    break;
                }
                default:
                    throw new IllegalArgumentException("Unknown collision gotten");
            }
        }
    }

    private void updateLevel()
    {
        //  Check if pause is active (Latin 'P' was pressed)

        //  Move player
        updatePlayerPosition();

        //  Move all MovableObject elements in the array

        //  Spawn player's projectile

        //  Spawn mobs' projectiles
    }

//
//  Game loop main section
//

    private final Thread gameLoopThread = new Thread(this::gameLoop);
    //  Change main GUI here: GUI_2D or GUI_3D
    private final GUI gui = new GUI_2D();

    /**
     *  Как работает выравнивание по времени в этой игре.
     *
     *      Игра работает в режиме 60 итераций игрового цикла (обновление
     *  И рендер уровня в одной итерации) в секунду.
     *      По сути, секунда разбита на 60 частей. Выравнивание происходит
     *  таким образом, что в начале каждой 1\60 части секунды должна начинаться
     *  КАЖДАЯ итерация игрового цикла. НЕТ гарантии, что при таком подходе
     *  не будет потеряна одна из 1\60-ой частей секунды
     *      Таким образом, каждое обновление уровня происходит с рассчетом
     *  ТОЛЬКО на текущую 1/60 часть секунды. Это позволяет избавиться от
     *  дробных величин при модификации позиции движущихся объектов.
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
        this.gui.init(this);
        this.gui.render(this.currentLevel);

        this.gameLoopThread.start();
    }

    public void gameLoop()
    {
        while (!this.closeGame)
        {
            //  Update
            this.updateLevel();

            //  Render
            this.closeGameLock.lock();
            try
            {
                if (!this.closeGame)
                    SwingUtilities.invokeAndWait(
                        () -> gui.render(currentLevel));
            }
            catch (InterruptedException | InvocationTargetException exception)
            {
                exception.printStackTrace();
            }
            finally
            {
                this.closeGameLock.unlock();
            }

            // Wait a little for time alignment
            this.timeAlignment();
        }
    }
}