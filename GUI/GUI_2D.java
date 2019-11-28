package GUI;

// Inner imports
import Engine.GameObjects.*;
import static Engine.GameObjects.PaintingConst.*;
import Engine.LevelsProcessor.GameLevel;

import java.awt.Image;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JPanel;
import javax.swing.JFrame;

// IO classes
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.function.BiConsumer;
import javax.imageio.ImageIO;

import jdk.jshell.spi.ExecutionControl.NotImplementedException;

import org.jetbrains.annotations.NotNull;

public class GUI_2D extends JPanel implements GUI, KeyListener
{
    private final JFrame gameMainFrame = new JFrame();

    @Override
    public void init(KeyListener engine, GameLevel inputLevel)
    {
        this.gameObjectsPainter = new GameObjectsPainter(inputLevel);

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

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyReleased(KeyEvent e) { }

    @Override
    public void keyPressed(@NotNull KeyEvent keyEvent)
    {
        if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE)
            this.gameMainFrame.dispatchEvent(
                new WindowEvent(
                    this.gameMainFrame,
                    WindowEvent.WINDOW_CLOSING));
    }

    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(700,700);
    }

    private class GameObjectsPainter
    {
        private class ImagesContainer
        {
            Image SPHERE_MOB_DEFAULT;
            Image SPHERE_MOB_ABOVE;
            Image SPHERE_MOB_BELOW;

            Image SPHERE_BOSS_DEFAULT;
            Image SPHERE_BOSS_ABOVE;
            Image SPHERE_BOSS_BELOW;

            Image GAME_OVER_INSCRIPTION;
            Image COMPLETED_INSCRIPTION;

            private Image loadSphereMobImage(String spritesPath, String imageName)
            {
                File sphereMobImageFile = new File(
                    Paths
                        .get(spritesPath, imageName)
                        .toString());

                Image sphereMobImage = null;
                try
                {
                    sphereMobImage =
                        ImageIO
                            .read(sphereMobImageFile)
                            .getScaledInstance(
                                SPHERE_MOB_DIAMETER,
                                SPHERE_MOB_DIAMETER,
                                Image.SCALE_SMOOTH);
                }
                catch (IOException occurredExc)
                {
                    occurredExc.printStackTrace();
                }

                return sphereMobImage;
            }

            private Image loadSphereBossImage(String spritesPath, String imageName)
            {
                File sphereBossImageFile = new File(
                    Paths
                        .get(spritesPath, imageName)
                        .toString());

                Image sphereBossImage = null;
                try
                {
                    sphereBossImage =
                        ImageIO
                            .read(sphereBossImageFile)
                            .getScaledInstance(
                                SPHERE_BOSS_DIAMETER,
                                SPHERE_BOSS_DIAMETER,
                                Image.SCALE_SMOOTH);
                }
                catch (IOException occurredExc)
                {
                    occurredExc.printStackTrace();
                }

                return sphereBossImage;
            }

            private void loadSphereMobImages(String spritesPath)
            {
                this.SPHERE_MOB_DEFAULT = loadSphereMobImage(
                    spritesPath,
                    "SphereMob-Default.png");

                this.SPHERE_MOB_ABOVE = loadSphereMobImage(
                    spritesPath,
                    "SphereMob-Above.png");

                this.SPHERE_MOB_BELOW = loadSphereMobImage(
                    spritesPath,
                    "SphereMob-Below.png");
            }

            private void loadSphereBossImages(String spritesPath)
            {
                this.SPHERE_BOSS_DEFAULT = loadSphereBossImage(
                    spritesPath,
                    "SphereMob-Default.png");

                this.SPHERE_BOSS_ABOVE = loadSphereBossImage(
                    spritesPath,
                    "SphereMob-Above.png");

                this.SPHERE_BOSS_BELOW = loadSphereBossImage(
                    spritesPath,
                    "SphereMob-Below.png");
            }

            private void loadGameOverInscription(String spritesPath)
            {
                File inscriptionImageFile = new File(
                    Paths
                        .get(spritesPath, "GameOverInscription.png")
                        .toString());

                try
                {
                    this.GAME_OVER_INSCRIPTION = ImageIO.read(inscriptionImageFile);
                }
                catch (IOException occurredExc)
                {
                    occurredExc.printStackTrace();
                }
            }

            private void loadCompletedInscription(String spritesPath)
            {
                File inscriptionImageFile = new File(
                    Paths
                        .get(spritesPath, "CompletedInscription.png")
                        .toString());

                try
                {
                    this.COMPLETED_INSCRIPTION = ImageIO.read(inscriptionImageFile);
                }
                catch (IOException occurredExc)
                {
                    occurredExc.printStackTrace();
                }
            }

            private void loadImages()
            {
                String executionFolderPath =
                    new File(
                        this
                            .getClass()
                            .getProtectionDomain()
                            .getCodeSource()
                            .getLocation()
                            .getPath()
                    ).getPath();
                String spritesPath =
                    Paths
                        .get(executionFolderPath, "Sprites")
                        .toString();

                this.loadSphereMobImages(spritesPath);

                this.loadSphereBossImages(spritesPath);

                this.loadGameOverInscription(spritesPath);
                this.loadCompletedInscription(spritesPath);
            }
        }

        ImagesContainer gameImages = new ImagesContainer();
        private final GameLevel renderingLevel;

        private GameObjectsPainter(GameLevel inputLevel)
        {
            this.renderingLevel = inputLevel;

            this.gameImages.loadImages();
        }
        private void paintPlayer(@NotNull Graphics graphics)
        {
            int[] playerLocation = this.renderingLevel.player.currentLocation;

            graphics.setColor(Color.BLUE);
            graphics.fillPolygon(
                new int[]{
                    playerLocation[0],
                    playerLocation[0]
                    + PLAYER_SIDE_LENGTH / 2,
                    playerLocation[0]
                    - PLAYER_SIDE_LENGTH / 2},
                new int[]{
                    playerLocation[1],
                    playerLocation[1]
                    + PLAYER_SIDE_LENGTH,
                    playerLocation[1]
                    + PLAYER_SIDE_LENGTH},
                3);
        }

        private void paintSphereMob(Graphics graphics, @NotNull SphereMob sphereMob)
        {
            Image drawingImage;
            switch (sphereMob.currentLocation[2])
            {
                case 0:
                {
                    drawingImage = this.gameImages.SPHERE_MOB_DEFAULT;
                    break;
                }

                case 1:
                {
                    drawingImage = this.gameImages.SPHERE_MOB_ABOVE;
                    break;
                }

                case -1:
                {
                    drawingImage = this.gameImages.SPHERE_MOB_BELOW;
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
            @NotNull BasicProjectile projectile)
        {
            if (projectile.firedByPlayer)
                graphics.setColor(Color.YELLOW);
            else
                graphics.setColor(Color.RED);
            graphics.fillOval(
                projectile.currentLocation[0],
                projectile.currentLocation[1],
                BASIC_PROJECTILE_DIAMETER,
                BASIC_PROJECTILE_DIAMETER);
        }

        private void paintSphereBoss(Graphics graphics, @NotNull SphereBoss sphereBoss)
        {
            Image drawingImage;
            Image auxiliaryDrawingImage;
            switch (sphereBoss.currentLocation[2])
            {
                case 0:
                {
                    drawingImage = this.gameImages.SPHERE_BOSS_DEFAULT;
                    auxiliaryDrawingImage = this.gameImages.SPHERE_MOB_DEFAULT;
                    break;
                }

                case 1:
                {
                    drawingImage = this.gameImages.SPHERE_BOSS_ABOVE;
                    auxiliaryDrawingImage = this.gameImages.SPHERE_MOB_ABOVE;
                    break;
                }

                case -1:
                {
                    drawingImage = this.gameImages.SPHERE_BOSS_BELOW;
                    auxiliaryDrawingImage = this.gameImages.SPHERE_MOB_BELOW;
                    break;
                }

                default:
                {
                    throw new IllegalStateException(
                        "SphereBoss instance has incorrect third coordinate: "
                        + sphereBoss.currentLocation[2]);
                }
            }

            graphics.drawImage(
                drawingImage,
                sphereBoss.currentLocation[0],
                sphereBoss.currentLocation[1],
                null);

            BiConsumer<Integer, Integer> drawBossTower =
                (Integer xModifier, Integer yModifier) ->
                    graphics.drawImage(
                        auxiliaryDrawingImage,
                        sphereBoss.currentLocation[0] + xModifier,
                        sphereBoss.currentLocation[1] + yModifier,
                        null);

            // Left tower
            drawBossTower.accept(0,         100 - 15);

            // Top tower
            drawBossTower.accept(100 - 15,  0);

            // Right tower
            drawBossTower.accept(200 - 30,  100 - 15);

            // Bottom tower
            drawBossTower.accept(100 - 15,  200 - 30);

            // Center tower
            drawBossTower.accept(100 - 15,  100 - 15);
        }

        /**
         * Use this method ONLY for MovableObject painting.
         */
        private void paintMovableObject(
            Graphics graphics,
            @NotNull MovableObject movableObject)
        {
            switch (movableObject.getClass().getSimpleName())
            {
                case "Player":
                {
                    this.paintPlayer(graphics);
                    break;
                }

                // Mobs

                case "SphereMob":
                {
                    this.paintSphereMob(graphics, (SphereMob) movableObject);
                    break;
                }

                case "SphereBoss":
                {
                    this.paintSphereBoss(graphics, (SphereBoss) movableObject);
                    break;
                }

                // Projectiles

                case "BasicProjectile":
                {
                    this.paintBasicProjectile(
                        graphics,
                        (BasicProjectile) movableObject);
                    break;
                }

                default:
                    try
                    {
                        throw new NotImplementedException(
                            "\""
                            + movableObject.getClass().getName()
                            + "\""
                            + ": painting of this class is not implemented");
                    }
                    catch (NotImplementedException occurredExc)
                    {
                        occurredExc.printStackTrace();
                    }
            }

        }

        private void paintMovableObjectsBelow(Graphics graphics)
        {
            // Paint mobs below
            this.renderingLevel.mobs.forEach(
                mobObject ->
                {
                    if (mobObject.currentLocation[2] == -1)
                        this.paintMovableObject(graphics, mobObject);
                });
        }

        /**
         * Projectiles after mobs for better debugging.
         **/
        private void paintMovableObjectsOnSurface(Graphics graphics)
        {
            // Paint mobs on surface
            this.renderingLevel.mobs.forEach(
                mobObject ->
                {
                    if (mobObject.currentLocation[2] == 0)
                        this.paintMovableObject(graphics, mobObject);
                });

            // Paint projectiles
            this.renderingLevel.projectiles.forEach(
                projectile ->
                    this.paintMovableObject(graphics, projectile));

            // Paint Player
            this.paintMovableObject(graphics, this.renderingLevel.player);
        }

        private void paintMovableObjectsAbove(Graphics graphics)
        {
            // Paint mobs above
            this.renderingLevel.mobs.forEach(
                mobObject ->
                {
                    if (mobObject.currentLocation[2] == 1)
                        this.paintMovableObject(graphics, mobObject);
                });
        }

        private void paintGameOverInscription(
            @NotNull Graphics graphics,
            @NotNull GameOverInscription gameOverInscription)
        {
            graphics.drawImage(
                this.gameImages.GAME_OVER_INSCRIPTION,
                gameOverInscription.getLocation()[0],
                gameOverInscription.getLocation()[1],
                null);
        }

        private void paintCompletedInscription(
            @NotNull Graphics graphics,
            @NotNull CompletedInscription completedInscription)
        {
            graphics.drawImage(
                this.gameImages.COMPLETED_INSCRIPTION,
                completedInscription.getLocation()[0],
                completedInscription.getLocation()[1],
                null);
        }

        private void paintInterfaceObject(
            Graphics graphics,
            @NotNull InterfaceObject interfaceObject)
        {
            switch (interfaceObject.getClass().getSimpleName())
            {
                case "GameOverInscription":
                {
                    this.paintGameOverInscription(
                        graphics,
                        (GameOverInscription) interfaceObject);

                    break;
                }

                case "CompletedInscription":
                {
                    this.paintCompletedInscription(
                        graphics,
                        (CompletedInscription) interfaceObject);
                    break;
                }

                default:
                    try
                    {
                        throw new NotImplementedException(
                            "\""
                            + interfaceObject.getClass().getName()
                            + "\""
                            + ": painting of this class is not implemented");
                    }
                    catch (NotImplementedException occurredExc)
                    {
                        occurredExc.printStackTrace();
                    }
            }
        }

        private void paintInterfaceObjects(Graphics graphics)
        {
            this.renderingLevel.interfaceObjects.forEach(
                interfaceObject -> this.paintInterfaceObject(graphics, interfaceObject));
        }
    }

    /**
     * For rendering ONLY when method "render" invokes
     **/
    private boolean levelRenderingIsNeeded = false;

    private GameObjectsPainter gameObjectsPainter;

    @Override
    public void paintComponent(Graphics graphics)
    {
        super.paintComponent(graphics);

        if (levelRenderingIsNeeded)
        {
            this.gameObjectsPainter.paintMovableObjectsBelow(graphics);

            this.gameObjectsPainter.paintMovableObjectsOnSurface(graphics);

            this.gameObjectsPainter.paintMovableObjectsAbove(graphics);

            this.gameObjectsPainter.paintInterfaceObjects(graphics);

            this.levelRenderingIsNeeded = false;
        }
    }

    @Override
    public void render()
    {
        this.levelRenderingIsNeeded = true;

        // Just say AWT thread to repaint game GUI
        this.repaint();

        // In this loop gameLoop thread awaits while GUI is rendering. Needed because
        // in GUI and gameLoop threads level is shared. When level is updating and
        // rendering at the same time, concurrent level modification occurs
        //
        // Improvement: implement solution without thread sleeping. Level cloning maybe?
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
