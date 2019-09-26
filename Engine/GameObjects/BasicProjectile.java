package Engine.GameObjects;

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
