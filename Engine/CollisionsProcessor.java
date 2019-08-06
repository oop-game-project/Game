package Engine;

import Engine.GameObjects.*;
import Engine.LevelsProcessor.SinglePlayerLevel;
import org.lwjgl.system.CallbackI;

import java.util.ArrayList;

public class CollisionsProcessor {

    private int sizeX;
    private int sizeY;
    private int sizeZ;

    public CollisionsProcessor(int[] gameFieldSize) {

        sizeX = gameFieldSize[0];
        sizeY = gameFieldSize[1];
        sizeZ = gameFieldSize[2];
    }

    public Collision getCollision(SinglePlayerLevel currentLevel, int[] moveVector, MovableObject movableObject) {

        if (movableObject instanceof Player) {
            return getPlayerCollision((Player)movableObject, moveVector, currentLevel);
        }
        if (movableObject instanceof BasicProjectile) {
            return getBasicProjectileCollision((BasicProjectile)movableObject, moveVector, currentLevel);
        }
        if (movableObject instanceof SphereMob) {
            return getSphereMobCollision((SphereMob)movableObject, moveVector, currentLevel);
        }
        if (movableObject instanceof SphereBoss) {
            return getSphereBossCollision((SphereBoss)movableObject, moveVector, currentLevel);
        }
        throw new IllegalArgumentException("unknown movableObject");
    }

    private Collision getPlayerCollision(Player player, int[] moveVector, SinglePlayerLevel currentLevel) {

        if (playerOutOfHorizontalBorders(moveVector[0], player.currentLocation[0]) &&
                playerOutOfVerticalBorders(moveVector[1], player.currentLocation[1]))
            return new Collision(player, GameEvent.OUT_DIAGONAL, null);
        if (playerOutOfHorizontalBorders(moveVector[0], player.currentLocation[0]))
            return new Collision(player, GameEvent.OUT_HORIZONTAL, null);
        if (playerOutOfVerticalBorders(moveVector[1], player.currentLocation[1]))
            return new Collision(player, GameEvent.OUT_VERTICAL, null);

        for (int i = 0; i < currentLevel.projectiles.size(); i++) {
            BasicProjectile projectile = (BasicProjectile)currentLevel.projectiles.get(i);
            if (projectile.currentLocation[2] != 0) continue;
            if (!projectile.firedByPlayer &&
                    checkCollided(player, projectile, PaintingConst.PLAYER_SIDE_LENGTH,
                            PaintingConst.BASIC_PROJECTILE_DIAMETER, PaintingConst.PLAYER_SIDE_LENGTH / 2))
                return new Collision(player, GameEvent.PLAYER_COLLIDED_BASIC_PROJECTILE, projectile);
        }
        for (int i = 0; i < currentLevel.mobs.size(); i++) {
            MovableObject sphereMob = currentLevel.mobs.get(i);
            if (sphereMob.currentLocation[2] != 0) continue;
            if (sphereMob instanceof SphereBoss)
                if (checkCollided(player, sphereMob, PaintingConst.PLAYER_SIDE_LENGTH,
                        PaintingConst.SPHERE_BOSS_DIAMETER))
                    return new Collision(player, GameEvent.PLAYER_COLLIDED_SPHERE_BOSS, sphereMob);
            if (checkCollided(player, sphereMob, PaintingConst.PLAYER_SIDE_LENGTH,
                    PaintingConst.SPHERE_MOB_DIAMETER))
                return new Collision(player, GameEvent.PLAYER_COLLIDED_SPHERE_MOB, sphereMob);
        }

        return new Collision(player, GameEvent.OK, null);
    }

