import Engine.CollisionsProcessor;
import Engine.GameObjects.*;
import Engine.LevelsProcessor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CollisionsProcessorTest {
    private LevelsProcessor levelsProcessor = new LevelsProcessor();
    private Player player = new Player(new int[] {300, 300, 0});
    private int[] fieldSize = new int[] {700, 700, 0};
    private CollisionsProcessor collisionsProcessor = new CollisionsProcessor(fieldSize);
    private CollisionsProcessor.Collision collision;


    @Test
    void getCollisionOK() {
        ArrayList<MortalObject> mobs = new ArrayList<>();
        mobs.add(new SphereMob(new int[] {100, 100, 0}, new int[] {0,0,0}, false));
        mobs.add(new SphereMob(new int[] {200, 100, 0}, new int[] {0,0,0}, false));
        mobs.add(new SphereMob(new int[] {100, 200, 0}, new int[] {0,0,0}, false));
        mobs.add(new SphereMob(new int[] {300, 300, 1}, new int[] {0,0,0}, false));
        SphereBoss sphereBoss = new SphereBoss(new int[] {200, 200, 1},
                                                                        new int [] {0, 0, 0}, false);

        LevelsProcessor.GameLevel level = levelsProcessor.new GameLevel(
                fieldSize, player, mobs, sphereBoss, null);
        level.projectiles.add(new BasicProjectile(new int[]{250, 250, 0}, mobs.get(0)));
        level.projectiles.add(new BasicProjectile(new int[]{300, 300, 0}, player));
        collision = collisionsProcessor.getCollision(level, new int[]{0, 0, 0}, player);
        assertEquals(collision.event, CollisionsProcessor.GameEvent.OK);

    }
    @Test
    void getPlayerCollisions() {
        LevelsProcessor.GameLevel level = levelsProcessor.new GameLevel(
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
        level.mobs.add(new SphereMob(new int[] {280, 280, 0}, new int[] {0,0,0}, false));
        collision = collisionsProcessor.getCollision(level, new int[]{0,0,0}, player);
        assertEquals(collision.event, CollisionsProcessor.GameEvent.PLAYER_COLLIDED_SPHERE_MOB);
        level.mobs.clear();
        level.mobs.add(new SphereBoss(new int[] {100, 100, 0}, new int[] {0,0,0}, false));
        collision = collisionsProcessor.getCollision(level, new int[]{0,0,0}, player);
        assertEquals(collision.event, CollisionsProcessor.GameEvent.PLAYER_COLLIDED_SPHERE_BOSS);
        level.mobs.clear();
        SphereBoss sphereBoss = new SphereBoss(new int[] {600, 100, 0}, new int[] {0,0,0}, false);
        level.projectiles.add(new BasicProjectile(new int[] {290, 290, 0}, sphereBoss));
        collision = collisionsProcessor.getCollision(level, new int[]{0,0,0}, player);
        assertEquals(collision.event, CollisionsProcessor.GameEvent.PLAYER_COLLIDED_BASIC_PROJECTILE);
    }
    @Test
    void getSphereMobCollision() {
        LevelsProcessor.GameLevel level = levelsProcessor.new GameLevel(
                fieldSize, player, new ArrayList<>(), null, null);
        SphereMob sphereMob = new SphereMob(new int[]{300, 290, 0}, new int[]{0,0,0}, false);
        level.mobs.add(sphereMob);
        collision = collisionsProcessor.getCollision(level, new int[]{0,0,0}, sphereMob);
        assertEquals(collision.event, CollisionsProcessor.GameEvent.SPHERE_MOB_COLLIDED_PLAYER);
        sphereMob.setCurrentLocation(new int[]{100, 100, 0});
        level.projectiles.add(new BasicProjectile(new int[]{90, 90, 0}, player));
        collision = collisionsProcessor.getCollision(level, new int[]{0,0,0}, sphereMob);
        assertEquals(collision.event, CollisionsProcessor.GameEvent.SPHERE_MOB_COLLIDED_BASIC_PROJECTILE);
    }
    @Test
    void getSphereBossCollision() {
        LevelsProcessor.GameLevel level = levelsProcessor.new GameLevel(
                fieldSize, player, new ArrayList<>(), null, null);
        SphereBoss sphereBoss = new SphereBoss(new int[]{100, 300, 0}, new int[]{0,0,0}, false);
        level.mobs.add(sphereBoss);
        collision = collisionsProcessor.getCollision(level, new int[]{0,0,0}, sphereBoss);
        assertEquals(collision.event, CollisionsProcessor.GameEvent.SPHERE_BOSS_COLLIDED_PLAYER);
        sphereBoss.setCurrentLocation(new int[]{50, 50, 0});
        level.projectiles.add(new BasicProjectile(new int[]{90, 90, 0}, player));
        collision = collisionsProcessor.getCollision(level, new int[]{0,0,0}, sphereBoss);
        assertEquals(collision.event, CollisionsProcessor.GameEvent.SPHERE_BOSS_COLLIDED_BASIC_PROJECTILE);
    }
    void getBasicProjectileCollision() {
        ArrayList<MortalObject> mobs = new ArrayList<>();
        mobs.add(new SphereMob(new int[] {100, 100, 0}, new int[] {0,0,0}, false));
        SphereBoss sphereBoss = new SphereBoss(new int[] {600, 600, 0},
                                                                        new int [] {0, 0, 0}, false);
        LevelsProcessor.GameLevel level = levelsProcessor.new GameLevel(
                fieldSize, player, mobs, sphereBoss, null);
        BasicProjectile projectileKilledPlayer = new BasicProjectile(new int[]{290, 290, 0}, sphereBoss);
        level.projectiles.add(projectileKilledPlayer);
        collision = collisionsProcessor.getCollision(level, new int[]{0,0,0}, projectileKilledPlayer);
        assertEquals(collision.event, CollisionsProcessor.GameEvent.BASIC_PROJECTILE_COLLIDED_PLAYER);
        BasicProjectile projectileKilledMob = new BasicProjectile(new int[]{90, 100, 0}, player);
        level.projectiles.add(projectileKilledMob);
        collision = collisionsProcessor.getCollision(level, new int[]{0,0,0}, projectileKilledMob);
        assertEquals(collision.event, CollisionsProcessor.GameEvent.BASIC_PROJECTILE_COLLIDED_SPHERE_MOB);
        BasicProjectile projectileKilledBoss = new BasicProjectile(new int[]{600, 600, 0}, player);
        level.projectiles.add(projectileKilledBoss);
        collision = collisionsProcessor.getCollision(level, new int[]{0,0,0}, projectileKilledBoss);
        assertEquals(collision.event, CollisionsProcessor.GameEvent.BASIC_PROJECTILE_COLLIDED_SPHERE_BOSS);
    }
}