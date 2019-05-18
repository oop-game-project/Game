package Game.GUI;

import Game.Engine.Engine;
import Game.Engine.LevelsProcessor.SinglePlayerLevel;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GUI_2D extends JPanel implements GUI
{
    private JFrame gameMainFrame = new JFrame();
    private final int PLAYER_TRIANGLE_SIDE_LENGTH = 30;
    private SinglePlayerLevel currentLevelState;

    public GUI_2D() { }

    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(700,700);
    }

    private void paintPlayer(Graphics graphics, int[] playerLocation)
    {
        graphics.fillPolygon(
                new int[] {
                        playerLocation[0],
                        playerLocation[0] + PLAYER_TRIANGLE_SIDE_LENGTH / 2,
                        playerLocation[0] - PLAYER_TRIANGLE_SIDE_LENGTH / 2 },
                new int[] {
                        playerLocation[1],
                        playerLocation[1] + (int)(PLAYER_TRIANGLE_SIDE_LENGTH * 0.75),
                        playerLocation[1] + (int)(PLAYER_TRIANGLE_SIDE_LENGTH * 0.75)},
                3);
    }

    @Override
    public void paintComponent(Graphics graphics)
    {
        super.paintComponent(graphics);

        this.setBackground(Color.WHITE);
        this.setForeground(Color.RED);

        this.paintPlayer(
                graphics,
                this.currentLevelState.player.currentLocation);
    }

    @Override
    public void init(Engine engineAsKeyListener)
    {
        this.gameMainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.gameMainFrame.setSize(700, 700);
        this.gameMainFrame.setResizable(false);

        this.gameMainFrame.addKeyListener(engineAsKeyListener);
        this.gameMainFrame.add(this);

        this.gameMainFrame.pack();
        this.gameMainFrame.setVisible(true);
    }

    @Override
    public void render(SinglePlayerLevel renderingLevel)
    {
        this.currentLevelState = renderingLevel;
        this.paintComponent(this.getGraphics());
    }
}
