package Game.Engine;
// TODO: get rid of '*'
import Game.GUI.GUI;
import Game.GUI.GUI_2D;
import Game.Engine.LevelsProcessor.SinglePlayerLevel;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.locks.ReentrantLock;

public class Engine extends KeyAdapter
{

    private SinglePlayerLevel currentLevel;
    private GUI gui;

    private int[] inputMoveVector;
    private ReentrantLock inputMoveLock = new ReentrantLock();

    private boolean closeGame = false;
    private ReentrantLock closeGameLock = new ReentrantLock();

    // TODO : constructor with "String levelFileName"
    public Engine(SinglePlayerLevel inputLevel) {
        this.currentLevel = inputLevel;
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        inputMoveLock.lock();
        try
        {

            switch (e.getKeyCode())
            {
                case KeyEvent.VK_RIGHT:
                {
                    this.inputMoveVector = new int[] { 1, 0, 0 };
                    break;
                }

                case KeyEvent.VK_DOWN:
                {
                    this.inputMoveVector = new int[] { 0, 1, 0 };
                    break;
                }

                case KeyEvent.VK_LEFT:
                {
                    this.inputMoveVector = new int[] { -1, 0, 0 };
                    break;
                }

                case KeyEvent.VK_UP:
                {
                    this.inputMoveVector = new int[] { 0, -1, 0 };
                    break;
                }

                case KeyEvent.VK_Q:
                {
                    closeGameLock.lock();
                    this.closeGame = true;
                    closeGameLock.unlock();
                    break;
                }
            }
        }
        finally
        {
            inputMoveLock.unlock();
        }
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

    private void updateLevel()  // TODO: Зависимость от процессора коллизий
    {
        this.inputMoveLock.lock();
        try
        {
            if (this.inputMoveVector != null)
            {
                this.currentLevel.player.modifyLocation(inputMoveVector);
                this.inputMoveVector = null;
            }
        }
        finally
        {
            this.inputMoveLock.unlock();
        }
    }

    public void runGameLoop()  // TODO: check window closing
    {
        this.gui = new GUI_2D();  // Change main GUI here: GUI_2D or GUI_3D

        this.gui.init(this);
        this.gui.render(this.currentLevel);

        new Thread(this::gameLoop).start();
    }

    public void gameLoop()
    {
        while (!this.closeGame)
        {
            this.updateLevel();

            try { SwingUtilities.invokeAndWait(() -> gui.render(currentLevel)); }
            catch (InterruptedException|InvocationTargetException exception)
            {
                exception.printStackTrace();
            }

            this.timeAlignment();
        }
    }
}