    private Collision getBasicProjectileCollision(BasicProjectile basicProjectile, int[] moveVector,
                                                  SinglePlayerLevel currentLevel) {

        if (basicProjectile.currentLocation[1] > sizeY ||
                basicProjectile.currentLocation[1] < 0)
            return new Collision(basicProjectile, GameEvent.BASIC_PROJECTILE_IS_OUT, null);
        if (basicProjectile.currentLocation[2] == 0 && !basicProjectile.firedByPlayer &&
                checkCollided(currentLevel.player, basicProjectile, PaintingConst.PLAYER_SIDE_LENGTH,
                        PaintingConst.BASIC_PROJECTILE_DIAMETER, PaintingConst.PLAYER_SIDE_LENGTH / 2))
            return new Collision(basicProjectile, GameEvent.BASIC_PROJECTILE_COLLIDED_PLAYER, currentLevel.player);
        for (int i = 0; i < currentLevel.mobs.size(); i++) {
            MovableObject sphereMob = currentLevel.mobs.get(i);
            if (sphereMob.currentLocation[2] != basicProjectile.currentLocation[2]) continue;
            if (sphereMob instanceof SphereBoss)
                if (basicProjectile.firedByPlayer &&
                        checkCollided(basicProjectile, sphereMob, PaintingConst.BASIC_PROJECTILE_DIAMETER,
                                PaintingConst.SPHERE_BOSS_DIAMETER))
                    return new Collision(basicProjectile, GameEvent.BASIC_PROJECTILE_COLLIDED_SPHERE_BOSS, sphereMob);
            if (basicProjectile.firedByPlayer &&
                    checkCollided(basicProjectile, sphereMob, PaintingConst.BASIC_PROJECTILE_DIAMETER,
                            PaintingConst.SPHERE_MOB_DIAMETER))
                return new Collision(basicProjectile, GameEvent.BASIC_PROJECTILE_COLLIDED_SPHERE_MOB, sphereMob);
        }

        return new Collision(basicProjectile, GameEvent.OK, null);
    }

    private Collision getSphereMobCollision(SphereMob sphereMob, int[] moveVector, SinglePlayerLevel currentLevel) {
        if (sphereMob.currentLocation[2] == 0 &&
                checkCollided(currentLevel.player, sphereMob, PaintingConst.PLAYER_SIDE_LENGTH,
                        PaintingConst.SPHERE_MOB_DIAMETER, PaintingConst.PLAYER_SIDE_LENGTH / 2))
            return new Collision(sphereMob, GameEvent.SPHERE_MOB_COLLIDED_PLAYER, currentLevel.player);
        if (sphereMob.currentLocation[0] > sizeX || sphereMob.currentLocation[0] < 0)
            return new Collision(sphereMob, GameEvent.SPHERE_MOB_IS_OUT_HORIZONTAL, null);
        if (sphereMob.currentLocation[1] > sizeY || sphereMob.currentLocation[1] < 0)
            return new Collision(sphereMob, GameEvent.SPHERE_MOB_IS_OUT_VERTICAL, null);

        for (int i = 0; i < currentLevel.projectiles.size(); i++) {
            BasicProjectile basicProjectile = (BasicProjectile)currentLevel.projectiles.get(i);
            if (basicProjectile.currentLocation[2] != sphereMob.currentLocation[2]) continue;
            if (basicProjectile.firedByPlayer &&
                    checkCollided(basicProjectile, sphereMob, PaintingConst.BASIC_PROJECTILE_DIAMETER,
                            PaintingConst.SPHERE_MOB_DIAMETER))
                return new Collision(sphereMob, GameEvent.SPHERE_MOB_COLLIDED_BASIC_PROJECTILE, basicProjectile);
        }


        return new Collision(sphereMob, GameEvent.OK, null);
    }

