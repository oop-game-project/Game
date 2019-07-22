package Game.Engine;

import Game.Engine.GameObjects.*; // TODO: get rid of '*'

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LevelsProcessor
{
    public class SinglePlayerLevel
    {
        public final int[] gameFieldSize;

        public Player player;
        public ArrayList<MortalObject> mobs;
        public ArrayList<MovableObject> projectiles;
        public ArrayList<InterfaceObject> interfaceObjects;

        public SinglePlayerLevel(
            int[] inputGameFieldSize,
            Player inputPlayer,
            ArrayList<MortalObject> inputMobs,
            ArrayList<MovableObject> inputProjectiles,
            ArrayList<InterfaceObject> inputInterfaceObjects)
        {
            this.gameFieldSize = inputGameFieldSize;
            this.player = inputPlayer;
            this.mobs = inputMobs;
            this.projectiles = inputProjectiles;
            this.interfaceObjects = inputInterfaceObjects;
        }
    }

//
// Manual level init section
//

    public SinglePlayerLevel getLevelOne()
    {
        GameObjects gameObjects = new GameObjects();

        int[] gameFieldSize = new int[] { 700, 700, 0 };

        Player player = gameObjects.new Player(
            new int[] { 175, 350, 0 },
            20);

        ArrayList<MortalObject> mobs = new ArrayList<>();
        mobs.add(gameObjects.new SphereMob(
            new int[]{420, 175, 1},
            5));
        mobs.add(gameObjects.new SphereMob(
            new int[]{350, 250, 0},
            5));
        mobs.add(gameObjects.new SphereMob(
            new int[]{420, 420, -1},
            5));

        return new SinglePlayerLevel(
            gameFieldSize,
            player,
            mobs,
            new ArrayList<>(),
            new ArrayList<>());
    }
}