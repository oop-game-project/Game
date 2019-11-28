package GUI;

import Engine.LevelsProcessor.GameLevel;

import java.awt.event.KeyListener;

public interface GUI
{
    void init(
        KeyListener engineAsKeyListener,
        GameLevel inputLevel);
    void render();
}
