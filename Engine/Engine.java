package Game.Engine;


import org.jetbrains.annotations.NotNull;

class Engine
{
    private LevelsProcessor.SinglePlayerLevel currentLevel;

    // TODO : constructor with "String levelFileName"
    public Engine(LevelsProcessor.SinglePlayerLevel inputLevel)
    {
        this.currentLevel = inputLevel;
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