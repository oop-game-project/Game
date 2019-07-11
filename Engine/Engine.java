package Game.Engine;

import Game.GUI.GUI;
import Game.GUI.GUI_2D;
import Game.Engine.LevelsProcessor.SinglePlayerLevel;
import Game.Engine.CollisionsProcessor.*;
import Game.Launcher;

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
    private final SinglePlayerLevel currentLevel;

    private HashSet<Integer> keysPressed = new HashSet<>();
    private ReentrantLock keysPressedLock = new ReentrantLock();

    private boolean closeGame = false;
    private ReentrantLock closeGameLock = new ReentrantLock();

    private boolean renderNeeded = false;

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
//  Level update section
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

        if (inputMoveVector[0] != 0 || inputMoveVector[1] != 0 || inputMoveVector[2] != 0)
        {
            Collision[] playerCollisions = collisionsProcessor.getCollision(
                this.currentLevel, inputMoveVector, this.currentLevel.player);
            switch (playerCollisions[0].event)
            {
                case OK:
                {
                    this.currentLevel.player.modifyLocation(inputMoveVector);
                    this.renderNeeded = true;

                    break;
                }

                case OUT_OF_BOUNDS:
                {
                /*  Would Be Better:
                        Teleport player to the right position, whether player deep out of
                    bounds or not. Not just leave player standing still */
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
                    {
                        this.currentLevel.player.modifyLocation(modifiedInputMoveVector);
                        this.renderNeeded = true;
                    }

                    break;
                }
                default:
                    throw new IllegalArgumentException("Unknown collision gotten");
            }
        }
    }

    private void updateLevel()
    {
        updatePlayerPosition();

        // Move all MovableObject elements

        // Spawn player's projectile

        // Spawn all projectiles
    }

//
//  Main game loop section
//

    private final Thread gameLoopThread = new Thread(this::gameLoop);
    private final GUI gui = new GUI_2D();  // Change main GUI here: GUI_2D or GUI_3D

    public void timeAlignment()
    {
        try
        {
            Thread.sleep(1000 / 60);
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
            this.updateLevel();

            this.closeGameLock.lock();
            try
            {
                if (!this.closeGame && this.renderNeeded)
                    SwingUtilities.invokeAndWait(() -> gui.render(currentLevel));
            }
            catch (InterruptedException | InvocationTargetException exception)
            {
                exception.printStackTrace();
            }
            finally
            {
                this.closeGameLock.unlock();
                this.renderNeeded = false;
            }

            this.timeAlignment();
        }
    }
}