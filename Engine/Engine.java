package Game.Engine;

import Game.GUI.GUI_2D;
import Game.GUI.GUI;
import Game.Engine.LevelsProcessor.SinglePlayerLevel;

import org.jetbrains.annotations.NotNull;

public class Engine {
    private SinglePlayerLevel currentLevel;

    // TODO : constructor with "String levelFileName"
    public Engine(SinglePlayerLevel inputLevel) {
        this.currentLevel = inputLevel;
    }

    class GeometryVector {
        private int X;
        private int Y;
        private int Z;

        GeometryVector(int inputX, int inputY, int inputZ) {
            this.X = inputX;
            this.Y = inputY;
            this.Z = inputZ;
        }

        int[] getCoordinates() {
            return new int[]{this.X, this.Y, this.Z};
        }

        int getX() {
            return this.X;
        }

        int getY() {
            return this.Y;
        }

        int getZ() {
            return this.Z;
        }
    }

    public void runGameLoop() {
        GUI gui = new GUI_2D();  // Change main GUI here: GUI_2D or GUI_3D

        gui.init(currentLevel);

        gui.render(currentLevel);

        gui.dispose();
    }
}