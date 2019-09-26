package Engine.GameObjects;

import org.jetbrains.annotations.NotNull;

public abstract class InterfaceObject extends GameObject
{
    public int[] location;

    public InterfaceObject(@NotNull int[] inputLocation)
    {
        assert inputLocation.length == 3;

        this.location = inputLocation.clone();
    }
}
