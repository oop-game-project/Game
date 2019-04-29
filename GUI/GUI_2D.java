package Game.GUI;

import Game.Engine.GameObjects;
import Game.Engine.LevelsProcessor.SinglePlayerLevel;

import javax.swing.JFrame;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Color;

public class GUI_2D implements GUI
{
    private JFrame gameMainFrame = new JFrame();

    private class GameCanvas extends Canvas
    {
        public void paint(Graphics graphics)
        {
            graphics.drawString("Hello",40,40);
            setBackground(Color.WHITE);
            graphics.fillRect(130, 30,100, 80);
            graphics.drawOval(30,130,50, 60);
            setForeground(Color.RED);
            graphics.fillOval(130,130,50, 60);
            graphics.drawArc(30, 200, 40,50,90,60);
            graphics.fillArc(30, 130, 40,50,180,40);
        }
    }

    public GUI_2D() { }

    public void init(SinglePlayerLevel renderingLevel)
    {
        GameCanvas gameCanvas = new GameCanvas();
        this.gameMainFrame.add(gameCanvas);
        this.gameMainFrame.setSize(700,700);
        this.gameMainFrame.setLayout(null);
        this.gameMainFrame.setVisible(true);
    }

    public void render(SinglePlayerLevel renderingLevel) { }

    public void dispose() { }
}