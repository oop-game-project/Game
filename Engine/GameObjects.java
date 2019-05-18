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
        public int[] currentLocation;

        MovableObject(@NotNull int[] inputLocation)
        {
            assert inputLocation.length == 3;

            this.currentLocation = inputLocation;
        }

        public void modifyLocation(int[] locationModifier)
        {
            this.currentLocation[0] += locationModifier[0];
            this.currentLocation[1] += locationModifier[1];
            this.currentLocation[2] += locationModifier[2];
        }
    }

    public class Player extends MovableObject
    {
        public static final int hitPointsMax = 20;
        public int hitPointsCurrent;

        public Player(
                @NotNull int[] inputLocation,
                int inputHitPointsCurrent)
        {
            super(inputLocation);

            assert inputHitPointsCurrent > 0;

            this.hitPointsCurrent = inputHitPointsCurrent;
        }
    }

    public class SphereMob extends MovableObject
    {
        public static final int hitPointsMax = 5;
        public int hitPointsCurrent;

        public final int radius;

        public SphereMob(
                int[] inputLocation,
                int inputHitPointsCurrent,
                int inputRadius)
        {
            super(inputLocation);

            assert inputHitPointsCurrent > 0;

            this.hitPointsCurrent = inputHitPointsCurrent;
            this.radius = inputRadius;
        }
    }

    public class Projectile extends MovableObject
    {
        // public final A projectileType
        public final boolean firedByPlayer;

        public Projectile(int[] inputLocation, boolean inputFiredByPlayer)
        {
            super(inputLocation);

            this.firedByPlayer = inputFiredByPlayer;
        }

    }
}