package Game.GUI;

import Game.Engine.GameObjects.*; // TODO: get rid of '*'
import Game.Engine.LevelsProcessor.SinglePlayerLevel;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.*; // TODO: get rid of '*'
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import java.util.List;

public class GUI_2D extends JPanel implements GUI, KeyListener
{
    private final JFrame gameMainFrame = new JFrame();
    private SinglePlayerLevel currentLevelState;

    public GUI_2D() { }

    public void init(KeyListener engine)
    {
        this.gameMainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.gameMainFrame.setSize(700, 700);
        this.gameMainFrame.setResizable(false);

        this.gameMainFrame.addKeyListener(engine);
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
                new WindowEvent(
                    this.gameMainFrame,
                    WindowEvent.WINDOW_CLOSING));
    }

//
//  Paint components section
//


    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(700,700);
    }

    private void paintPlayer(Graphics graphics)
    {
        int[] playerLocation = this.currentLevelState.player.currentLocation;

        graphics.setColor(Color.BLUE);
        graphics.fillPolygon(
                new int[] {
                    playerLocation[0],
                    playerLocation[0]
                    + PaintingConst.PLAYER_TRIANGLE_SIDE_LENGTH / 2,
                    playerLocation[0]
                    - PaintingConst.PLAYER_TRIANGLE_SIDE_LENGTH / 2 },
                new int[] {
                    playerLocation[1],
                    playerLocation[1]
                    + PaintingConst.PLAYER_TRIANGLE_SIDE_LENGTH,
                    playerLocation[1]
                    + PaintingConst.PLAYER_TRIANGLE_SIDE_LENGTH },
                3);
    }

    private void paintSphereMob(Graphics graphics, SphereMob sphereMob)
    {
        //  TODO : Paint image, not just circle!
    }

    private void paintBasicProjectile(
        Graphics graphics,
        BasicProjectile projectile)
    {
        graphics.setColor(Color.YELLOW);
        graphics.fillRect(
            projectile.currentLocation[0],
            projectile.currentLocation[1],
            PaintingConst.BASIC_PROJECTILE_SIDE_LENGTH,
            PaintingConst.BASIC_PROJECTILE_SIDE_LENGTH);
    }

    private void paintMovableObjects(Graphics graphics)
    {
        //  Paint mobs
        for (MovableObject mobObject : this.currentLevelState.mobs)
        {
            if (mobObject instanceof SphereMob)
                paintSphereMob(graphics, (SphereMob)mobObject);
        }

        //  Paint projectiles (after mobs for better debugging. It will be
        //  easier to see collisions, when projectiles paints above mods)
        for (MovableObject projectile : this.currentLevelState.projectiles)
        {
            if (projectile instanceof BasicProjectile)
                paintBasicProjectile(graphics, (BasicProjectile)projectile);
        }
    }

    @Override
    public void paintComponent(Graphics graphics)
    {
        super.paintComponent(graphics);

        //  Paint movable objects
        this.paintMovableObjects(graphics);

        //  Paint player
        this.paintPlayer(graphics);

        //      Paint interface objects (Last painting for overall overlapping
        //  by interface objects)
    }

//
//  Main render section
//

    public void render(SinglePlayerLevel renderingLevel)
    {
        this.currentLevelState = renderingLevel;
        //  Just say AWT thread to repaint game GUI
        this.repaint();
    }
}
