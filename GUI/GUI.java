package Game.GUI;

import Game.Engine.Engine;
import Game.Engine.LevelsProcessor.SinglePlayerLevel;

public interface GUI
{
    public void init();
    public void render(SinglePlayerLevel renderingLevel);
    public void dispose();
}
