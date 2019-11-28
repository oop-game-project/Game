package Engine.GameObjects;

import org.jetbrains.annotations.NotNull;

public class SphereBoss extends MortalObject
{
    /**
     * It sets moving speed and direction
     **/
    public final int[] autoMovingVector;

    /**
     * Time in game loop iterations
     **/
    private long lastVolleyIteration = 0;

    public SphereBoss(
        @NotNull int[] inputLocation,
        @NotNull int[] inputMovingVector,
        boolean inputBorderWasCrossed)
    {
        super(inputLocation);

        this.autoMovingVector = inputMovingVector.clone();
    }

    public int getHitPointsMax() { return 50; }

    public long getLastVolleyIteration()
    {
        return this.lastVolleyIteration;
    }

    public void setLastVolleyIteration(long newValue)
    {
        if (newValue > 0)
            this.lastVolleyIteration = newValue;
        else
            this.lastVolleyIteration = 0;
    }
}
