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
    private int SPHERE_MOB_MOVING_SPEED = 1;
    private int SPHERE_BOSS_MOVING_SPEED = 2;

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

    private ArrayList<MortalObject> createWaveOfSphereMobs(
        SphereMob waveMob, int[] locationModifier, int mobsCount)
    {
        ArrayList<MortalObject> mobs = new ArrayList<>();

        mobs.add(waveMob);
        for (int i = 0; i < mobsCount; i++)
        {
            waveMob =
                this.gameObjects.new SphereMob(
                    waveMob.currentLocation,
                    waveMob.autoMovingVector,
                    waveMob.borderWasCrossed);
            waveMob.modifyLocation(locationModifier);

            mobs.add(waveMob);
        }

        return mobs;
    }

    private ArrayList<MortalObject> getLevelOneMobs()
    {
        ArrayList<MortalObject> mobs;

        SphereMob firstWaveSphereMob =
            this.gameObjects.new SphereMob(
                new int[]{ -400, 300, 0 },
                productAutoMovingVector(
                    MOVEMENT_RIGHT,
                    SPHERE_MOB_MOVING_SPEED),
                false);
        mobs = new ArrayList<>(this.createWaveOfSphereMobs(
            firstWaveSphereMob,
            new int[]{30, -25, 0},
            11));

        mobs.add(this.gameObjects.new SphereBoss(
            new int[]{ 250, -200, 0 },
            productAutoMovingVector(
                MOVEMENT_RIGHT,
                SPHERE_BOSS_MOVING_SPEED),
            false));

        return mobs;
    }

    private HashMap<Long, ArrayList<MortalObject>>
        getLevelOneNonSpawnedMobs()
    {
        HashMap<Long, ArrayList<MortalObject>> nonSpawnedMobs = new HashMap<>();

        SphereMob secondWaveSphereMob =
            this.gameObjects.new SphereMob(
                new int[]{ 20, -30, 0 },
                productAutoMovingVector(
                    MOVEMENT_DOWN,
                    SPHERE_MOB_MOVING_SPEED),
                false);

        nonSpawnedMobs.put(
            20000L,
            this.createWaveOfSphereMobs(
                secondWaveSphereMob,
                new int[]{ 45, 0, 0 },
                14));

        return nonSpawnedMobs;
    }

    public SinglePlayerLevel getLevelOne()
    {
        Player levelOnePlayer = this.gameObjects.new Player(
            new int[] { 175, 350, 0 });

        return new SinglePlayerLevel(
            new int[] { 700, 700, 0 },
            levelOnePlayer,
            this.getLevelOneMobs(),
            this.getLevelOneNonSpawnedMobs());
    }
}