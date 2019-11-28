package Engine;

import Engine.GameObjects.*;

public class CollisionsProcessor {

    private int sizeX;
    private int sizeY;
    private int sizeZ;

    public CollisionsProcessor(int[] gameFieldSize) {

        sizeX = gameFieldSize[0];
        sizeY = gameFieldSize[1];
        sizeZ = gameFieldSize[2];
    }

    public Collision getCollision(LevelsProcessor.GameLevel currentLevel, int[] moveVector, MovableObject movableObject) {

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

    private Collision getPlayerCollision(Player player, int[] moveVector, LevelsProcessor.GameLevel currentLevel) {

        if (playerOutOfHorizontalBorders(moveVector[0], player.getCurrentLocation()[0]) &&
                playerOutOfVerticalBorders(moveVector[1], player.getCurrentLocation()[1]))
            return new Collision(player, GameEvent.OUT_DIAGONAL, null);
        if (playerOutOfHorizontalBorders(moveVector[0], player.getCurrentLocation()[0]))
            return new Collision(player, GameEvent.OUT_HORIZONTAL, null);
        if (playerOutOfVerticalBorders(moveVector[1], player.getCurrentLocation()[1]))
            return new Collision(player, GameEvent.OUT_VERTICAL, null);

        for (int i = 0; i < currentLevel.projectiles.size(); i++) {
            BasicProjectile projectile = (BasicProjectile)currentLevel.projectiles.get(i);
            if (projectile.getCurrentLocation()[2] != 0) continue;
            if (!projectile.firedByPlayer &&
                    checkCollided(player, projectile, PaintingConst.PLAYER_SIDE_LENGTH,
                            PaintingConst.BASIC_PROJECTILE_DIAMETER, PaintingConst.PLAYER_SIDE_LENGTH / 2))
                return new Collision(player, GameEvent.PLAYER_COLLIDED_BASIC_PROJECTILE, projectile);
        }
        for (int i = 0; i < currentLevel.mobs.size(); i++) {
            MovableObject sphereMob = currentLevel.mobs.get(i);
            if (sphereMob.getCurrentLocation()[2] != 0) continue;
            if (sphereMob instanceof SphereBoss)
                if (checkCollided(player, sphereMob, PaintingConst.PLAYER_SIDE_LENGTH,
                        PaintingConst.SPHERE_BOSS_DIAMETER, PaintingConst.PLAYER_SIDE_LENGTH / 2))
                    return new Collision(player, GameEvent.PLAYER_COLLIDED_SPHERE_BOSS, sphereMob);
            if (checkCollided(player, sphereMob, PaintingConst.PLAYER_SIDE_LENGTH,
                    PaintingConst.SPHERE_MOB_DIAMETER, PaintingConst.PLAYER_SIDE_LENGTH / 2))
                return new Collision(player, GameEvent.PLAYER_COLLIDED_SPHERE_MOB, sphereMob);
        }

        return new Collision(player, GameEvent.OK, null);
    }

    private Collision getBasicProjectileCollision(BasicProjectile basicProjectile, int[] moveVector,
                                                  LevelsProcessor.GameLevel currentLevel) {

        if (basicProjectile.getCurrentLocation()[1] > sizeY ||
                basicProjectile.getCurrentLocation()[1] < 0)
            return new Collision(basicProjectile, GameEvent.BASIC_PROJECTILE_IS_OUT, null);
        if (basicProjectile.getCurrentLocation()[2] == 0 && !basicProjectile.firedByPlayer &&
                checkCollided(currentLevel.player, basicProjectile, PaintingConst.PLAYER_SIDE_LENGTH,
                        PaintingConst.BASIC_PROJECTILE_DIAMETER, PaintingConst.PLAYER_SIDE_LENGTH / 2))
            return new Collision(basicProjectile, GameEvent.BASIC_PROJECTILE_COLLIDED_PLAYER, currentLevel.player);
        for (int i = 0; i < currentLevel.mobs.size(); i++) {
            MovableObject sphereMob = currentLevel.mobs.get(i);
            if (sphereMob.getCurrentLocation()[2] != basicProjectile.getCurrentLocation()[2]) continue;
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

    private Collision getSphereMobCollision(SphereMob sphereMob, int[] moveVector, LevelsProcessor.GameLevel currentLevel) {
        if (sphereMob.getCurrentLocation()[2] == 0 &&
                checkCollided(currentLevel.player, sphereMob, PaintingConst.PLAYER_SIDE_LENGTH,
                        PaintingConst.SPHERE_MOB_DIAMETER, PaintingConst.PLAYER_SIDE_LENGTH / 2))
            return new Collision(sphereMob, GameEvent.SPHERE_MOB_COLLIDED_PLAYER, currentLevel.player);
        if (sphereMob.getCurrentLocation()[0] > sizeX || sphereMob.getCurrentLocation()[0] < 0)
            return new Collision(sphereMob, GameEvent.SPHERE_MOB_IS_OUT_HORIZONTAL, null);
        if (sphereMob.getCurrentLocation()[1] > sizeY || sphereMob.getCurrentLocation()[1] < 0)
            return new Collision(sphereMob, GameEvent.SPHERE_MOB_IS_OUT_VERTICAL, null);

        for (int i = 0; i < currentLevel.projectiles.size(); i++) {
            BasicProjectile basicProjectile = (BasicProjectile)currentLevel.projectiles.get(i);
            if (basicProjectile.getCurrentLocation()[2] != sphereMob.getCurrentLocation()[2]) continue;
            if (basicProjectile.firedByPlayer &&
                    checkCollided(basicProjectile, sphereMob, PaintingConst.BASIC_PROJECTILE_DIAMETER,
                            PaintingConst.SPHERE_MOB_DIAMETER))
                return new Collision(sphereMob, GameEvent.SPHERE_MOB_COLLIDED_BASIC_PROJECTILE, basicProjectile);
        }


        return new Collision(sphereMob, GameEvent.OK, null);
    }

    private Collision getSphereBossCollision(SphereBoss sphereBoss, int[] moveVector, LevelsProcessor.GameLevel currentLevel) {
        if (sphereBoss.getCurrentLocation()[1] < 50)
            return new Collision(sphereBoss, GameEvent.SPHERE_BOSS_IS_GOING, null);
        if (sphereBoss.getCurrentLocation()[2] == 0 &&
                checkCollided(currentLevel.player, sphereBoss, PaintingConst.PLAYER_SIDE_LENGTH,
                PaintingConst.SPHERE_BOSS_DIAMETER, PaintingConst.PLAYER_SIDE_LENGTH / 2))
            return new Collision(sphereBoss, GameEvent.SPHERE_BOSS_COLLIDED_PLAYER, currentLevel.player);
        if (sphereBoss.getCurrentLocation()[0] <= 0 || sphereBoss.getCurrentLocation()[0] + PaintingConst.SPHERE_BOSS_DIAMETER >= sizeY)
            return new Collision(sphereBoss, GameEvent.SPHERE_BOSS_IS_OUT_HORIZONTAL, null);
        for (int i = 0; i < currentLevel.projectiles.size(); i++) {
            BasicProjectile projectile = (BasicProjectile)currentLevel.projectiles.get(i);
            if (projectile.getCurrentLocation()[2] != sphereBoss.getCurrentLocation()[2]) continue;
            if (projectile.firedByPlayer &&
            checkCollided(projectile, sphereBoss, PaintingConst.BASIC_PROJECTILE_DIAMETER,
                    PaintingConst.SPHERE_BOSS_DIAMETER))
                return new Collision(sphereBoss, GameEvent.SPHERE_BOSS_COLLIDED_BASIC_PROJECTILE, projectile);
        }
        return new Collision(sphereBoss, GameEvent.OK, null);
    }

    private boolean checkCollided(MovableObject firstObject, MovableObject secondObject,
                                  int firstConst, int secondConst, int accurary) {
        return firstObject.getCurrentLocation()[0] - accurary + firstConst >= secondObject.getCurrentLocation()[0] &&
                firstObject.getCurrentLocation()[0] - accurary <= secondObject.getCurrentLocation()[0] + secondConst &&
                firstObject.getCurrentLocation()[1] <= secondObject.getCurrentLocation()[1] + secondConst &&
                firstObject.getCurrentLocation()[1] + firstConst >= secondObject.getCurrentLocation()[1];
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
