package Game.GUI;

import Game.Engine.LevelsProcessor.SinglePlayerLevel;

import java.util.EventListener;

public interface GUI
{
    public void init(EventListener engineAsKeyListener);
    public void render(SinglePlayerLevel renderingLevel);
}
