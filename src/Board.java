import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board {

    public static final int BOARD_SIZE = 8;
    public static final List<List<Integer>> DIRECTIONS = List.of(
            List.of(1, 1),
            List.of(1, -1),
            List.of(-1, 1),
            List.of(-1, -1),
            List.of(1, 0),
            List.of(-1, 0),
            List.of(0, 1),
            List.of(0, -1)
    );

    public static final List<List<Integer>> CORNERS = List.of(
            List.of(0, 0),
            List.of(0, BOARD_SIZE - 1),
            List.of(BOARD_SIZE - 1, 0),
            List.of(BOARD_SIZE - 1, BOARD_SIZE - 1)
    );

    private final Disc[][] board;

    public Disc[][] getBoard() {
        return board;
    }

    public Board() {
        board = startBoard();
    }

    public static Disc[][] startBoard() {
        Disc[][] board = new Disc[BOARD_SIZE][BOARD_SIZE];
        Arrays.stream(board).forEach(row -> Arrays.fill(row, Disc.EMPTY));
        board[3][3] = Disc.BLACK;
        board[4][4] = Disc.BLACK;
        board[3][4] = Disc.WHITE;
        board[4][3] = Disc.WHITE;
        return board;
    }

    public List<List<Integer>> getPotentialMoves(Disc opponentDisc) {
        List<List<Integer>> potentialMoves = new ArrayList<>();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int column = 0; column < BOARD_SIZE; column++) {
                if (board[row][column] == opponentDisc) {
                    for (List<Integer> direction : DIRECTIONS) {
                        int newRow = row + direction.get(0);
                        int newColumn = column + direction.get(1);
                        if (newRow >= 0 && newRow < BOARD_SIZE && newColumn >= 0 && newColumn < BOARD_SIZE) {
                            if (board[newRow][newColumn] == Disc.EMPTY) {
                                potentialMoves.add(List.of(newRow, newColumn));
                            }
                        }
                    }
                }
            }
        }
        return potentialMoves;
    }

    public void setDisc(Disc disc, int row, int column) {
        board[row][column] = disc;
    }

    public Integer getDisc(int row, int column) {
        return board[row][column].getNumber();
    }

    public boolean flipDiscs(int row, int column, Disc currentPlayerDisc) {
        boolean flipped = false;
        for (List<Integer> direction : DIRECTIONS) {
            List<List<Integer>> line = getLine(row, column, direction.get(0), direction.get(1), currentPlayerDisc);
            if (!line.isEmpty()) {
                for (List<Integer> disc : line) {
                    board[disc.get(0)][disc.get(1)] = currentPlayerDisc;
                }
                flipped = true;
            }
        }
        return flipped;
    }

    public List<List<Integer>> getLine(int row, int column, int xDir, int yDir, Disc currentColor) {
        List<List<Integer>> line =new ArrayList<>() ;
        int newRow = row + xDir;
        int newColumn = column + yDir;
        while (newRow >= 0 && newRow < BOARD_SIZE && newColumn >= 0 && newColumn < BOARD_SIZE) {
            if (board[newRow][newColumn] == Disc.EMPTY) {
                line.clear();
                return line;
            }
            if (board[newRow][newColumn] == currentColor) {
                return line;
            }
            line.add(List.of(newRow, newColumn));
            newRow += xDir;
            newColumn += yDir;
        }
        return line;
    }

    public boolean isFull() {
        for (Disc[] row : board) {
            for (Disc disc : row) {
                if (disc == Disc.EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    public Disc getDiscObject(int row, int column) {
        return board[row][column];
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("   1 2 3 4 5 6 7 8\n");
        int rowNumber = 1;
        for (Disc[] row : board) {
            sb.append(rowNumber).append(" |");
            for (Disc disc : row) {
                sb.append(disc).append("|");
            }
            sb.append("\n");
            rowNumber++;
        }
        return sb.toString();
    }
}
