package Game.Engine;

import Game.Engine.GameObjects.*;

import java.util.ArrayList;


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
// Auto moving constants
//
    private int SPHERE_MOB_MOVING_SPEED = 5;

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
        mobs.add(gameObjects.new SphereMob(
            new int[]{420, 175, 0},
            productAutoMovingVector(
                MOVEMENT_TO_THE_RIGHT,
                SPHERE_MOB_MOVING_SPEED),
            5,
            0));
        mobs.add(gameObjects.new SphereMob(
            new int[]{420, 100, 1},
            productAutoMovingVector(
                MOVEMENT_TO_THE_RIGHT,
                SPHERE_MOB_MOVING_SPEED),
            5,
            0));
        mobs.add(gameObjects.new SphereMob(
            new int[]{420, 420, -1},
            productAutoMovingVector(
                MOVEMENT_TO_THE_RIGHT,
                SPHERE_MOB_MOVING_SPEED),
            5,
            0));

        return new SinglePlayerLevel(
            gameFieldSize,
            player,
            mobs,
            new ArrayList<>(),
            new ArrayList<>());
    }
}