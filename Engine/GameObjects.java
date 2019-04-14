package Game.Engine;


import org.jetbrains.annotations.NotNull;

public class GameObjects
{
    private GameFieldObject gameField;
    private Player playerOne;

    public GameObjects(@NotNull GameFieldObject inputGameField, @NotNull Player inputPlayerOne)
    {
        this.gameField = new GameFieldObject(inputGameField);
        this.playerOne = new Player(inputPlayerOne);
    }


    abstract class GameObject
    {
        GameObject() { }
    }

    public class GameFieldObject extends GameObject
    {
        private int sizeX;
        private int sizeY;
        private int sizeZ;

        GameFieldObject(int inputSizeX, int inputSizeY, int inputSizeZ)
        {
            assert inputSizeX > 0;
            assert inputSizeY > 0;
            assert inputSizeZ > 0;

            this.sizeX = inputSizeX;
            this.sizeY = inputSizeY;
            this.sizeZ = inputSizeZ;
        }

        GameFieldObject(@NotNull GameFieldObject inputGameField)
        {
            int[] inputGameFieldSize = inputGameField.getGameFieldSize();

            this.sizeX = inputGameFieldSize[0];
            this.sizeY = inputGameFieldSize[1];
            this.sizeZ = inputGameFieldSize[2];
        }

        int[] getGameFieldSize()
        {
            return new int[] { this.sizeX, this.sizeY, this.sizeZ };
        }
    }

    public abstract class MovableObject
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

        int[] getCurrentLocation()
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

//    public class Creature extends MovableObject
//    {
//        int hitPointsMax;
//        int hitPointsCurrent;
//
//        public Creature(Integer[] inputLocation, int inputHitPointsMax, int inputHitPointsCurrent)
//        {
//            super(inputLocation);
//
//            assert inputHitPointsMax > 0;
//            assert inputHitPointsCurrent > 0;
//
//            this.hitPointsMax = inputHitPointsMax;
//            this.hitPointsCurrent = inputHitPointsCurrent;
//        }
//    }

    public class Player extends MovableObject
    {
        Player(int inputLocationX, int inputLocationY, int inputLocationZ)
        {
            super(inputLocationX, inputLocationY, inputLocationZ);
        }

        Player(@NotNull Player inputPlayer)
        {
            super(inputPlayer.getCurrentLocation());
        }
    }

    public Player getPlayerOne()
    {
        return this.playerOne;
    }

    public GameFieldObject getGameField()
    {
        return this.gameField;
    }
}