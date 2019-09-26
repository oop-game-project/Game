package Engine.GameObjects;

import org.jetbrains.annotations.NotNull;

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
        @NotNull int[] inputLocation,
        @NotNull int[] inputMovingVector,
        boolean inputBorderWasCrossed)
    {
        super(inputLocation);

        this.autoMovingVector = inputMovingVector.clone();
        this.borderWasCrossed = inputBorderWasCrossed;
    }

    public int getHitPointsMax() { return 2; }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}
