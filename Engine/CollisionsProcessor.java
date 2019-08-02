package Engine;

import Engine.GameObjects.*;
import Engine.LevelsProcessor.SinglePlayerLevel;

public class CollisionsProcessor {

    private int sizeX;
    private int sizeY;
    private int sizeZ;
    private int playerX;
    private int playerY;

    public CollisionsProcessor(int[] gameFieldSize) {

        sizeX = gameFieldSize[0];
        sizeY = gameFieldSize[1];
        sizeZ = gameFieldSize[2];
        playerX = 175;
        playerY = 350;
    }

    public Collision getCollision(SinglePlayerLevel currentLevel, int[] moveVector, MovableObject movableObject) {

        int x = movableObject.currentLocation[0];
        int y = movableObject.currentLocation[1];
        int z = movableObject.currentLocation[2];
        int vectorX = moveVector[0];
        int vectorY = moveVector[1];
        int vectorZ = moveVector[2];

        if (movableObject instanceof Player) {
            playerX = x;
            playerY = y;
            return getPlayerCollision(x, y, vectorX, vectorY, (Player)movableObject);
        }
        if (movableObject instanceof BasicProjectile) {
            return getBasicProjectileCollision(x, y, vectorX, vectorY, (BasicProjectile)movableObject);
        }
        throw new IllegalArgumentException("unknown movableObject");
    }

    private Collision getPlayerCollision(int x, int y, int vectorX, int vectorY, Player movableObject) {
        if (playerOutOfHorizontalBorders(vectorX, x) && playerOutOfVerticalBorders(vectorY, y))
            return new Collision(movableObject, GameEvent.OUT_DIAGONAL, null);
        if (playerOutOfHorizontalBorders(vectorX, x))
            return new Collision(movableObject, GameEvent.OUT_HORIZONTAL, null);
        if (playerOutOfVerticalBorders(vectorY, y))
            return new Collision(movableObject, GameEvent.OUT_VERTICAL, null);
        return new Collision(movableObject, GameEvent.OK, null);
    }

    private boolean playerOutOfHorizontalBorders(int vector, int player) {
        return (player + vector + PaintingConst.PLAYER_SIDE_LENGTH / 2 > sizeX)
                || (player + vector - PaintingConst.PLAYER_SIDE_LENGTH / 2 < 0);
    }

    private boolean playerOutOfVerticalBorders(int vector, int player) {
        return (player + vector + PaintingConst.PLAYER_SIDE_LENGTH > sizeY)
                || (player + vector < 0);
    }

    private Collision getBasicProjectileCollision(int x, int y, int vectorX, int vectorY, BasicProjectile movableObject) {
        int projectileX = x + vectorX + PaintingConst.BASIC_PROJECTILE_DIAMETER;
        int projectileY = y + vectorY + PaintingConst.BASIC_PROJECTILE_DIAMETER;

        if (projectileY > sizeY || projectileY < 0)
            return new Collision(movableObject, GameEvent.BASIC_PROJECTILE_IS_OUT, null);
        if (!movableObject.firedByPlayer &&
                (projectileX <= playerX + PaintingConst.PLAYER_SIDE_LENGTH &&
                        projectileX >= playerX - PaintingConst.PLAYER_SIDE_LENGTH) &&
                (projectileY <= playerY + PaintingConst.PLAYER_SIDE_LENGTH &&
                        projectileY >= playerY - PaintingConst.PLAYER_SIDE_LENGTH))
            return new Collision(movableObject, GameEvent.BASIC_PROJECTILE_TOUCHED_PLAYER, null);

        return new Collision(movableObject, GameEvent.OK, null);
    }

    public enum GameEvent {
        OK,
        OUT_VERTICAL,
        OUT_HORIZONTAL,
        OUT_DIAGONAL,
        BASIC_PROJECTILE_IS_OUT,
        BASIC_PROJECTILE_TOUCHED_PLAYER
    }

    public class Collision {
        public final MovableObject movingObject;
        public final GameEvent event;
        public final MovableObject collidedObject;

        public Collision(MovableObject inputMovingObject, GameEvent inputEvent, MovableObject inputCollidedObject) {
            this.movingObject = inputMovingObject;
            this.event = inputEvent;
            this.collidedObject = inputCollidedObject;
        }
    }

}
