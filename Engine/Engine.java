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
//  Final engine's objects
    private final SinglePlayerLevel currentLevel;
    private final GUI gui = new GUI_2D();  // Change main GUI here: GUI_2D or GUI_3D
    private final Launcher launcher;
    private final Thread gameLoopThread = new Thread(this::gameLoop);
    private final CollisionsProcessor collisionsProcessor;

    private final int PLAYER_MOVE_SPEED = 5;

    private int[] inputMoveVector = new int[] { 0, 0, 0 };
    private ReentrantLock inputMoveLock = new ReentrantLock();

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

    private HashSet<Integer> keysPressed = new HashSet<>();

    private int[] getKeyMoveModifier(int keyCode)
    {
        switch (keyCode)
        {
            case KeyEvent.VK_RIGHT:
                return new int[] { PLAYER_MOVE_SPEED, 0, 0 };

            case KeyEvent.VK_DOWN:
                return new int[] { 0, PLAYER_MOVE_SPEED, 0 };

            case KeyEvent.VK_LEFT:
                return new int[] { -PLAYER_MOVE_SPEED, 0, 0 };

            case KeyEvent.VK_UP:
                return new int[] { 0, -PLAYER_MOVE_SPEED, 0 };

            default:
                return null;
        }
    }

    public void modifyTernaryArray(int[] target, int[] modifier)
    {
        target[0] += modifier[0];
        target[1] += modifier[1];
        target[2] += modifier[2];
    }

    public void keyTyped(KeyEvent keyEvent) { }

    public void keyPressed(KeyEvent keyEvent)
    {
        if (this.keysPressed.contains(keyEvent.getKeyCode()))
            return;

        inputMoveLock.lock();
        try
        {
            this.keysPressed.add(keyEvent.getKeyCode());

            int[] moveModifier = this.getKeyMoveModifier(keyEvent.getKeyCode());
            if (moveModifier != null)
                this.modifyTernaryArray(this.inputMoveVector, moveModifier);
        }
        finally
        {
            inputMoveLock.unlock();
        }
    }

    public void keyReleased(KeyEvent keyEvent)
    {
        if (!this.keysPressed.contains(keyEvent.getKeyCode()))
            return;

        this.inputMoveLock.lock();
        try
        {
            int[] moveModifier = this.getKeyMoveModifier(keyEvent.getKeyCode());
            if (moveModifier != null)
            {
                moveModifier[0] *= -1;
                moveModifier[1] *= -1;
                moveModifier[2] *= -1;
                this.modifyTernaryArray(this.inputMoveVector, moveModifier);
            }
        }
        finally
        {
            this.inputMoveLock.unlock();
        }

        this.keysPressed.remove(keyEvent.getKeyCode());
    }

//
//  WindowAdapter overrides
//

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

    private void updatePlayerPosition()
    {
        this.inputMoveLock.lock();
        try
        {
            if (this.inputMoveVector[0] != 0
                || this.inputMoveVector[1] != 0
                || this.inputMoveVector[2] != 0)
            {
                Collision[] playerCollisions = collisionsProcessor.getCollision(
                    this.currentLevel, this.inputMoveVector, this.currentLevel.player);
                if (playerCollisions[0].event == GameEvent.OK)
                {
                    this.currentLevel.player.modifyLocation(inputMoveVector);
                    this.renderNeeded = true;
                }
                else if (playerCollisions[0].event == GameEvent.OUT_OF_BOUNDS)
                {
                    int[] modifiedInputMoveVector = this.inputMoveVector.clone();
                    if (this.collisionsProcessor.playerOutOfVerticalBorders(
                        this.inputMoveVector,
                        this.currentLevel.player))
                        modifiedInputMoveVector[1] = 0;  // TODO : remove gap
                    if (this.collisionsProcessor.playerOutOfHorizontalBorders(
                        this.inputMoveVector,
                        this.currentLevel.player))
                        modifiedInputMoveVector[0] = 0;  // TODO : remove gap

                    if (modifiedInputMoveVector[0] != 0
                        || modifiedInputMoveVector[1] != 0
                        || modifiedInputMoveVector[2] != 0)
                    {
                        this.currentLevel.player.modifyLocation(modifiedInputMoveVector);
                        this.renderNeeded = true;
                    }
                }
                else
                    assert false;
            }
        }
        finally
        {
            this.inputMoveLock.unlock();
        }
    }

    private void updateLevel()
    {
        updatePlayerPosition();
    }

//
//  Main game loop section
//

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