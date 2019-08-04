package Engine;

import org.jetbrains.annotations.NotNull;

public class GameObjects
{
    abstract class GameObject implements Cloneable
    {
        public boolean shouldBeDespawned = false;
    }

//
// Movable objects
//

    public abstract class MovableObject extends GameObject implements Cloneable
    {
        /**
         * WouldBeBetter actually have "double" coordinates for moving
         *  speed less than 1 pixel per iteration
         */
        public int[] currentLocation;

        MovableObject(@NotNull int[] inputLocation)
        {
            assert inputLocation.length == 3;

            this.currentLocation = inputLocation.clone();
        }

        public void modifyLocation(@NotNull int[] locationModifier)
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
        public MortalObject(int[] inputLocation)
        {
            super(inputLocation);
        }

        public abstract int getHitPointsMax();

        @Override
        public Object clone() throws CloneNotSupportedException
        {
            return super.clone();
        }

//
// Projectiles' damage methods
//

        private int hitPoints = this.getHitPointsMax();

        public boolean isNotDead()
        {
            return hitPoints != 0;
        }

        private void addToHitPoints(int addendum)
        {
            this.hitPoints += addendum;

            if (this.hitPoints < 0)
                this.hitPoints = 0;
        }

        public void receiveDamageFromBasicProjectile()
        {
            this.addToHitPoints(-1);
        }
    }

    public class Player extends MortalObject implements Cloneable
    {
        // WouldBeBetter check iterations count, not amount of time
        public long lastProjectileWasFiredTime = 0;

        public Player(@NotNull int[] inputLocation)
        {
            super(inputLocation);
        }

        public int getHitPointsMax() { return 10; }  // Optimize

        @Override
        public Object clone() throws CloneNotSupportedException
        {
            return super.clone();
        }
    }

    /**
     * This mob moves in one direction with fixed speed and fires
     * BasicProjectile every N seconds
     */
    public class SphereMob extends MortalObject implements Cloneable
    {
        /**
         * It sets moving speed and direction
         **/
        public final int[] autoMovingVector;

        /**
         * When a mob spawns outside game field it prevents engine from
         * despawning this mob immediately. I.e., every GameObject in the game
         * should eventually be fully on the game field
         *
         * WouldBeBetter place it in "GameMob" class between mob classes
         *  and MortalObject
         **/
        public boolean borderWasCrossed;

        public SphereMob(
            int[] inputLocation,
            int[] inputMovingVector,
            boolean inputBorderWasCrossed)
        {
            super(inputLocation);

            this.autoMovingVector = inputMovingVector.clone();
            this.borderWasCrossed = inputBorderWasCrossed;
        }

        public int getHitPointsMax() { return 5; }  // Optimize

        @Override
        public Object clone() throws CloneNotSupportedException
        {
            return super.clone();
        }
    }

    public class SphereBoss extends MortalObject
    {
        /**
         * It sets moving speed and direction
         **/
        public int[] autoMovingVector;

        /**
         * When a mob spawns outside game field it prevents engine from
         * despawning this mob immediately. I.e., every GameObject in the game
         * should eventually be fully on the game field
         *
         * WouldBeBetter place it in "GameMob" class between mob classes
         *  and MortalObject
         **/
        public boolean borderWasCrossed;

        /**
         * Time in game loop iterations
         **/
        public long lastVolleyIteration = 0;

        public SphereBoss(
            int[] inputLocation,
            int[] inputMovingVector,
            boolean inputBorderWasCrossed)
        {
            super(inputLocation);

            this.autoMovingVector = inputMovingVector.clone();
            this.borderWasCrossed = inputBorderWasCrossed;
        }

        public int getHitPointsMax() { return 100; }  // Optimize
    }
//
// Interface objects
//

//    public class InterfaceObject extends GameObject { }

//
// Painting constants for graphical and collision models
//

    public static class PaintingConst
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
        public static final int BASIC_PROJECTILE_DIAMETER = 10;

        /**
         * Same as SphereMob but bigger
         **/
        public static final int SPHERE_BOSS_DIAMETER = 200;
    }
}