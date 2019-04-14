package Game.Engine;


public class CollisionsProcessor
{
    public CollisionsProcessor(GameObjects.GameFieldObject gameField)
    {
        int[] size = gameField.getGameFieldSize();
        sizeX = size[0];
        sizeY = size[1];
        sizeZ = size[2];
        border = 5;
    }
    private int sizeX;
    private int sizeY;
    private int sizeZ;
    private int border;

    private enum Event
    {
        OUT_TERRITORY,
        KILLED,
        DISAPPEARED,
        OK
    }
    private class Collision<T>
    {
        private T gameObject;
        private Event event;
        Collision(T gameObject, Event event)
        {
            this.gameObject = gameObject;
            this.event = event;
        }
        public T getGameObject() {return gameObject;}
        public Event getEvent() {return event;}
    }

    public Collision getCollision(Engine.GeometryVector vector, GameObjects.Player player)
    {
        int[] location = player.getCurrentLocation();
        int playerX = location[0];
        int playerY = location[1];
        int playerZ = location[2];
        if (isOutTerritory(vector.getX(), playerX, sizeX) ||
                isOutTerritory(vector.getY(), playerY, sizeY) ||
                isOutTerritory(vector.getZ(), playerZ, sizeZ))
            return new Collision<>(player, Event.OUT_TERRITORY);
        return new Collision<>(player, Event.OK);
    }

    private boolean isOutTerritory(int vectorCoordinate, int playerCoordinate, int size)
    {
        return ((vectorCoordinate >= 0) && (playerCoordinate + border >= size)) ||
                ((vectorCoordinate <= 0) && (playerCoordinate - border <= 0));
    }
}