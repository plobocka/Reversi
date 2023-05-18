import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Game {
    private final Board board;
    private Disc currentPlayerDisc = Disc.BLACK;
    private Computer playerB;
    private Computer playerW;
    private int turn = 0;

    public Game(Board board) {
        this.board = board;
        playerB = new Computer(Disc.BLACK, board);
        playerW = new Computer(Disc.WHITE, board);
        AIvsAI();
    }

    public void play() {
        while(!this.isGameOver()) {
            turn++;
            System.out.println(board);
            System.out.println("It's " + currentPlayerDisc + "'s turn.");
            List<List<Integer>> possibleMoves = showPossibleMoves(currentPlayerDisc, board);
            System.out.println("Possible moves: " + possibleMoves);
            System.out.println("Enter your move in the format: row column");
            String[] input = new Scanner(System.in).nextLine().split(" ");
            int row = Integer.parseInt(input[0]);
            int column = Integer.parseInt(input[1]);
            if (possibleMoves.contains(List.of(row, column)) && board.getDiscObject(row, column) == Disc.EMPTY) {
                makeMove(row, column);
                System.out.println("Score:  B=" + getScoreBW().get(0) + " W=" + getScoreBW().get(1));
            } else {
                System.out.println("Invalid move. Try again.");
            }
        }
        System.out.println("Game over!");
        System.out.println("Final score: B=" + getScoreBW().get(0) + " W=" + getScoreBW().get(1));
    }

    public void OneAIplay() {
        while(!this.isGameOver()) {
            turn++;
            System.out.println(board);
            System.out.println("It's " + currentPlayerDisc + "'s turn.");
            if (currentPlayerDisc == Disc.BLACK) {
                List<List<Integer>> possibleMoves = showPossibleMoves(currentPlayerDisc, board);
                System.out.println("Possible moves: " + possibleMoves);
                System.out.println("Enter your move in the format: row column");
                String[] input = new Scanner(System.in).nextLine().split(" ");
                int row = Integer.parseInt(input[0]);
                int column = Integer.parseInt(input[1]);
                if (possibleMoves.contains(List.of(row, column)) && board.getDiscObject(row, column) == Disc.EMPTY) {
                    makeMove(row, column);
                    System.out.println("Score:  B=" + getScoreBW().get(0) + " W=" + getScoreBW().get(1));
                } else {
                    System.out.println("Invalid move. Try again.");
                }
            } else {
                List<Integer> moveToGet = playerW.makeMove(board);
                if (moveToGet.size() != 1) {
                    List<Integer> move = List.of(moveToGet.get(1), moveToGet.get(2));
                    makeMove(move.get(0), move.get(1));
                    System.out.println("Score:  B=" + getScoreBW().get(0) + " W=" + getScoreBW().get(1));
                } else {
                    System.out.println("No possible moves. Pass.");
                    currentPlayerDisc = currentPlayerDisc.switchColor();
                }
            }
        }
        System.out.println("Game over!");
        System.out.println("Final score: B=" + getScoreBW().get(0) + " W=" + getScoreBW().get(1));
    }

    public void AIvsAI() {
        long totalBTime = 0;
        while(!this.isGameOver()) {
            turn++;
            System.out.println(board);
            System.out.println("It's " + currentPlayerDisc + "'s turn.");
            if (currentPlayerDisc == Disc.BLACK) {
                System.err.println("Player B's turn");
                long startTime = System.nanoTime();
                List<Integer> moveToGet = playerB.makeMove(board);
                long endTime = System.nanoTime();
                long duration = (endTime - startTime) / 1000000; // Czas w milisekundach
                totalBTime += duration;
                System.err.println("Player B's move time: " + duration + "ms");
                if (moveToGet.size() != 1) {
                    List<Integer> move = List.of(moveToGet.get(1), moveToGet.get(2));
                    makeMove(move.get(0), move.get(1));
                    System.out.println("Score:  B=" + getScoreBW().get(0) + " W=" + getScoreBW().get(1));
                } else {
                    System.out.println("No possible moves. Pass.");
                    currentPlayerDisc = currentPlayerDisc.switchColor();
                }
            } else {
                System.err.println("Player W's turn");
                long startTime = System.nanoTime();
                List<Integer> moveToGet = playerW.makeMove(board);
                long endTime = System.nanoTime();
                long duration = (endTime - startTime) / 1000000; // Czas w milisekundach
                System.err.println("Player W's move time: " + duration + "ms");
                if (moveToGet.size() != 1) {
                    List<Integer> move = List.of(moveToGet.get(1), moveToGet.get(2));
                    makeMove(move.get(0), move.get(1));
                    System.out.println("Score:  B=" + getScoreBW().get(0) + " W=" + getScoreBW().get(1));
                } else {
                    System.out.println("No possible moves. Pass.");
                    currentPlayerDisc = currentPlayerDisc.switchColor();
                }
            }
        }
        System.out.println("BOARD");
        System.out.println(board);
        System.out.println("Final score: B=" + getScoreBW().get(0) + " W=" + getScoreBW().get(1));
        System.out.println("Turns: " + turn);
        System.out.println("Player B's average move time: " + totalBTime / turn + "ms");
    }

    public List<Integer> getScoreBW() {
        int blackScore = 0;
        int whiteScore = 0;
        for (int row = 0; row < Board.BOARD_SIZE; row++) {
            for (int column = 0; column < Board.BOARD_SIZE; column++) {
                if (board.getDiscObject(row, column) == Disc.BLACK) {
                    blackScore++;
                } else if (board.getDiscObject(row, column) == Disc.WHITE) {
                    whiteScore++;
                }
            }
        }
        return List.of(blackScore, whiteScore);
    }

    public static List<List<Integer>> showPossibleMoves(Disc currentDisc, Board board) {
        List<List<Integer>> potentialMoves = board.getPotentialMoves(currentDisc.opponent());
        List<List<Integer>> possibleMoves = new ArrayList<>();
        for (List<Integer> move : potentialMoves) {
            for (List<Integer> direction : Board.DIRECTIONS) {
                List<List<Integer>> line = board.getLine(move.get(0), move.get(1), direction.get(0), direction.get(1), currentDisc);
                if (!line.isEmpty() && !possibleMoves.contains(move)) {
                    possibleMoves.add(move);
                }
            }
        }
        return possibleMoves;
    }

    public void makeMove(int row, int column) {
        board.setDisc(currentPlayerDisc, row, column);
        for (List<Integer> direction : Board.DIRECTIONS) {
            List<List<Integer>> line = board.getLine(row, column, direction.get(0), direction.get(1), currentPlayerDisc);
            if (!line.isEmpty()) {
                for (List<Integer> disc : line) {
                    board.setDisc(currentPlayerDisc, disc.get(0), disc.get(1));
                }
            }
        }
        currentPlayerDisc = currentPlayerDisc.switchColor();
    }

    public boolean isGameOver() {
        List<List<Integer>> possibleMoves = showPossibleMoves(currentPlayerDisc, board);
        if (possibleMoves.isEmpty()) {
            currentPlayerDisc = currentPlayerDisc.switchColor();
            possibleMoves = showPossibleMoves(currentPlayerDisc, board);
            if (possibleMoves.isEmpty()) {
                System.out.println("Game over. No possible moves.");
                return true;
            }
            currentPlayerDisc = currentPlayerDisc.switchColor();
        } else if (board.isFull()) {
            System.out.println("Game over. Board full.");
            return true;
        }
        return false;
    }
}
