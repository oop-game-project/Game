package Game;


import Game.Engine.Engine;

public class DebugLauncher
{
    public static void main(String[] args)
    {
        Game.Engine.Engine engine = new Engine(null);
        engine.runGameLoop();
    }
}
