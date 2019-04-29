package Game.Engine;

import Game.GUI.GUI_2D;
import Game.GUI.GUI;
import Game.Engine.LevelsProcessor.SinglePlayerLevel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.locks.ReentrantLock;

//import java.awt.event.

public class Engine implements KeyListener
{
    private SinglePlayerLevel currentLevel;
    private GeometryVector inputMove;
    private final ReentrantLock inputMoveLock = new ReentrantLock();

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
            }
        }
        finally
        {
            inputMoveLock.unlock();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) { }

    public void runGameLoop()
    {
        GUI gui = new GUI_2D();  // Change main GUI here: GUI_2D or GUI_3D

        gui.init(this);

//        while (true)
//        {
//            this.input();
//            this.update();
//            gui.render(currentLevel);
//        }
        try
        {
            Thread.sleep(1000 * 20);
        }
        catch (InterruptedException exception)
        {

        }

        gui.dispose();
    }
}