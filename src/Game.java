import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Екатерина on 09.05.2018.
 */
public class Game {
    private UBoard board;
    public Game() {
        board = new UBoard();
    }


    private static final int SIZE = 3;
    private static class Cell {
        static Cell[][] cells;
        int col, row;
        Cell(int col, int row) {
            this.col = col;
            this.row = row;
        }

        public static Cell getPoint(int row, int col) {
            return cells[row][col];
        }

        @Override
        public String toString() {
            return row + " " + col;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Cell cell = (Cell) o;

            if (col != cell.col) return false;
            return row == cell.row;
        }

        @Override
        public int hashCode() {
            int result = col;
            result = 31 * result + row;
            return result;
        }
        static {
            Cell.cells = new Cell[SIZE][SIZE];
            for (int row = 0; row < SIZE; row ++) {
                for (int col = 0; col < SIZE; col++) {
                    Cell.cells[row][col] = new Cell(col, row);
                }
            }
        }
    }

    private static class UCell {
        int uCol, uRow, col, row;
        private UCell(int uRow, int uCol, int row, int col) {
            this.uRow = uRow;
            this.uCol = uCol;
            this.row = row;
            this.col = col;
        }
        static UCell[][][][] uCells;
        static {
            uCells = new UCell[SIZE][SIZE][SIZE][SIZE];
            for (int uRow = 0; uRow < SIZE; uRow ++) {
                for (int uCol = 0; uCol < SIZE; uCol ++) {
                    for (int row = 0; row < SIZE; row ++) {
                        for (int col = 0; col < SIZE; col ++) {
                            uCells[uRow][uCol][row][col] = new UCell(uRow, uCol, row, col);
                        }
                    }
                }
            }
        }

        static UCell getUCell(int uRow, int uCol, int row, int col) {
            return uCells[uRow][uCol][row][col];
        }

        @Override
        public String toString() {
            return uRow + " "  + uCol + " " + row + " " + col + " ("
                    + (row + uRow * SIZE) + " " + (col + uCol * SIZE) + ")";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UCell cell = (UCell) o;
            return uCol == cell.uCol &&
                    uRow == cell.uRow &&
                    col == cell.col &&
                    row == cell.row;
        }

        @Override
        public int hashCode() {
            return Objects.hash(uCol, uRow, col, row);
        }
    }


    private static class Board {
        short[][] grid;
        int steps = 0;
        List<UCell> validActions;
        short currentPlayer;
        Board() {
            grid = new short[SIZE][SIZE];
            validActions = new ArrayList<>();
        }


