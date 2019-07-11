package Game.Engine;

import Game.Engine.GameObjects.*;
import Game.Engine.LevelsProcessor.SinglePlayerLevel;

public class CollisionsProcessor
{
    private int[] FIELD_SIZE = new int[3];

    public CollisionsProcessor(int[] gameFieldSize)
    {
        this.FIELD_SIZE[0] = gameFieldSize[0];
        this.FIELD_SIZE[1] = gameFieldSize[1];
        this.FIELD_SIZE[2] = gameFieldSize[2];
    }

//
//  Checking section
//

    public boolean playerOutOfHorizontalBorders(int[] moveVector, Player movableObject)
    {
        int[] playerLocation = movableObject.currentLocation;

        return playerLocation[0]
               + moveVector[0]
               + PaintingConst.PLAYER_TRIANGLE_SIDE_LENGTH / 2 > this.FIELD_SIZE[0]
            || playerLocation[0]
               + moveVector[0]
               - PaintingConst.PLAYER_TRIANGLE_SIDE_LENGTH / 2 < 0;
    }

    public boolean playerOutOfVerticalBorders(int[] moveVector, Player movableObject)
    {
        int[] playerLocation = movableObject.currentLocation;

        return playerLocation[1]
               + moveVector[1]
               + (int) (PaintingConst.PLAYER_TRIANGLE_SIDE_LENGTH * 0.75) > this.FIELD_SIZE[1]
            || playerLocation[1] + moveVector[1] < 0;
    }

//
//  Main section
//

    public enum GameEvent
    {
        OUT_OF_BOUNDS,
        COLLISION,
        OK
    }

    public class Collision
    {
        public final MovableObject movingObject;
        public final GameEvent event;
        public final MovableObject collidedObject;

        public Collision(
            MovableObject inputMovingObject,
            GameEvent inputEvent,
            MovableObject inputCollidedObject)
        {
            this.movingObject = inputMovingObject;
            this.event = inputEvent;
            this.collidedObject = inputCollidedObject;
        }
    }

    public Collision[] getCollision(
        SinglePlayerLevel currentLevel,
        int[] moveVector,
        MovableObject movableObject)
    {
        if (movableObject instanceof Player)
        // All player's moves should be checked here
        {
            if (this.playerOutOfHorizontalBorders(moveVector, (Player)movableObject)
                || this.playerOutOfVerticalBorders(moveVector, (Player)movableObject))
                return new Collision[] {
                    new Collision(
                        movableObject,
                        GameEvent.OUT_OF_BOUNDS,
                        null) };
        }

        return new Collision[] {
            new Collision(movableObject, GameEvent.OK, null) };
    }



}