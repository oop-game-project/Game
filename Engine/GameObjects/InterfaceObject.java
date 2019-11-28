package Engine.GameObjects;

import org.jetbrains.annotations.NotNull;

public abstract class InterfaceObject extends GameObject
{
    private int[] location;

    public InterfaceObject(@NotNull int[] inputLocation)
    {
        assert inputLocation.length == 3;

        this.location = inputLocation.clone();
    }

    public int[] getLocation()
    {
        return this.location.clone();
    }
}