        public List<UCell> getValidActions(int uRow, int uCol) {
            validActions.clear();
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    if (grid[row][col] == 0) {
                        validActions.add(UCell.getUCell(uRow, uCol, row, col));
                    }
                }
            }
            return validActions;
        }

        public void setAction(Cell cell, short playerId) {
            grid[cell.row][cell.col] = playerId;
            steps ++;
            currentPlayer = playerId;
        }

        GameState getState() {
            if (steps < 3) {
                return GameState.UNFINISHED;
            }
            for (int i = 0; i < 3; i++) {
                // check rows
                if (grid[i][0] > 0 && grid[i][0] == grid[i][1] && grid[i][0] == grid[i][2]) {
                    if (currentPlayer == grid[i][0]) {
                        return GameState.WIN;
                    } else {
                        return GameState.LOSE;
                    }
                }
                // check cols
                if (grid[0][i] > 0 && grid[0][i] == grid[1][i] && grid[0][i] == grid[2][i]) {
                    if (currentPlayer == grid[0][i]) {
                        return GameState.WIN;
                    } else {
                        return GameState.LOSE;
                    }
                }
            }
            // check diags
            if (grid[0][0] > 0 && grid[0][0] == grid[1][1] && grid[0][0] == grid[2][2]) {
                if (currentPlayer == grid[0][0]) {
                    return GameState.WIN;
                } else {
                    return GameState.LOSE;
                }
            }
            if (grid[2][0] > 0 && grid[2][0] == grid[1][1] && grid[2][0] == grid[0][2]) {
                if (currentPlayer == grid[0][2]) {
                    return GameState.WIN;
                } else {
                    return GameState.LOSE;
                }
            }

            if (steps == SIZE * SIZE) {
                return GameState.DRAW;
            } else {
                return GameState.UNFINISHED;
            }
        }

    }

    private static class UBoard {
        Board master;
        Board[][] boards;
        UCell lastMove = null;
        List<UCell> validActions;
        private short player;

        UBoard() {
            boards = new Board[SIZE][SIZE];
            master = new Board();
            player = 1;
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    boards[i][j] = new Board();
                }
            }
            validActions = new ArrayList<>();
        }

        public List<UCell> getValidActions() {
            validActions.clear();
            if (lastMove != null && lastMove.uRow != -1) {
                if (master.grid[lastMove.row][lastMove.col] == 0) {
                    Board board = boards[lastMove.row][lastMove.col];
                    validActions.addAll(board.getValidActions(lastMove.row, lastMove.col));
                }
            }

            if (validActions.isEmpty()) {
                for (int uRow = 0; uRow < SIZE; uRow ++) {
                    for (int uCol = 0; uCol < SIZE; uCol ++) {
                        if (master.grid[uRow][uCol] == 0) {
                            validActions.addAll(boards[uRow][uCol].getValidActions(uRow, uCol));
                        }
                    }
                }
            }
            return validActions;
        }

        GameState getState() {
            GameState state = master.getState();
            if (!GameState.DRAW.equals(state)) {
                return state;
            }

            int currentWins = 0;
            int opponentWins = 0;

            for (int row = 0; row < SIZE; row ++) {
                for (int col = 0; col < SIZE; col ++) {
                    if (master.grid[row][col] == player) {
                        currentWins ++;
                    } else if (master.grid[row][col] == 3 - player) {
                        opponentWins ++;
                    }
                }
            }
            if (currentWins > opponentWins) {
                return GameState.WIN;
            } else if (opponentWins > currentWins) {
                return GameState.LOSE;
            } else {
                return GameState.DRAW;
            }
        }

        public void setAction(UCell cell) {
            Board board = boards[cell.uRow][cell.uCol];
            board.setAction(Cell.getPoint(cell.row, cell.col), player);
            GameState state = board.getState();
            if (!GameState.UNFINISHED.equals(state)) {
                master.setAction(Cell.getPoint(cell.uRow, cell.uCol), player);
            }
            lastMove = cell;
        }
    }

    public String output() {
        List<UCell> actions = board.getValidActions();
        String output = "";
        if (board.lastMove == null) {
            output += "-1 -1\n";
        } else {
            output += (board.lastMove.row + board.lastMove.uRow * SIZE) + " " + (board.lastMove.col + board.lastMove.uCol * SIZE) + "\n";
        }
        output += actions.size();
        for (UCell cell : actions) {
            output += "\n" + (cell.row + cell.uRow * SIZE) + " " + (cell.col + cell.uCol * SIZE);
        }
        return output;
    }

    public GameState readInput(String input) {
        Cell cell = null;
        try {
            String[] c = input.split(" ");
            cell = new Cell(Integer.valueOf(c[1]), Integer.valueOf(c[0]));
        } catch (Exception e) {
            throw new InvalidInputException();
        }

        List<UCell> actions = board.getValidActions();
        UCell action = null;
        for (UCell move : actions) {
            if (move.uRow == cell.row / SIZE && move.uCol == cell.col / SIZE && move.row == cell.row % SIZE && move.col == cell.col % SIZE) {
                action = move;
            }
        }
        if (action == null) {
            throw new InvalidInputException();
        }

        board.setAction(action);
        GameState state = board.getState();
        board.player = (short) (3 - board.player);
        return state;
    }
}
