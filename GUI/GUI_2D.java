package Game.GUI;

import Game.Engine.Engine;
import Game.Engine.LevelsProcessor.SinglePlayerLevel;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;

public class GUI_2D extends JPanel implements GUI
{
    private static JFrame gameMainFrame = new JFrame();

    public GUI_2D()
    {
    }

    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(700,700);
    }

    @Override
    public void paintComponent(Graphics graphics)
    {
        super.paintComponent(graphics);
        graphics.drawString("Hello", 40, 40);
        setBackground(Color.WHITE);
        graphics.fillRect(130, 30, 100, 80);
        graphics.drawOval(30, 130, 50, 60);
        setForeground(Color.RED);
        graphics.fillOval(130, 130, 50, 60);
        graphics.drawArc(30, 200, 40, 50, 90, 60);
        graphics.fillArc(30, 130, 40, 50, 180, 40);
    }

    private static void createAndShowGUI()
    {
        GUI_2D.gameMainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GUI_2D.gameMainFrame.setSize(700, 700);

        GUI_2D.gameMainFrame.add(new GUI_2D());
        GUI_2D.gameMainFrame.pack();
        GUI_2D.gameMainFrame.setVisible(true);
    }

    @Override
    public void init(Engine engineAsKeyListener)
    {
        SwingUtilities.invokeLater(GUI_2D::createAndShowGUI);
    }

    @Override
    public void render(SinglePlayerLevel renderingLevel)
    {
    }

    @Override
    public void dispose()
    {
        GUI_2D.gameMainFrame.dispose();
    }
}
