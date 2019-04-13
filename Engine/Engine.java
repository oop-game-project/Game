public class Engine
{
    public class GeometryVector
    {
        private int X;
        private int Y;
        private int Z;

        public GeometryVector(int inputX, int inputY, int inputZ)
        {
            this.X = inputX;
            this.Y = inputY;
            this.Z = inputZ;
        }

        public int[] getCoordinates() { return new int[] { this.X, this.Y, this.Z }; }

        public int getX() { return this.X; }

        public int getY() { return this.Y; }

        public int getZ() { return this.Z; }

    }
}