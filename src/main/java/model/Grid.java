package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javafx.scene.paint.Color;

/**
 * {@link Grid} instances represent the grid in <i>The Game of Life</i>.
 */
public class Grid implements Iterable<Cell> {

    private final int numberOfRows;
    private final int numberOfColumns;
    private final Cell[][] cells;

    /**
     * Creates a new {@code Grid} instance given the number of rows and columns.
     *
     * @param numberOfRows    the number of rows
     * @param numberOfColumns the number of columns
     * @throws IllegalArgumentException if {@code numberOfRows} or
     *                                  {@code numberOfColumns} are
     *                                  less than or equal to 0
     */
    public Grid(int numberOfRows, int numberOfColumns) {
        this.numberOfRows = numberOfRows;
        this.numberOfColumns = numberOfColumns;
        this.cells = createCells();
    }

    /**
     * Returns an iterator over the cells in this {@code Grid}.
     *
     * @return an iterator over the cells in this {@code Grid}
     */

    @Override
    public Iterator<Cell> iterator() {
        return new GridIterator(this);
    }

    private Cell[][] createCells() {
        Cell[][] cells = new Cell[getNumberOfRows()][getNumberOfColumns()];
        for (int rowIndex = 0; rowIndex < getNumberOfRows(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < getNumberOfColumns(); columnIndex++) {
                cells[rowIndex][columnIndex] = new Cell();
            }
        }
        return cells;
    }

    /**
     * Returns the {@link Cell} at the given index.
     *
     * <p>
     * Note that the index is wrapped around so that a {@link Cell} is always
     * returned.
     *
     * @param rowIndex    the row index of the {@link Cell}
     * @param columnIndex the column index of the {@link Cell}
     * @return the {@link Cell} at the given row and column index
     */
    public Cell getCell(int rowIndex, int columnIndex) {
        return cells[getWrappedRowIndex(rowIndex)][getWrappedColumnIndex(columnIndex)];
    }

    private int getWrappedRowIndex(int rowIndex) {
        return (rowIndex + getNumberOfRows()) % getNumberOfRows();
    }

    private int getWrappedColumnIndex(int columnIndex) {
        return (columnIndex + getNumberOfColumns()) % getNumberOfColumns();
    }

    /**
     * Returns the number of rows in this {@code Grid}.
     *
     * @return the number of rows in this {@code Grid}
     */
    public int getNumberOfRows() {
        return numberOfRows;
    }

    /**
     * Returns the number of columns in this {@code Grid}.
     *
     * @return the number of columns in this {@code Grid}
     */
    public int getNumberOfColumns() {
        return numberOfColumns;
    }

    public List<Cell> getNeighbors(int rowIndex, int columnIndex) {
        List<Cell> Result = new ArrayList<Cell>();
        Result.add(this.getCell(rowIndex - 1, columnIndex - 1));
        Result.add(this.getCell(rowIndex - 1, columnIndex));
        Result.add(this.getCell(rowIndex - 1, columnIndex + 1));
        Result.add(this.getCell(rowIndex, columnIndex + 1));
        Result.add(this.getCell(rowIndex, columnIndex - 1));
        Result.add(this.getCell(rowIndex + 1, columnIndex - 1));
        Result.add(this.getCell(rowIndex + 1, columnIndex));
        Result.add(this.getCell(rowIndex + 1, columnIndex + 1));
        return Result;
    }

    public int countAliveNeighbors(int rowIndex, int columnIndex) {
        List<Cell> listOfNeighbors = this.getNeighbors(rowIndex, columnIndex);
        int numberOfAliveNeighbors = 0;
        for (int i = 0; i < listOfNeighbors.size(); i++) {
            if (listOfNeighbors.get(i).isAlive()) {
                numberOfAliveNeighbors++;
            }
        }
        return numberOfAliveNeighbors;
    }

    public int countBlueNeighbors(int rowIndex, int columnIndex) {
        List<Cell> listOfNeighbors = this.getNeighbors(rowIndex, columnIndex);
        int numberOfBlueNeighbors = 0;
        for (int i = 0; i < listOfNeighbors.size(); i++) {
            if (listOfNeighbors.get(i).getState().getCellColor() == Color.BLUE) {
                numberOfBlueNeighbors++;
            }
        }
        return numberOfBlueNeighbors;
    }

    public CellState calculateNextState(int rowIndex, int columnIndex) {
        int howManyAlive = countAliveNeighbors(rowIndex, columnIndex);
        Cell cellAnalyzed = this.getCell(rowIndex, columnIndex);
        if (cellAnalyzed.isAlive()) {
            if (howManyAlive == 2 || howManyAlive == 3) {
                return cellAnalyzed.getState();
            } else {
                return CellState.DEAD;
            }
        } else {
            if (howManyAlive == 3) {
                int numberOfBlue = countBlueNeighbors(rowIndex, columnIndex);
                if (numberOfBlue >= 2) {
                    return CellState.BLUE_ALIVE;
                } else {
                    return CellState.RED_ALIVE;
                }
            }
            return CellState.DEAD;
        }
    }

    public CellState[][] calculateNextStates() {
        CellState[][] nextCellState = new CellState[getNumberOfRows()][getNumberOfColumns()];
        for (int row = 0; row < getNumberOfRows(); row++) {
            for (int columns = 0; columns < getNumberOfColumns(); columns++) {
                nextCellState[row][columns] = calculateNextState(row, columns);
            }
        }
        return nextCellState;
    }

    public void updateStates(CellState[][] nextState) {
        for (int row = 0; row < nextState.length; row++) {
            for (int columns = 0; columns < nextState[row].length; columns++) {
                getCell(row, columns).setState(nextState[row][columns]);
            }
        }
    }

    /**
     * Transitions all {@link Cell}s in this {@code Grid} to the next generation.
     *
     * <p>
     * The following rules are applied:
     * <ul>
     * <li>Any live {@link Cell} with fewer than two live neighbours dies, i.e.
     * underpopulation.</li>
     * <li>Any live {@link Cell} with two or three live neighbours lives on to the
     * next
     * generation.</li>
     * <li>Any live {@link Cell} with more than three live neighbours dies, i.e.
     * overpopulation.</li>
     * <li>Any dead {@link Cell} with exactly three live neighbours becomes a live
     * cell, i.e.
     * reproduction.</li>
     * </ul>
     */
    public void updateToNextGeneration() {
        updateStates(this.calculateNextStates());

    }

    /**
     * Sets all {@link Cell}s in this {@code Grid} as dead.
     */
    public void clear() {
        for (Cell cell : this) {
            cell.setState(CellState.DEAD);
        }
    }

    /**
     * Goes through each {@link Cell} in this {@code Grid} and randomly sets its
     * state as ALIVE or DEAD.
     *
     * @param random {@link Random} instance used to decide if each {@link Cell} is
     *               ALIVE or DEAD.
     * @throws NullPointerException if {@code random} is {@code null}.
     */
    public void randomGeneration(Random random) {
        for (Cell cell : this) {
            cell.setState(random.nextBoolean() ? (random.nextBoolean() ? CellState.RED_ALIVE : CellState.BLUE_ALIVE)
                    : CellState.DEAD);
        }
    }
}
