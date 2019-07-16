package Game.Engine;

import org.jetbrains.annotations.NotNull;

public class GameObjects
{
    abstract class GameObject { }

//
//  Movable objects
//

    public abstract class MovableObject extends GameObject
    {
        //  Only needed for calculations around screen moving forward
        public final long spawnTime;
        public final int[] spawnLocation;

        public int[] currentLocation;

        MovableObject(@NotNull int[] inputLocation)
        {
            assert inputLocation.length == 3;

            this.spawnTime = System.currentTimeMillis();
            this.spawnLocation = inputLocation.clone();
            this.currentLocation = inputLocation.clone();
        }

        public void modifyLocation(int[] locationModifier)
        {
            this.currentLocation[0] += locationModifier[0];
            this.currentLocation[1] += locationModifier[1];
            this.currentLocation[2] += locationModifier[2];
        }
    }

    public abstract class MortalObject extends MovableObject
    {
        public int hitPointsCurrent;

        public MortalObject(
            int[] inputLocation,
            int inputHitPointsCurrent)
        {
            super(inputLocation);

            assert inputHitPointsCurrent > 0;

            this.hitPointsCurrent = inputHitPointsCurrent;
        }

        public abstract int getHitPointsMax();
    }

    public class Player extends MortalObject
    {
        public int lastProjectileWasFiredTime = 0;

        public Player(
                @NotNull int[] inputLocation,
                int inputHitPointsCurrent)
        {
            super(inputLocation, inputHitPointsCurrent);
        }

        public int getHitPointsMax() { return 30; }
    }

    public class SphereMob extends MortalObject
    {
        public SphereMob(
            int[] inputLocation,
            int inputHitPointsCurrent)
        {
            super(inputLocation, inputHitPointsCurrent);
        }

        public int getHitPointsMax() { return 5; }
    }

    /**
     *  It just flies forward. Up if fired by player and down otherwise
     **/
    public class BasicProjectile extends MovableObject
    {
        public final boolean firedByPlayer;

        public BasicProjectile(
            int[] inputLocation,
            MovableObject firedObject)
        {
            super(inputLocation);

            this.firedByPlayer = firedObject instanceof Player;
        }

    }

//
//  Interface objects
//

    public class InterfaceObject { }

//
//  Painting constants for graphical and collision models
//

    public class PaintingConst
    {
        public static final int PLAYER_TRIANGLE_SIDE_LENGTH = 30;
        public static final int SPHERE_MOB_CIRCLE_DIAMETER = 30;  // TODO: test
        public static final int BASIC_PROJECTILE_DIAMETER = 7;
    }

}