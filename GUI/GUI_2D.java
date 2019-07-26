package Game.GUI;

import Game.Engine.GameObjects.*;
import Game.Engine.LevelsProcessor.SinglePlayerLevel;
import static Game.Engine.GameObjects.PaintingConst.*;

import java.awt.Image;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import java.io.File;
import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JPanel;
import javax.swing.JFrame;

import javax.imageio.ImageIO;


public class GUI_2D extends JPanel implements GUI, KeyListener
{
    private final JFrame gameMainFrame = new JFrame();
    private SinglePlayerLevel renderingLevel;

    private boolean levelRenderingIsNeeded = false;

    public GUI_2D() { }

    private void loadImages()
    {
        try
        {
            // TODO: Change on release. Execution dir must be "Game", not
            //  "game-project"
            Path pathToSprites = Paths.get(
                System.getProperty("user.dir") + "\\src\\Game\\Sprites\\");

            File sphereMobDefaultImage = new File(
                pathToSprites.toString() + "\\SphereMob-Default.png");
            this.SPHERE_MOB_DEFAULT = ImageIO
                .read(sphereMobDefaultImage)
                .getScaledInstance(
                    SPHERE_MOB_DIAMETER,
                    SPHERE_MOB_DIAMETER,
                    Image.SCALE_SMOOTH);

            File sphereMobAboveImage = new File(
                pathToSprites.toString() + "\\SphereMob-Above.png");
            this.SPHERE_MOB_ABOVE = ImageIO
                .read(sphereMobAboveImage)
                .getScaledInstance(
                    SPHERE_MOB_DIAMETER,
                    SPHERE_MOB_DIAMETER,
                    Image.SCALE_SMOOTH);

            File sphereMobBelowImage = new File(
                pathToSprites.toString() + "\\SphereMob-Below.png");
            this.SPHERE_MOB_BELOW = ImageIO
                .read(sphereMobBelowImage)
                .getScaledInstance(
                    SPHERE_MOB_DIAMETER,
                    SPHERE_MOB_DIAMETER,
                    Image.SCALE_SMOOTH);
        }
        catch (IOException occurredExc)
        {
            occurredExc.printStackTrace();
        }
    }

    public void init(KeyListener engine, SinglePlayerLevel inputLevel)
    {
        this.renderingLevel = inputLevel;

        this.loadImages();

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
// KeyListener implementations
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
// Paint component section
//
    Image SPHERE_MOB_DEFAULT;
    Image SPHERE_MOB_ABOVE;
    Image SPHERE_MOB_BELOW;

    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(700,700);
    }

    private void paintPlayer(Graphics graphics)
    {
        int[] playerLocation = this.renderingLevel.player.currentLocation;

        graphics.setColor(Color.BLUE);
        graphics.fillPolygon(
                new int[] {
                    playerLocation[0],
                    playerLocation[0]
                    + PLAYER_SIDE_LENGTH / 2,
                    playerLocation[0]
                    - PLAYER_SIDE_LENGTH / 2 },
                new int[] {
                    playerLocation[1],
                    playerLocation[1]
                    + PLAYER_SIDE_LENGTH,
                    playerLocation[1]
                    + PLAYER_SIDE_LENGTH},
                3);
    }

    private void paintSphereMob(Graphics graphics, SphereMob sphereMob)
    {
        Image drawingImage;
        switch(sphereMob.currentLocation[2])
        {
            case 0:
            {
                drawingImage = this.SPHERE_MOB_DEFAULT;
                break;
            }

            case 1:
            {
                drawingImage = this.SPHERE_MOB_ABOVE;
                break;
            }

            case -1:
            {
                drawingImage = this.SPHERE_MOB_BELOW;
                break;
            }

            default:
            {
                throw new IllegalStateException(
                    "SphereMob have incorrect third coordinate: "
                    + sphereMob.currentLocation[2]);
            }
        }

        graphics.drawImage(
            drawingImage,
            sphereMob.currentLocation[0],
            sphereMob.currentLocation[1],
            null);
    }

    private void paintBasicProjectile(
        Graphics graphics,
        BasicProjectile projectile)
    {
        graphics.setColor(Color.YELLOW);
        graphics.fillOval(
            projectile.currentLocation[0],
            projectile.currentLocation[1],
            BASIC_PROJECTILE_DIAMETER,
            BASIC_PROJECTILE_DIAMETER);
    }

    private void paintMovableObjectsBelow(Graphics graphics)
    {
        // Paint mobs below
        this.renderingLevel.mobs.forEach(
            mobObject ->
            {
                if (
                    mobObject instanceof SphereMob
                    && mobObject.currentLocation[2] == -1)
                    paintSphereMob(graphics, (SphereMob)mobObject);
            });
    }

    /**
     * Projectiles after mobs for better debugging.
     *
     * Mobs' projectiles after Player's projectiles for player convenience.
     * Mob's projectiles have bigger priority for player than his own
     * projectiles that are harmless for player himself
     **/
    private void paintMovableObjectsOnSurface(Graphics graphics)
    {
        // Paint mobs on surface
        this.renderingLevel.mobs.forEach(
            mobObject ->
            {
                if (
                    mobObject instanceof SphereMob
                    && mobObject.currentLocation[2] == 0)
                    paintSphereMob(graphics, (SphereMob)mobObject);
            });

        // Paint player's projectiles
        this.renderingLevel.projectiles.forEach(
            projectileObject ->
            {
                if (
                        projectileObject instanceof BasicProjectile
                        && ((BasicProjectile)projectileObject).firedByPlayer)
                    paintBasicProjectile(
                        graphics,
                        (BasicProjectile)projectileObject);
            });

        // Paint mobs' projectile
        this.renderingLevel.projectiles.forEach(
            projectileObject ->
            {
                if (
                    projectileObject instanceof BasicProjectile
                    && !((BasicProjectile)projectileObject).firedByPlayer)
                    paintBasicProjectile(
                        graphics,
                        (BasicProjectile)projectileObject);
            });

        // Paint Player
        this.paintPlayer(graphics);
    }

    private void paintMovableObjectsAbove(Graphics graphics)
    {
        // Paint mobs above
        this.renderingLevel.mobs.forEach(
            mobObject ->
            {
                if (
                        mobObject instanceof SphereMob
                        && mobObject.currentLocation[2] == 1)
                    paintSphereMob(graphics, (SphereMob) mobObject);
            });
    }

    @Override
    public void paintComponent(Graphics graphics)
    {
        super.paintComponent(graphics);

        if (levelRenderingIsNeeded)
        {
            // Paint movable objects below
            this.paintMovableObjectsBelow(graphics);

            // Paint movable objects on game surface
            this.paintMovableObjectsOnSurface(graphics);

            // Paint movable objects above
            this.paintMovableObjectsAbove(graphics);

            // [Would Be Better]
            // Paint interface objects (Last painting for overall overlapping
            // by interface objects)

            this.levelRenderingIsNeeded = false;
        }
    }

//
// Main render section
//

    public void render()
    {
        this.levelRenderingIsNeeded = true;

        // Just say AWT thread to repaint game GUI
        this.repaint();

        // [Would Be Better] solution without thread sleeping
        while (levelRenderingIsNeeded)
            try
            {
                Thread.sleep(10);
            }
            catch(InterruptedException exception)
            {
                exception.printStackTrace();
            }
    }
}
