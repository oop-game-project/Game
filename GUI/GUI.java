package Game.GUI;

import Game.Engine.LevelsProcessor.SinglePlayerLevel;

public interface GUI
{
    public void init(SinglePlayerLevel renderingLevel);
    public void render(SinglePlayerLevel renderingLevel);
    public void dispose();
}
