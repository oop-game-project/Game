package Game.Engine;

import org.jetbrains.annotations.NotNull;

public class GameObjects
{
    abstract class GameObject
    {
        GameObject() { }
    }

    public class GameField extends GameObject
    {
        private int sizeX;
        private int sizeY;
        private int sizeZ;

        public GameField(int inputSizeX, int inputSizeY, int inputSizeZ)
        {
            assert inputSizeX > 0;
            assert inputSizeY > 0;
            assert inputSizeZ > 0;

            this.sizeX = inputSizeX;
            this.sizeY = inputSizeY;
            this.sizeZ = inputSizeZ;
        }

        GameField(@NotNull GameField inputGameField)
        {
            int[] inputGameFieldSize = inputGameField.getGameFieldSize();

            this.sizeX = inputGameFieldSize[0];
            this.sizeY = inputGameFieldSize[1];
            this.sizeZ = inputGameFieldSize[2];
        }

        public int[] getGameFieldSize()
        {
            return new int[] { this.sizeX, this.sizeY, this.sizeZ };
        }
    }

    public class MovableObject
    {
        private int currentLocationX;
        private int currentLocationY;
        private int currentLocationZ;

        MovableObject(int inputLocationX, int inputLocationY, int inputLocationZ)
        {
            this.currentLocationX = inputLocationX;
            this.currentLocationY = inputLocationY;
            this.currentLocationZ = inputLocationZ;
        }

        MovableObject(@NotNull int[] inputLocation)
        {
            assert inputLocation.length == 3;

            this.currentLocationX = inputLocation[0];
            this.currentLocationY = inputLocation[1];
            this.currentLocationZ = inputLocation[2];
        }

        public int[] getCurrentLocation()
        {
            return new int[] {
                    this.currentLocationX,
                    this.currentLocationY,
                    this.currentLocationZ };
        }

        public void setCurrentLocation(int inputLocationX, int inputLocationY, int inputLocationZ)
        {
            this.currentLocationX = inputLocationX;
            this.currentLocationY = inputLocationY;
            this.currentLocationZ = inputLocationZ;
        }

        public void modifyCurrentLocation(Engine.GeometryVector vector)
        {
            this.currentLocationX += vector.getX();
            this.currentLocationY += vector.getY();
            this.currentLocationZ += vector.getZ();
        }
    }

//    public class MortalCreature extends MovableObject
//    {
//        int hitPointsMax;
//        int hitPointsCurrent;
//
//        public MortalCreature(
//                @NotNull int[] inputLocation,
//                int inputHitPointsMax,
//                int inputHitPointsCurrent)
//        {
//            super(inputLocation);
//
//            assert inputHitPointsMax > 0;
//            assert 0 < inputHitPointsCurrent && inputHitPointsCurrent <= inputHitPointsMax;
//
//            this.hitPointsMax = inputHitPointsMax;
//            this.hitPointsCurrent = inputHitPointsCurrent;
//        }
//    }
//    publc class MobObject { }
//
//    public class PlayerProjectile { }
//
//    public class MobProjectile { }

    public class Player extends MovableObject
    {
        public Player(int inputLocationX, int inputLocationY, int inputLocationZ)
        {
            super(inputLocationX, inputLocationY, inputLocationZ);
        }

        Player(@NotNull Player inputPlayer)
        {
            super(inputPlayer.getCurrentLocation());
        }
    }

    public class SphereMob extends MovableObject
    {
        private int radius;

        public SphereMob(int inputLocationX, int inputLocationY, int inputLocationZ, int radius)
        {
            super(inputLocationX, inputLocationY, inputLocationZ);
            this.radius = radius;
        }

        public int getRadius() { return radius; }
    }
}