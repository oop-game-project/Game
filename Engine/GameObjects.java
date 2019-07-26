package Game.Engine;

import org.jetbrains.annotations.NotNull;

public class GameObjects
{
    abstract class GameObject implements Cloneable { }

//
// Movable objects
//

    public abstract class MovableObject extends GameObject implements Cloneable
    {
        public int[] currentLocation;

        MovableObject(@NotNull int[] inputLocation)
        {
            assert inputLocation.length == 3;

            this.currentLocation = inputLocation.clone();
        }

        public void modifyLocation(int[] locationModifier)
        {
            this.currentLocation[0] += locationModifier[0];
            this.currentLocation[1] += locationModifier[1];
            this.currentLocation[2] += locationModifier[2];
        }

        @Override
        public Object clone() throws CloneNotSupportedException
        {
            MovableObject newMovableObject = (MovableObject)super.clone();

            newMovableObject.currentLocation = this.currentLocation.clone();

            return newMovableObject;
        }
    }

    /**
     * It just flies forward. Up if fired by player and down otherwise.
     **/
    public class BasicProjectile extends MovableObject implements Cloneable
    {
        public final boolean firedByPlayer;

        public BasicProjectile(
            int[] inputLocation,
            MovableObject firedObject)
        {
            super(inputLocation);

            this.firedByPlayer = firedObject instanceof Player;
        }

        @Override
        public Object clone() throws CloneNotSupportedException
        {
            return super.clone();
        }
    }


//
// Mortal objects
//

    public abstract class MortalObject extends MovableObject
        implements Cloneable
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

        @Override
        public Object clone() throws CloneNotSupportedException
        {
            return super.clone();
        }
    }

    public class Player extends MortalObject implements Cloneable
    {
        public long lastProjectileWasFiredTime = 0;

        public Player(
                @NotNull int[] inputLocation,
                int inputHitPointsCurrent)
        {
            super(inputLocation, inputHitPointsCurrent);
        }

        public int getHitPointsMax() { return 30; }

        @Override
        public Object clone() throws CloneNotSupportedException
        {
            return super.clone();
        }
    }

    public class SphereMob extends MortalObject implements Cloneable
    {
        /**
         * It sets moving speed and direction
         **/
        public final int[] autoMovingVector;

        /**
         * When a mob spawns outside game field it prevents engine from
         * despawning this mob immediately
         *
         * [Would Be Better] place it in "GameMob" class between mob classes
         * and MortalObject
         **/
        public boolean wasOnScreen = false;

        /**
         * This time should pass from game start for mob to spawn
         *
         * [Would Be Better] place it in "GameMob" class between mob classes
         * and MortalObject
         **/
        public final int spawnTime;

        public SphereMob(
            int[] inputLocation,
            int[] inputMovingVector,
            int inputHitPointsCurrent,
            int inputSpawnTime)
        {
            super(inputLocation, inputHitPointsCurrent);

            this.autoMovingVector = inputMovingVector.clone();
            this.spawnTime = inputSpawnTime;
        }

        public int getHitPointsMax() { return 5; }

        @Override
        public Object clone() throws CloneNotSupportedException
        {
            return super.clone();
        }
    }


//
// Interface objects
//

    public class InterfaceObject extends GameObject { }

//
// Painting constants for graphical and collision models
//

    public class PaintingConst
    {
        /**
         * Player is a triangular
         */
        public static final int PLAYER_SIDE_LENGTH = 30;

        /**
         * Sphere mob is a circle (because of pseudo 3d)
         */
        public static final int SPHERE_MOB_DIAMETER = 30;

        /**
         * Projectile is a flat circle
         */
        public static final int BASIC_PROJECTILE_DIAMETER = 7;
    }

}