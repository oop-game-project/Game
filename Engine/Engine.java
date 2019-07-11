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

    private enum PlayerAction
    {
        Unknown,
        Fire,
        Move,
        OpenMenu
    }

    private PlayerAction getPlayerActionFromKey(int keyCode)
    {
        switch (keyCode)
        {
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_UP:
                return PlayerAction.Move;

            case KeyEvent.VK_Z:
                return PlayerAction.Fire;

            default:
                return PlayerAction.Unknown;
        }
    }

    private int[] getMoveModifierFromKey(int keyCode)
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
                throw new IllegalArgumentException("Incorrect input key code");
        }
    }

    public void modifyThreeElementArray(int[] target, int[] modifier)
    {
        target[0] += modifier[0];
        target[1] += modifier[1];
        target[2] += modifier[2];
    }

    public void keyTyped(KeyEvent keyEvent) { }

    private void addModifierToMoveVector(int keyCode)
    {
        this.keysPressed.add(keyCode);

        this.modifyThreeElementArray(
            this.inputMoveVector,
            this.getMoveModifierFromKey(keyCode));
    }

    public void keyPressed(KeyEvent keyEvent)
    {
        if (this.keysPressed.contains(keyEvent.getKeyCode()))
            return;

        switch (this.getPlayerActionFromKey(keyEvent.getKeyCode()))
        {
            case Move:
            {
                inputMoveLock.lock();
                try
                {
                    addModifierToMoveVector(keyEvent.getKeyCode());
                }
                finally
                {
                    inputMoveLock.unlock();
                }

                break;
            }
            case Fire: // TODO
                break;

            case OpenMenu: // TODO
                break;

            case Unknown:
                break;
        }
    }

    private void subtractModifierFromMoveVector(int keyCode)
    {
        int[] moveModifier = this.getMoveModifierFromKey(keyCode);
        moveModifier[0] *= -1;
        moveModifier[1] *= -1;
        moveModifier[2] *= -1;
        this.modifyThreeElementArray(this.inputMoveVector, moveModifier);

        this.keysPressed.remove(keyCode);
    }

    public void keyReleased(KeyEvent keyEvent)
    {
        if (!this.keysPressed.contains(keyEvent.getKeyCode()))
            return;

        switch (this.getPlayerActionFromKey(keyEvent.getKeyCode()))
        {
            case Move:
            {
                this.inputMoveLock.lock();
                try
                {
                    subtractModifierFromMoveVector(keyEvent.getKeyCode());
                }
                finally
                {
                    this.inputMoveLock.unlock();
                }
            }

            case Fire:
            case OpenMenu:
            case Unknown:
                break;
        }
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
                    /*  Would Be Better:
                            Teleport player to the right position, whether player deep out of
                        bounds or not. Not just leave player standing still */
                    int[] modifiedInputMoveVector = this.inputMoveVector.clone();
                    if (this.collisionsProcessor.playerOutOfVerticalBorders(
                        this.inputMoveVector,
                        this.currentLevel.player))
                        modifiedInputMoveVector[1] = 0;
                    if (this.collisionsProcessor.playerOutOfHorizontalBorders(
                        this.inputMoveVector,
                        this.currentLevel.player))
                        modifiedInputMoveVector[0] = 0;

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