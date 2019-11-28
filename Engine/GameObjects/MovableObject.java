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
        private int[] currentLocation;

        MovableObject(@NotNull int[] inputLocation)
        {
            assert inputLocation.length == 3;

            this.currentLocation = inputLocation.clone();
        }

        public int[] getCurrentLocation()
        {
            return this.currentLocation.clone();
        }

        public void setCurrentLocation(@NotNull int[] newValue)
        {
            this.currentLocation = newValue.clone();
        }

        public void setCurrentLocationX(int newValue)
        {
            this.currentLocation[0] = newValue;
        }

        public void setCurrentLocationY(int newValue)
        {
            this.currentLocation[1] = newValue;
        }

        public void setCurrentLocationZ(int newValue)
        {
            this.currentLocation[2] = newValue;
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
