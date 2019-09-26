package Engine.GameObjects;

import org.jetbrains.annotations.NotNull;

public class Player extends MortalObject implements Cloneable
{
    // WouldBeBetter check iterations count, not amount of time
    public long lastProjectileWasFiredTime = 0;

    public Player(@NotNull int[] inputLocation)
    {
        super(inputLocation);
    }

    public int getHitPointsMax() { return 20; }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}