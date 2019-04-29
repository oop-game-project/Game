package Game.Engine;

public class LevelsProcessor
{
    public class SinglePlayerLevel
    {
        private GameObjects.GameField gameField;
        private GameObjects.Player playerOne;
        private GameObjects.MovableObject[] mobs;

        SinglePlayerLevel()
        {
            GameObjects gameObjects = new GameObjects();
            gameField = gameObjects.new GameField(700, 700, 0);
            playerOne = gameObjects.new Player(175, 350, 0);
            mobs = new GameObjects.MovableObject[3];
            mobs[0] = gameObjects.new SphereMob(420, 175, 0, 50);
            mobs[1] = gameObjects.new SphereMob(350, 250, 0, 50);
            mobs[2] = gameObjects.new SphereMob(420, 420, 0, 50);

        }

        private void parseLevelFile()
        {
            //TODO parse level in file
        }

        public GameObjects.GameField getGameField() { return gameField; }

        public GameObjects.Player getPlayerOne() { return playerOne; }

        public GameObjects.MovableObject[] getMobs() { return mobs; }
    }
    public SinglePlayerLevel getSinglePlayerLevel()
    {
        return new SinglePlayerLevel();
    }
}