package Game.Engine;

import Game.GUI.GUI;
import Game.GUI.GUI_2D;
import Game.Engine.LevelsProcessor.SinglePlayerLevel;
import Game.Launcher;
// TODO: get rid of '*'
import javax.swing.*;

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
    private final GUI gui;
    private final Launcher launcher;
    private final Thread gameLoopThread;

    private int[] inputMoveVector = new int[] { 0, 0, 0 };
    private ReentrantLock inputMoveLock = new ReentrantLock();

    private boolean closeGame = false;
    private ReentrantLock closeGameLock = new ReentrantLock();

    private HashSet<Integer> keysPressed = new HashSet<>();

    // TODO : constructor with "String levelFileName"
    public Engine(SinglePlayerLevel inputLevel, Launcher inputLauncher)
    {
        this.gui = new GUI_2D();  // Change main GUI here: GUI_2D or GUI_3D
        this.launcher = inputLauncher;
        this.currentLevel = inputLevel;
        this.gameLoopThread = new Thread(this::gameLoop);
    }

    private int[] getKeyMoveModifier(int keyCode)
    {
        switch (keyCode)
        {
            case KeyEvent.VK_RIGHT:
                return new int[] { 5, 0, 0 };

            case KeyEvent.VK_DOWN:
                return new int[] { 0, 5, 0 };

            case KeyEvent.VK_LEFT:
                return new int[] { -5, 0, 0 };

            case KeyEvent.VK_UP:
                return new int[] { 0, -5, 0 };

            default:
                return null;
        }
    }

    public void modifyArray(int[] target, int[] modifier)
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
                this.modifyArray(this.inputMoveVector, moveModifier);
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
                this.modifyArray(this.inputMoveVector, moveModifier);
            }
        }
        finally
        {
            this.inputMoveLock.unlock();
        }

        this.keysPressed.remove(keyEvent.getKeyCode());
    }

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

    public void timeAlignment()  // TODO
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

    private void updateLevel()
    {
        this.inputMoveLock.lock();
        try
        {
            if (this.inputMoveVector[0] != 0
                || this.inputMoveVector[1] != 0
                || this.inputMoveVector[2] != 0)
                this.currentLevel.player.modifyLocation(inputMoveVector);
        }
        finally
        {
            this.inputMoveLock.unlock();
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
                if (!this.closeGame)
                    SwingUtilities.invokeAndWait(() -> gui.render(currentLevel));
            }
            catch (InterruptedException | InvocationTargetException exception)
            {
                exception.printStackTrace();
            }
            finally
            {
                this.closeGameLock.unlock();
            }

            this.timeAlignment();
        }
    }
}