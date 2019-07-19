package Game.Engine;

import Game.Engine.GameObjects.*;
import Game.Engine.LevelsProcessor.SinglePlayerLevel;

public class CollisionsProcessor {

    private int sizeX;
    private int sizeY;
    private int sizeZ;

    public CollisionsProcessor(int[] gameFieldSize) {

        sizeX = gameFieldSize[0];
        sizeY = gameFieldSize[1];
        sizeZ = gameFieldSize[2];
    }

    public Collision getCollision(SinglePlayerLevel currentLevel, int[] moveVector, MovableObject movableObject) {

        int playerX = movableObject.currentLocation[0];
        int playerY = movableObject.currentLocation[1];
//        int playerZ = movableObject.currentLocation[2]; ?
        int vectorX = moveVector[0];
        int vectorY = moveVector[1];
//        int vectorZ = moveVector[2]; ?
        if (movableObject instanceof Player) {

            if (playerOutOfHorizontalBorders(vectorX, playerX))
                    return new Collision(movableObject, GameEvent.OUT_HORIZONTAL, null);
            if (playerOutOfVerticalBorders(vectorY, playerY))
                return new Collision(movableObject, GameEvent.OUT_VERTICAL, null);
        }
        return new Collision(movableObject, GameEvent.OK, null);
    }

    private boolean playerOutOfHorizontalBorders(int vector, int player) {

        return (player + vector + PaintingConst.PLAYER_TRIANGLE_SIDE_LENGTH / 2 > sizeX)
                || (player + vector - PaintingConst.PLAYER_TRIANGLE_SIDE_LENGTH / 2 < 0);
    }

    private boolean playerOutOfVerticalBorders(int vector, int player) {

        return (player + vector + PaintingConst.PLAYER_TRIANGLE_SIDE_LENGTH > sizeY)
                || (player + vector < 0);
    }

    public enum GameEvent {
        OUT_VERTICAL,
        OUT_HORIZONTAL,
        OK
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
