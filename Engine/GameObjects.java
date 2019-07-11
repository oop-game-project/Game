package Game.Engine;

import org.jetbrains.annotations.NotNull;

public class GameObjects
{
    abstract class GameObject
    {
        GameObject() { }
    }

//
//  Movable objects
//

    public abstract class MovableObject
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

    public class Projectile extends MovableObject
    {
        public final boolean firedByPlayer;

        public Projectile(int[] inputLocation, boolean inputFiredByPlayer)
        {
            super(inputLocation);

            this.firedByPlayer = inputFiredByPlayer;
        }

    }

//
//  Mortal objects
//

    public abstract class MortalObject extends MovableObject
    {
        public static final int hitPointsMax = 0;
        public int hitPointsCurrent;

        public MortalObject(
            int[] inputLocation,
            int inputHitPointsCurrent)
        {
            super(inputLocation);

            assert inputHitPointsCurrent > 0;

            this.hitPointsCurrent = inputHitPointsCurrent;
        }
    }

    public class Player extends MortalObject
    {
        public static final int hitPointsMax = 20;

        public Player(
                @NotNull int[] inputLocation,
                int inputHitPointsCurrent)
        {
            super(inputLocation, inputHitPointsCurrent);
        }
    }

    public class SphereMob extends MortalObject
    {
        public static final int hitPointsMax = 5;

        public SphereMob(
            int[] inputLocation,
            int inputHitPointsCurrent)
        {
            super(inputLocation, inputHitPointsCurrent);
        }
    }

//
//  Interface objects
//

    public class InterfaceObject { }

//
//  For painting
//

    public abstract static class PaintingConst
    {
        public static final int PLAYER_TRIANGLE_SIDE_LENGTH = 30;
        public static final int SPHERE_MOB_RADIUS = 20;
        public static final int BASIC_PROJECTILE_SIDE_LENGTH = 2;
    }
}