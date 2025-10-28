package nye.flocrm.progtech.model;

/**
 * A játék lehetséges állapotai.
 */
public enum GameState {
    IN_PROGRESS, //folyamatban
    PLAYER_X_WON, //X játékos győzőtt
    PLAYER_O_WON, //O játékos győzőtt
    DRAW //Döntetlen
}