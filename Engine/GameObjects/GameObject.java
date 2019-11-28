package Engine.GameObjects;

public abstract class GameObject implements Cloneable
{
    private boolean shouldBeDespawned = false;

    public boolean getShouldBeDespawned()
    {
        return this.shouldBeDespawned;
    }

    public void setShouldBeDespawned()
    {
        this.shouldBeDespawned = true;
    }
}
