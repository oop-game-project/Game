package Game.GUI;

import Game.Engine.LevelsProcessor.SinglePlayerLevel;

import java.awt.event.KeyListener;

public interface GUI
{
    public void init(KeyListener engineAsKeyListener);
    public void render(SinglePlayerLevel renderingLevel);
}
