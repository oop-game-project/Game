package Game.Engine;

import Game.GUI.GUI_2D;
import Game.GUI.GUI;
import Game.Engine.LevelsProcessor.SinglePlayerLevel;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.locks.ReentrantLock;

//import java.awt.event.

public class Engine implements KeyListener
{
    private SinglePlayerLevel currentLevel;
    private GeometryVector inputMove;
    private ReentrantLock inputMoveLock = new ReentrantLock();
    private boolean closeGame = false;
    private ReentrantLock closeGameLock = new ReentrantLock();


    // TODO : constructor with "String levelFileName"
    public Engine(SinglePlayerLevel inputLevel) {
        this.currentLevel = inputLevel;
    }

    class GeometryVector {
        private int X;
        private int Y;
        private int Z;

        GeometryVector(int inputX, int inputY, int inputZ) {
            this.X = inputX;
            this.Y = inputY;
            this.Z = inputZ;
        }

        int[] getCoordinates() {
            return new int[]{this.X, this.Y, this.Z};
        }

        int getX() {
            return this.X;
        }

        int getY() {
            return this.Y;
        }

        int getZ() {
            return this.Z;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e)
    {
        System.out.println(123);
        try
        {
            inputMoveLock.lock();

            switch (e.getKeyCode())
            {
                case KeyEvent.VK_RIGHT:
                {
                    inputMove = new GeometryVector(1, 0, 0);
                    break;
                }

                case KeyEvent.VK_DOWN:
                {
                    inputMove = new GeometryVector(0, 1, 0);
                    break;
                }

                case KeyEvent.VK_LEFT:
                {
                    inputMove = new GeometryVector(-1, 0, 0);
                    break;
                }

                case KeyEvent.VK_UP:
                {
                    inputMove = new GeometryVector(0, -1, 0);
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

    @Override
    public void keyReleased(KeyEvent e) { }

    private void updateLevel()
    {
        try
        {
            this.inputMoveLock.lock();
            if (this.inputMove != null)
            {
                this.currentLevel.getPlayerOne().modifyCurrentLocation(this.inputMove);
                this.inputMove = null;
            }
        }
        finally
        {
            this.inputMoveLock.unlock();
        }
    }

    public void runGameLoop()
    {
        GUI_2D gui = new GUI_2D();  // Change main GUI here: GUI_2D or GUI_3D

        gui.init();
        gui.addKeyListener(this);
        gui.render(this.currentLevel);

        while (!this.closeGame)
        {
            this.updateLevel();

            gui.render(currentLevel);

            try
            {
                Thread.sleep(1000 / 60);
            }
            catch (InterruptedException exception)
            {
                exception.printStackTrace();
            }
        }

        gui.dispose();
    }
}