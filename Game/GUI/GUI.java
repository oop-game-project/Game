package Game.GUI;

import Game.Engine.LevelsProcessor.SinglePlayerLevel;

import java.awt.event.KeyListener;

public interface GUI
{
    void init(KeyListener engineAsKeyListener);
    void render();
}
