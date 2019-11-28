package Engine.GameObjects;

import org.jetbrains.annotations.NotNull;

public abstract class MovableObject extends GameObject implements Cloneable
{
        /**
         * Improvement: actually have "double" coordinates for moving
         *  speed less than 1 pixel per iteration
         * Improvement: if this coordinates were in GameObject because all game
         *  objects have coordinates actually
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
