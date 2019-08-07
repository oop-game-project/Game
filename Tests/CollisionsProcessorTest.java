package Tests;

import Engine.CollisionsProcessor;
import Engine.GameObjects;
import Engine.LevelsProcessor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CollisionsProcessorTest {
    private LevelsProcessor levelsProcessor = new LevelsProcessor();
    private GameObjects gameObjects = new GameObjects();
    private GameObjects.Player player = gameObjects.new Player(new int[] {300, 300, 0});
    private int[] fieldSize = new int[] {700, 700, 0};
    private CollisionsProcessor collisionsProcessor = new CollisionsProcessor(fieldSize);
    private CollisionsProcessor.Collision collision;


    @Test
    void getCollisionOK() {
        ArrayList<GameObjects.MortalObject> mobs = new ArrayList<>();
        mobs.add(gameObjects.new SphereMob(new int[] {100, 100, 0}, new int[] {0,0,0}, false));
        mobs.add(gameObjects.new SphereMob(new int[] {200, 100, 0}, new int[] {0,0,0}, false));
        mobs.add(gameObjects.new SphereMob(new int[] {100, 200, 0}, new int[] {0,0,0}, false));
        mobs.add(gameObjects.new SphereMob(new int[] {300, 300, 1}, new int[] {0,0,0}, false));
        GameObjects.SphereBoss sphereBoss = gameObjects.new SphereBoss(new int[] {200, 200, 1},
                new int [] {0, 0, 0}, false);

        LevelsProcessor.SinglePlayerLevel level = levelsProcessor.new SinglePlayerLevel(
                fieldSize, player, mobs, sphereBoss, null);
        level.projectiles.add(gameObjects.new BasicProjectile(new int[]{250, 250, 0}, mobs.get(0)));
        level.projectiles.add(gameObjects.new BasicProjectile(new int[]{300, 300, 0}, player));
        collision = collisionsProcessor.getCollision(level, new int[]{0, 0, 0}, player);
        assertEquals(collision.event, CollisionsProcessor.GameEvent.OK);

    }
    @Test
    void getPlayerCollisions() {
        LevelsProcessor.SinglePlayerLevel level = levelsProcessor.new SinglePlayerLevel(
                fieldSize, player, new ArrayList<>(), null, null);
        collision = collisionsProcessor.getCollision(level, new int[]{401, 0, 0}, player);
        assertEquals(collision.event, CollisionsProcessor.GameEvent.OUT_HORIZONTAL);
        collision = collisionsProcessor.getCollision(level, new int[]{-301, 0, 0}, player);
        assertEquals(collision.event, CollisionsProcessor.GameEvent.OUT_HORIZONTAL);
        collision = collisionsProcessor.getCollision(level, new int[]{0, 401, 0}, player);
        assertEquals(collision.event, CollisionsProcessor.GameEvent.OUT_VERTICAL);
        collision = collisionsProcessor.getCollision(level, new int[]{0, -301, 0}, player);
        assertEquals(collision.event, CollisionsProcessor.GameEvent.OUT_VERTICAL);
        collision = collisionsProcessor.getCollision(level, new int[]{400, 400, 0}, player);
        assertEquals(collision.event, CollisionsProcessor.GameEvent.OUT_DIAGONAL);
        collision = collisionsProcessor.getCollision(level, new int[]{-400, -400, 0}, player);
        assertEquals(collision.event, CollisionsProcessor.GameEvent.OUT_DIAGONAL);
        collision = collisionsProcessor.getCollision(level, new int[]{400, -400, 0}, player);
        assertEquals(collision.event, CollisionsProcessor.GameEvent.OUT_DIAGONAL);
        collision = collisionsProcessor.getCollision(level, new int[]{-400, 400, 0}, player);
        assertEquals(collision.event, CollisionsProcessor.GameEvent.OUT_DIAGONAL);
        level.mobs.add(gameObjects.new SphereMob(new int[] {280, 280, 0}, new int[] {0,0,0}, false));
        collision = collisionsProcessor.getCollision(level, new int[]{0,0,0}, player);
        assertEquals(collision.event, CollisionsProcessor.GameEvent.PLAYER_COLLIDED_SPHERE_MOB);
        level.mobs.clear();
        level.mobs.add(gameObjects.new SphereBoss(new int[] {100, 100, 0}, new int[] {0,0,0}, false));
        collision = collisionsProcessor.getCollision(level, new int[]{0,0,0}, player);
        assertEquals(collision.event, CollisionsProcessor.GameEvent.PLAYER_COLLIDED_SPHERE_BOSS);
        level.mobs.clear();
        GameObjects.SphereBoss sphereBoss = gameObjects.new SphereBoss(new int[] {600, 100, 0}, new int[] {0,0,0},false);
        level.projectiles.add(gameObjects.new BasicProjectile(new int[] {290, 290, 0}, sphereBoss));
        collision = collisionsProcessor.getCollision(level, new int[]{0,0,0}, player);
        assertEquals(collision.event, CollisionsProcessor.GameEvent.PLAYER_COLLIDED_BASIC_PROJECTILE);
    }
    @Test
    void getSphereMobCollision() {
        LevelsProcessor.SinglePlayerLevel level = levelsProcessor.new SinglePlayerLevel(
                fieldSize, player, new ArrayList<>(), null, null);
        GameObjects.SphereMob sphereMob = gameObjects.new SphereMob(new int[]{300, 290, 0}, new int[]{0,0,0}, false);
        level.mobs.add(sphereMob);
        collision = collisionsProcessor.getCollision(level, new int[]{0,0,0}, sphereMob);
        assertEquals(collision.event, CollisionsProcessor.GameEvent.SPHERE_MOB_COLLIDED_PLAYER);
        sphereMob.currentLocation = new int[]{100, 100, 0};
        level.projectiles.add(gameObjects.new BasicProjectile(new int[]{90, 90, 0}, player));
        collision = collisionsProcessor.getCollision(level, new int[]{0,0,0}, sphereMob);
        assertEquals(collision.event, CollisionsProcessor.GameEvent.SPHERE_MOB_COLLIDED_BASIC_PROJECTILE);
    }
    @Test
    void getSphereBossCollision() {
        LevelsProcessor.SinglePlayerLevel level = levelsProcessor.new SinglePlayerLevel(
                fieldSize, player, new ArrayList<>(), null, null);
        GameObjects.SphereBoss sphereBoss = gameObjects.new SphereBoss(new int[]{100, 300, 0}, new int[]{0,0,0}, false);
        level.mobs.add(sphereBoss);
        collision = collisionsProcessor.getCollision(level, new int[]{0,0,0}, sphereBoss);
        assertEquals(collision.event, CollisionsProcessor.GameEvent.SPHERE_BOSS_COLLIDED_PLAYER);
        sphereBoss.currentLocation = new int[]{50, 50, 0};
        level.projectiles.add(gameObjects.new BasicProjectile(new int[]{90, 90, 0}, player));
        collision = collisionsProcessor.getCollision(level, new int[]{0,0,0}, sphereBoss);
        assertEquals(collision.event, CollisionsProcessor.GameEvent.SPHERE_BOSS_COLLIDED_BASIC_PROJECTILE);
    }
    void getBasicProjectileCollision() {
        ArrayList<GameObjects.MortalObject> mobs = new ArrayList<>();
        mobs.add(gameObjects.new SphereMob(new int[] {100, 100, 0}, new int[] {0,0,0}, false));
        GameObjects.SphereBoss sphereBoss = gameObjects.new SphereBoss(new int[] {600, 600, 0},
                new int [] {0, 0, 0}, false);
        LevelsProcessor.SinglePlayerLevel level = levelsProcessor.new SinglePlayerLevel(
                fieldSize, player, mobs, sphereBoss, null);
        GameObjects.BasicProjectile projectileKilledPlayer = gameObjects.new BasicProjectile(new int[]{290, 290, 0}, sphereBoss);
        level.projectiles.add(projectileKilledPlayer);
        collision = collisionsProcessor.getCollision(level, new int[]{0,0,0}, projectileKilledPlayer);
        assertEquals(collision.event, CollisionsProcessor.GameEvent.BASIC_PROJECTILE_COLLIDED_PLAYER);
        GameObjects.BasicProjectile projectileKilledMob = gameObjects.new BasicProjectile(new int[]{90, 100, 0}, player);
        level.projectiles.add(projectileKilledMob);
        collision = collisionsProcessor.getCollision(level, new int[]{0,0,0}, projectileKilledMob);
        assertEquals(collision.event, CollisionsProcessor.GameEvent.BASIC_PROJECTILE_COLLIDED_SPHERE_MOB);
        GameObjects.BasicProjectile projectileKilledBoss = gameObjects.new BasicProjectile(new int[]{600, 600, 0}, player);
        level.projectiles.add(projectileKilledBoss);
        collision = collisionsProcessor.getCollision(level, new int[]{0,0,0}, projectileKilledBoss);
        assertEquals(collision.event, CollisionsProcessor.GameEvent.BASIC_PROJECTILE_COLLIDED_SPHERE_BOSS);
    }
}