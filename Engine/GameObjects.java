package Game.Engine;

import org.jetbrains.annotations.NotNull;

public class GameObjects
{
    abstract class GameObject
    {
        GameObject() { }
    }

    public class MovableObject
    {
        public final int hitPointsMax;
        public int hitPointsCurrent;

        public int currentLocationX;
        public int currentLocationY;
        public int currentLocationZ;

        MovableObject(
                @NotNull int[] inputLocation,
                int inputHitPointsMax,
                int inputHitPointsCurrent)
        {
            assert inputLocation.length == 3;

            this.currentLocationX = inputLocation[0];
            this.currentLocationY = inputLocation[1];
            this.currentLocationZ = inputLocation[2];

            assert inputHitPointsMax > 0;
            assert 0 < inputHitPointsCurrent && inputHitPointsCurrent <= inputHitPointsMax;

            this.hitPointsMax = inputHitPointsMax;
            this.hitPointsCurrent = inputHitPointsCurrent;
        }
    }

    public class Player extends MovableObject
    {
        public Player(
                @NotNull int[] inputLocation,
                int inputHitPointsMax,
                int inputHitPointsCurrent)
        {
            super(inputLocation, inputHitPointsMax, inputHitPointsCurrent);
        }

        Player(@NotNull Player inputPlayer)
        {
            super(inputPlayer.getCurrentLocation());
        }
    }

    public class SphereMob extends MovableObject
    {

        private int radius;

        public SphereMob(int[] inputLocation, int radius)
        {
            super(inputLocation);
            this.radius = radius;
        }

        public int getRadius() { return radius; }
    }
}