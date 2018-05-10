/**
 * Created by Екатерина on 09.05.2018.
 */
enum GameState {
    UNFINISHED(0), DRAW(-1), WIN(1), LOSE(2);
    private int id;

    GameState(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    static GameState getValueById(int id) {
        for (GameState gameState : GameState.values()) {
            if (gameState.id == id) {
                return gameState;
            }
        }
        return null;
    }
}
