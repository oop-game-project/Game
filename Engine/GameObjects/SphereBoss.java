package Engine.GameObjects;

import org.jetbrains.annotations.NotNull;

public class SphereBoss extends MortalObject
{
    /**
     * It sets moving speed and direction
     **/
    public int[] autoMovingVector;

    /**
     * Time in game loop iterations
     **/
    public long lastVolleyIteration = 0;

    public SphereBoss(
        @NotNull int[] inputLocation,
        @NotNull int[] inputMovingVector,
        boolean inputBorderWasCrossed)
    {
        super(inputLocation);

        this.autoMovingVector = inputMovingVector.clone();
    }

    public int getHitPointsMax() { return 50; }
}
