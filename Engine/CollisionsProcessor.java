package Game.Engine;


enum Event
{
    OUT_TERRITORY,
    KILLED,
    DISAPPEARED,
    OK
}


public class CollisionsProcessor
{
    public CollisionsProcessor(LevelsProcessor.SinglePlayerLevel currentLevel)
    {
        int[] size = currentLevel.gameFieldSize;
        sizeX = size[0];
        sizeY = size[1];
        sizeZ = size[2];
        border = 5;
    }
    private int sizeX;
    private int sizeY;
    private int sizeZ;
    private int border;

    public class Collision<T>
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

    public Collision getCollision(int[] vector, GameObjects.MovableObject object)
    {
        int[] location = object.currentLocation;
        int objectX = location[0];
        int objectY = location[1];
        int objectZ = location[2];
        if (isOutTerritory(vector[0], objectX, sizeX) ||
                isOutTerritory(vector[1], objectY, sizeY))
//                isOutTerritory(vector.getZ(), objectZ, sizeZ))
            return new Collision<>(object, Event.OUT_TERRITORY);
        return new Collision<>(object, Event.OK);
    }

    private boolean isOutTerritory(int vectorCoordinate, int playerCoordinate, int size)
    {
        return ((vectorCoordinate >= 0) && (playerCoordinate + border >= size)) ||
                ((vectorCoordinate <= 0) && (playerCoordinate - border <= 0));
    }


}