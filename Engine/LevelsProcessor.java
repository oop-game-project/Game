package Game.Engine;

public class LevelsProcessor
{
    public class SinglePlayerLevel
    {
        public final int gameFieldSizeX;
        public final int gameFieldSizeY;
        public final int gameFieldSizeZ;

        public GameObjects.Player player;
        public GameObjects.MovableObject[] mobs;

        public SinglePlayerLevel()
        {
            // TODO: Вытащить ручную инициализацию конкретного уровня из конструктора общего класса

            GameObjects gameObjects = new GameObjects();

            this.gameFieldSizeX = 700;
            this.gameFieldSizeY = 700;
            this.gameFieldSizeZ = 0;

            this.player = gameObjects.new Player(new int[] { 175, 350, 0 });
            this.mobs = new GameObjects.MovableObject[3];
            this.mobs[0] = gameObjects.new SphereMob(new int[] { 420, 175, 0 }, 50);
            this.mobs[1] = gameObjects.new SphereMob(new int[] { 350, 250, 0 }, 50);
            this.mobs[2] = gameObjects.new SphereMob(new int[] { 420, 420, 0 }, 50);
        }

        private void parseLevelFile()
        {
            //TODO parse level in file
        }
    }
}