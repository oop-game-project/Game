package Engine.GameObjects;

import org.jetbrains.annotations.NotNull;

public class Player extends MortalObject implements Cloneable
{
    // Improvement: check iterations count, not amount of time
    private long lastProjectileWasFiredTime = 0;

    public Player(@NotNull int[] inputLocation)
    {
        super(inputLocation);
    }

    public int getHitPointsMax() { return 20; }

    public long getLastProjectileWasFiredTime()
    {
        return lastProjectileWasFiredTime;
    }

    public void setLastProjectileWasFiredTime(long newValue)
    {
        if (newValue > 0)
            this.lastProjectileWasFiredTime = newValue;
        else
            this.lastProjectileWasFiredTime = 0;
    }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}