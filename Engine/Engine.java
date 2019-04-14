package Game.Engine;


import org.jetbrains.annotations.NotNull;

class Engine
{
    private GameObjects gameObjects;
    private GameObjects.GameField gameField;
    private GameObjects.Player playerOne;

    public Engine(
            @NotNull GameObjects.GameField inputGameField,
            @NotNull GameObjects.Player inputPlayerOne)
    {
        this.gameObjects = new GameObjects();
        this.gameField = gameObjects.new GameField(inputGameField);
        this.playerOne = gameObjects.new Player(inputPlayerOne);
    }

    class GeometryVector
    {
        private int X;
        private int Y;
        private int Z;

        GeometryVector(int inputX, int inputY, int inputZ)
        {
            this.X = inputX;
            this.Y = inputY;
            this.Z = inputZ;
        }

        int[] getCoordinates() { return new int[] { this.X, this.Y, this.Z }; }

        int getX() { return this.X; }

        int getY() { return this.Y; }

        int getZ() { return this.Z; }
    }
}