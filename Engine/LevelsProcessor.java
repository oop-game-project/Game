package Engine;

import Engine.GameObjects.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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
        public MortalObject boss;
        public ArrayList<MovableObject> projectiles = new ArrayList<>();
        public ArrayList<InterfaceObject> interfaceObjects = new ArrayList<>();


        /**
         * Long -> game loop iterations count passed from game start.
         **/
        public HashMap<Long, ArrayList<MortalObject>> nonSpawnedMobs;

        public SinglePlayerLevel(
            int[] inputGameFieldSize,
            Player inputPlayer,
            ArrayList<MortalObject> inputMobs,
            MortalObject inputBoss,
            HashMap<Long, ArrayList<MortalObject>> inputNonSpawnedMobs)
        {
            this.gameFieldSize = inputGameFieldSize;
            this.player = inputPlayer;
            this.mobs = inputMobs;
            this.boss = inputBoss;
            this.nonSpawnedMobs = inputNonSpawnedMobs;
        }
    }

//
// Auto moving constants
//
    public static final int MINIMAL_MOVING_SPEED = 1;
    public static final int SPHERE_BOSS_MOVING_SPEED = 2;

    private static final int[] MOVEMENT_RIGHT = new int[] { 1, 0, 0 };
    private static final int[] MOVEMENT_DOWN = new int[] { 0, 1, 0 };

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    private static int[] productAutoMovingVector(
        @NotNull int[] directionVectorConstant,
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

    @NotNull
    private ArrayList<MortalObject> createWaveOfSphereMobs(
        SphereMob waveMob, int[] locationModifier, int mobsCount)
    {
        ArrayList<MortalObject> mobs = new ArrayList<>();

        mobs.add(waveMob);
        for (int i = 0; i < mobsCount; i++)
        {
            waveMob =
                new SphereMob(
                    waveMob.currentLocation,
                    waveMob.autoMovingVector,
                    waveMob.borderWasCrossed);
            waveMob.modifyLocation(locationModifier);

            mobs.add(waveMob);
        }

        return mobs;
    }

    @NotNull
    private ArrayList<MortalObject> getLevelOneMobs()
    {
        ArrayList<MortalObject> mobs;

        SphereMob firstWaveSphereMob =
            new SphereMob(
                new int[]{ -400, 300, 0 },
                productAutoMovingVector(
                    MOVEMENT_RIGHT,
                    MINIMAL_MOVING_SPEED),
                false);
        mobs = new ArrayList<>(this.createWaveOfSphereMobs(
            firstWaveSphereMob,
            new int[]{30, -25, 0},
            11));

        return mobs;
    }

    @NotNull
    private HashMap<Long, ArrayList<MortalObject>>
        getLevelOneNonSpawnedMobs(SphereBoss sphereBoss)
    {
        HashMap<Long, ArrayList<MortalObject>> nonSpawnedMobs = new HashMap<>();

        SphereMob secondWaveSphereMob =
            new SphereMob(
                new int[]{ 20, -30, 0 },
                productAutoMovingVector(
                    MOVEMENT_DOWN,
                    MINIMAL_MOVING_SPEED),
                false);

        nonSpawnedMobs.put(
            1200L,
            this.createWaveOfSphereMobs(
                secondWaveSphereMob,
                new int[]{ 45, 0, 0 },
                14));

        ArrayList<MortalObject> sphereBossArray = new ArrayList<>();
        sphereBossArray.add(sphereBoss);
        nonSpawnedMobs.put(
            2000L,
            sphereBossArray);

        return nonSpawnedMobs;
    }

    public SinglePlayerLevel getLevelOne()
    {
        Player levelOnePlayer = new Player(
            new int[] { 175, 350, 0 });

        SphereBoss levelOneSphereBoss = new SphereBoss(
            new int[]{ 250, -200, 1 },
            productAutoMovingVector(
                MOVEMENT_DOWN,
                MINIMAL_MOVING_SPEED),
            false);

        return new SinglePlayerLevel(
            new int[] { 700, 700, 0 },
            levelOnePlayer,
            this.getLevelOneMobs(),
            levelOneSphereBoss,
            this.getLevelOneNonSpawnedMobs(levelOneSphereBoss));
    }
}