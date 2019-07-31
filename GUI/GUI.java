package GUI;

import Engine.LevelsProcessor.SinglePlayerLevel;

import java.awt.event.KeyListener;

public interface GUI
{
    void init(
        KeyListener engineAsKeyListener,
        SinglePlayerLevel inputLevel);
    void render();
}
