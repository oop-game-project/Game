package Game.Engine;

public class LevelsProcessor
{
    public class SinglePlayerLevel
    {
        public final int[] gameFieldSize;

        public GameObjects.Player player;
        public GameObjects.MovableObject[] mobs;

        public SinglePlayerLevel()
        {
            // TODO: Вытащить ручную инициализацию конкретного уровня из конструктора общего класса

            GameObjects gameObjects = new GameObjects();

            this.gameFieldSize = new int[] { 700, 700, 0 };

            this.player = gameObjects.new Player(new int[] { 175, 350, 0 }, 20);
            this.mobs = new GameObjects.MovableObject[3];
            this.mobs[0] = gameObjects.new SphereMob(
                    new int[] { 420, 175, 0 },
                    5,
                    20);
            this.mobs[1] = gameObjects.new SphereMob(
                    new int[] { 350, 250, 0 },
                    5,
                    20);
            this.mobs[2] = gameObjects.new SphereMob(
                    new int[] { 420, 420, 0 },
                    5,
                    20);
        }

//        private SinglePlayerLevel parseLevelFile(String filename)
//        {
//            //TODO parse level in file
//        }
    }
}