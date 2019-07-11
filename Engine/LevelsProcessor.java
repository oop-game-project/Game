package Game.Engine;

import Game.Engine.GameObjects.*;

public class LevelsProcessor
{
    public class SinglePlayerLevel
    {
        public final int[] gameFieldSize;

        public GameObjects.Player player;
        public GameObjects.MovableObject[] mobs;

        public SinglePlayerLevel(
            int[] inputGameFieldSize,
            Player inputPlayer,
            MovableObject[] inputMobs)
        {
            this.gameFieldSize = inputGameFieldSize;
            this.player = inputPlayer;
            this.mobs = inputMobs;
        }
    }

//
//  Manual level init section
//

    public SinglePlayerLevel getLevelOne()
    {
        GameObjects gameObjects = new GameObjects();

        int[] gameFieldSize = new int[] { 700, 700, 0 };

        Player player = gameObjects.new Player(new int[] { 175, 350, 0 }, 20);

        MovableObject[] mobs = new GameObjects.MovableObject[3];
        mobs[0] = gameObjects.new SphereMob(
            new int[]{420, 175, 0},
            5);
        mobs[1] = gameObjects.new SphereMob(
            new int[]{350, 250, 0},
            5);
        mobs[2] = gameObjects.new SphereMob(
            new int[]{420, 420, 0},
            5);

        return new SinglePlayerLevel(gameFieldSize, player, mobs);
    }
}