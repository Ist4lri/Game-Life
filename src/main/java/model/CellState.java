package model;

import javafx.scene.paint.Color;

/**
 * {@link CellState} instances represent the possible states of a
 * {@link CellState}.
 */
public enum CellState {
    RED_ALIVE(true, Color.RED),
    BLUE_ALIVE(true, Color.BLUE),
    DEAD(false, Color.WHITE);

    public final boolean isAlive;
    public Color color;

    CellState(boolean isAlive, Color color) {
        this.isAlive = isAlive;
        this.color = color;
    }

}