    private Collision getSphereBossCollision(SphereBoss sphereBoss, int[] moveVector, SinglePlayerLevel currentLevel) {
        if (sphereBoss.currentLocation[1] < 50)
            return new Collision(sphereBoss, GameEvent.SPHERE_BOSS_IS_GOING, null);
        if (sphereBoss.currentLocation[2] == 0 &&
                checkCollided(currentLevel.player, sphereBoss, PaintingConst.PLAYER_SIDE_LENGTH,
                PaintingConst.SPHERE_BOSS_DIAMETER, PaintingConst.PLAYER_SIDE_LENGTH / 2))
            new Collision(sphereBoss, GameEvent.SPHERE_BOSS_COLLIDED_PLAYER, currentLevel.player);
        if (sphereBoss.currentLocation[0] <= 0 || sphereBoss.currentLocation[0] + PaintingConst.SPHERE_BOSS_DIAMETER >= sizeY)
            return new Collision(sphereBoss, GameEvent.SPHERE_BOSS_IS_OUT_HORIZONTAL, null);
        for (int i = 0; i < currentLevel.projectiles.size(); i++) {
            BasicProjectile projectile = (BasicProjectile)currentLevel.projectiles.get(i);
            if (projectile.currentLocation[2] != sphereBoss.currentLocation[2]) continue;
            if (projectile.firedByPlayer &&
            checkCollided(projectile, sphereBoss, PaintingConst.BASIC_PROJECTILE_DIAMETER,
                    PaintingConst.SPHERE_BOSS_DIAMETER))
                return new Collision(sphereBoss, GameEvent.SPHERE_BOSS_COLLIDED_BASIC_PROJECTILE, projectile);
        }
        return new Collision(sphereBoss, GameEvent.OK, null);
    }

    private boolean checkCollided(MovableObject firstObject, MovableObject secondObject,
                                  int firstConst, int secondConst, int accurary) {
        return firstObject.currentLocation[0] - accurary + firstConst >= secondObject.currentLocation[0] &&
                firstObject.currentLocation[0] - accurary <= secondObject.currentLocation[0] + secondConst &&
                firstObject.currentLocation[1] <= secondObject.currentLocation[1] + secondConst &&
                firstObject.currentLocation[1] + firstConst >= secondObject.currentLocation[1];
    }
    private boolean checkCollided(MovableObject firstObject, MovableObject secondObject,
                                  int firstConst, int secondConst) {
        return checkCollided(firstObject, secondObject, firstConst, secondConst, 0);
    }

    private boolean playerOutOfHorizontalBorders(int vector, int player) {
        return (player + vector + PaintingConst.PLAYER_SIDE_LENGTH / 2 > sizeX) ||
                (player + vector - PaintingConst.PLAYER_SIDE_LENGTH / 2 < 0);
    }

    private boolean playerOutOfVerticalBorders(int vector, int player) {
        return (player + vector + PaintingConst.PLAYER_SIDE_LENGTH > sizeY) || (player + vector < 0);
    }

    public enum GameEvent {
        OK,

        OUT_DIAGONAL,
        OUT_HORIZONTAL,
        OUT_VERTICAL,

        BASIC_PROJECTILE_COLLIDED_PLAYER,
        BASIC_PROJECTILE_COLLIDED_SPHERE_BOSS,
        BASIC_PROJECTILE_COLLIDED_SPHERE_MOB,
        BASIC_PROJECTILE_IS_OUT,

        PLAYER_COLLIDED_BASIC_PROJECTILE,
        PLAYER_COLLIDED_SPHERE_BOSS,
        PLAYER_COLLIDED_SPHERE_MOB,

        SPHERE_BOSS_COLLIDED_BASIC_PROJECTILE,
        SPHERE_BOSS_COLLIDED_PLAYER,
        SPHERE_BOSS_IS_GOING,
        SPHERE_BOSS_IS_OUT_HORIZONTAL,

        SPHERE_MOB_COLLIDED_BASIC_PROJECTILE,
        SPHERE_MOB_COLLIDED_PLAYER,
        SPHERE_MOB_IS_OUT_HORIZONTAL,
        SPHERE_MOB_IS_OUT_VERTICAL
    }

    public class Collision {
        public final MovableObject movingObject;
        public final GameEvent event;
        public final MovableObject collidedObject;

        public Collision(MovableObject inputMovingObject, GameEvent inputEvent, MovableObject inputCollidedObject) {
            this.movingObject = inputMovingObject;
            this.event = inputEvent;
            this.collidedObject = inputCollidedObject;
        }
    }

}
