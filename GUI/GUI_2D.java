package Game.GUI;

import Game.Engine.Engine;
import Game.Engine.GameObjects.PaintingConst;
import Game.Engine.LevelsProcessor.SinglePlayerLevel;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.EventListener;

public class GUI_2D extends JPanel implements GUI, KeyListener
{
    private final JFrame gameMainFrame = new JFrame();
    private SinglePlayerLevel currentLevelState;

    public GUI_2D() { }

    @Override
    public void init(EventListener engine)
    {
        this.gameMainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.gameMainFrame.setSize(700, 700);
        this.gameMainFrame.setResizable(false);

        this.gameMainFrame.addKeyListener((KeyListener)engine);
        this.gameMainFrame.addKeyListener(this);
        this.gameMainFrame.addWindowListener((WindowListener)engine);
        this.gameMainFrame.add(this);

        this.gameMainFrame.pack();
        this.gameMainFrame.setVisible(true);

        this.setBackground(Color.GRAY);
    }

//
//  KeyListener implementations
//

    public void keyTyped(KeyEvent e) { }
    public void keyReleased(KeyEvent e) { }

    public void keyPressed(KeyEvent keyEvent)
    {
        if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE)
            this.gameMainFrame.dispatchEvent(
                new WindowEvent(this.gameMainFrame, WindowEvent.WINDOW_CLOSING));
    }

//
//  Paint components section
//

    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(700,700);
    }

    private void paintPlayer(Graphics graphics, int[] playerLocation)
    {
        graphics.setColor(Color.BLUE);

        graphics.fillPolygon(
                new int[] {
                    playerLocation[0],
                    playerLocation[0] + PaintingConst.PLAYER_TRIANGLE_SIDE_LENGTH / 2,
                    playerLocation[0] - PaintingConst.PLAYER_TRIANGLE_SIDE_LENGTH / 2 },
                new int[] {
                    playerLocation[1],
                    playerLocation[1] + PaintingConst.PLAYER_TRIANGLE_SIDE_LENGTH,
                    playerLocation[1] + PaintingConst.PLAYER_TRIANGLE_SIDE_LENGTH },
                3);
    }

    @Override
    public void paintComponent(Graphics graphics)
    {
        super.paintComponent(graphics);

        this.paintPlayer(
                graphics,
                this.currentLevelState.player.currentLocation);
    }

//
//  Render main section
//

    @Override
    public void render(SinglePlayerLevel renderingLevel)
    {
        this.currentLevelState = renderingLevel;
        this.paintComponent(this.getGraphics());
    }
}
