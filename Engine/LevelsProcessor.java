package Game.Engine;

import Game.Engine.GameObjects.*;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Class for level production
 **/
public class LevelsProcessor
{
    public class SinglePlayerLevel
    {
        public final int[] gameFieldSize;

        public Player player;
        public ArrayList<MortalObject> mobs;
        public HashMap<Integer, ArrayList<MortalObject>> nonSpawnedMobs;
        public ArrayList<MovableObject> projectiles = new ArrayList<>();

        public SinglePlayerLevel(
            int[] inputGameFieldSize,
            Player inputPlayer,
            ArrayList<MortalObject> inputMobs,
            HashMap<Integer, ArrayList<MortalObject>> inputNonSpawnedMobs)
        {
            this.gameFieldSize = inputGameFieldSize;
            this.player = inputPlayer;
            this.mobs = inputMobs;
            this.nonSpawnedMobs = inputNonSpawnedMobs;
        }
    }

//
// Auto moving constants
//
    private int SPHERE_MOB_MOVING_SPEED = 1;  // TODO

    private final int[] MOVEMENT_TO_THE_RIGHT = new int[] { 1, 0, 0 };

    private static int[] productAutoMovingVector(
        int[] directionVectorConstant,
        int movingSpeedConstant)
    {
        return new int[] {
            directionVectorConstant[0] * movingSpeedConstant,
            directionVectorConstant[1] * movingSpeedConstant,
            directionVectorConstant[2] * movingSpeedConstant };
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

        SphereMob firstWaveSphereMob =
            gameObjects.new SphereMob(
            new int[]{ -400, 300, 0 },
            productAutoMovingVector(
                MOVEMENT_TO_THE_RIGHT,
                SPHERE_MOB_MOVING_SPEED),
            5);

        mobs.add(firstWaveSphereMob);
        for (int i = 0; i < 10; i++)
            try
            {
                firstWaveSphereMob =
                    (SphereMob) firstWaveSphereMob.clone();
                firstWaveSphereMob.modifyLocation(new int[]{ 30, -25, 0 });

                mobs.add(firstWaveSphereMob);
            }
            catch (CloneNotSupportedException occurredExc)
            {
                occurredExc.printStackTrace();
            }

        return new SinglePlayerLevel(
            gameFieldSize,
            player,
            mobs,
            new HashMap<>());
    }
}