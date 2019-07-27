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
        public HashMap<Long, ArrayList<MortalObject>> nonSpawnedMobs;
        public ArrayList<MovableObject> projectiles = new ArrayList<>();

        public SinglePlayerLevel(
            int[] inputGameFieldSize,
            Player inputPlayer,
            ArrayList<MortalObject> inputMobs,
            HashMap<Long, ArrayList<MortalObject>> inputNonSpawnedMobs)
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

    private final int[] MOVEMENT_RIGHT = new int[] { 1, 0, 0 };
    private final int[] MOVEMENT_DOWN = new int[] { 0, 1, 0 };

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

    GameObjects gameObjects = new GameObjects();

    private ArrayList<MortalObject> getLevelOneMobs()
    {
        ArrayList<MortalObject> mobs = new ArrayList<>();

        SphereMob firstWaveSphereMob =
            this.gameObjects.new SphereMob(
                new int[]{ -400, 300, 0 },
                productAutoMovingVector(
                    MOVEMENT_RIGHT,
                    SPHERE_MOB_MOVING_SPEED),
                5);

        mobs.add(firstWaveSphereMob);
        for (int i = 0; i < 10; i++)
        {
            try
            {
                firstWaveSphereMob =
                    (SphereMob) firstWaveSphereMob.clone();
                firstWaveSphereMob.modifyLocation(new int[]{30, -25, 0});

                mobs.add(firstWaveSphereMob);
            }
            catch (CloneNotSupportedException occurredExc)
            {
                occurredExc.printStackTrace();
            }
        }

        return mobs;
    }

    private HashMap<Long, ArrayList<MortalObject>>
        getLevelOneNonSpawnedMobs()
    {
        HashMap<Long, ArrayList<MortalObject>> nonSpawnedMobs = new HashMap<>();

        ArrayList<MortalObject> secondWaveMobs = new ArrayList<>();

        SphereMob secondWaveSphereMob =
            this.gameObjects.new SphereMob(
                new int[]{ 20, -30, 0 },
                productAutoMovingVector(
                    MOVEMENT_DOWN,
                    SPHERE_MOB_MOVING_SPEED),
                5);

        secondWaveMobs.add(secondWaveSphereMob);
        for (int i = 0; i < 14; i++)
        {
            try
            {
                secondWaveSphereMob =
                    (SphereMob) secondWaveSphereMob.clone();
                secondWaveSphereMob.modifyLocation(new int[]{ 45, 0, 0 });

                secondWaveMobs.add(secondWaveSphereMob);
            }
            catch (CloneNotSupportedException occurredExc)
            {
                occurredExc.printStackTrace();
            }
        }

        nonSpawnedMobs.put(20000L, secondWaveMobs);

        return nonSpawnedMobs;
    }

    public SinglePlayerLevel getLevelOne()
    {
        Player levelOnePlayer = this.gameObjects.new Player(
            new int[] { 175, 350, 0 },
            20);

        return new SinglePlayerLevel(
            new int[] { 700, 700, 0 },
            levelOnePlayer,
            this.getLevelOneMobs(),
            this.getLevelOneNonSpawnedMobs());
    }
}