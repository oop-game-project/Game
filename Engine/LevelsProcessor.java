package Game.Engine;


import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class LevelsProcessor
{
    class SinglePlayerLevel
    {
        private GameObjects.GameField gameField;
        private GameObjects.Player playerOne;
        private GameObjects.GameObject[] gameObjects;

        SinglePlayerLevel(
                @NotNull GameObjects.GameField inputGameField,
                @NotNull GameObjects.Player inputPlayerOne,
                @Nullable GameObjects.GameObject[] inputGameObjects)
        {
            this.gameField = inputGameField;
            this.playerOne = inputPlayerOne;
            this.gameObjects = inputGameObjects;
        }

        GameObjects.GameField getGameField()
        {
            return this.gameField;
        }

        GameObjects.Player getPlayerOne()
        {
            return this.playerOne;
        }

        GameObjects.GameObject[] getGameObjects()
        {
            return this.gameObjects;
        }
    }
}