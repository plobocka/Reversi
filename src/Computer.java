import java.util.ArrayList;
import java.util.List;

public class Computer {
    private Disc player;
    private Board board;

    public Computer(Disc player, Board board) {
        this.player = player;
        this.board = copyBoard(board);
    }

    public Board copyBoard(Board board) {
        Board copy = new Board();
        for (int row = 0; row < Board.BOARD_SIZE; row++) {
            for (int column = 0; column < Board.BOARD_SIZE; column++) {
                copy.getBoard()[row][column] = board.getBoard()[row][column];
            }
        }
        return copy;
    }

    public List<Integer> makeMove(Board boardState) {
        this.board = copyBoard(boardState);
        List<Integer> move;
        move = MinMax(this.board, this.getPlayer(), 3, 0, true);
        return move;
    }

    private Integer MobilityStrategy(Board board, Disc player) {
        var maxPlayerMoves = Game.showPossibleMoves(player, board).size();
        var minPlayerMoves = Game.showPossibleMoves(player.opponent(), board).size();
        if (maxPlayerMoves + minPlayerMoves != 0) {
            return 100 * (maxPlayerMoves - minPlayerMoves) / (maxPlayerMoves + minPlayerMoves);
        } else {
            return 0;
        }
    }

    public List<Integer> MinMax(Board board, Disc player, int depth, double heuristic, boolean isMaximizing) {
//        if (isGameOver(this.getPlayer())) {
//            return List.of(0, bestMove.get(0), bestMove.get(1));
//        }
        if (depth == 0) {
            return List.of(MobilityStrategy(board, player));
        }
        List<List<Integer>> possibleMoves = Game.showPossibleMoves(player, board);
        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            List<Integer> bestMove = null;
            for (List<Integer> move : possibleMoves) {
                Board newBoard = copyBoard(board);
                newBoard.setDisc(player, move.get(0), move.get(1));
                for (List<Integer> direction : Board.DIRECTIONS) {
                    List<List<Integer>> line = newBoard.getLine(move.get(0), move.get(1), direction.get(0), direction.get(1), player);
                    if (!line.isEmpty()) {
                        for (List<Integer> disc : line) {
                            newBoard.setDisc(player, disc.get(0), disc.get(1));
                        }
                    }
                }
                int eval = MinMax(newBoard, player.opponent(), depth - 1, heuristic, false).get(0);
                if (eval > maxEval) {
                    maxEval = eval;
                    bestMove = List.of(move.get(0), move.get(1));
                }
            }

            return List.of(maxEval, bestMove.get(0), bestMove.get(1));
        } else {
            int minEval = Integer.MAX_VALUE;
            List<Integer> bestMove = null;
            for (List<Integer> move : possibleMoves) {
                Board newBoard = copyBoard(board);
                newBoard.setDisc(player, move.get(0), move.get(1));
                for (List<Integer> direction : Board.DIRECTIONS) {
                    List<List<Integer>> line = newBoard.getLine(move.get(0), move.get(1), direction.get(0), direction.get(1), player);
                    if (!line.isEmpty()) {
                        for (List<Integer> disc : line) {
                            newBoard.setDisc(player, disc.get(0), disc.get(1));
                        }
                    }
                }
                int eval = MinMax(newBoard, player.opponent(), depth - 1, heuristic, true).get(0);
                if (eval <= minEval) {
                    minEval = eval;
                    bestMove = List.of(move.get(0), move.get(1));
                }
            }
            return List.of(minEval, bestMove.get(0), bestMove.get(1));
        }
    }

    public boolean isGameOver(Disc currentPlayerDisc) {
        List<List<Integer>> possibleMoves = Game.showPossibleMoves(currentPlayerDisc, board);
        if (possibleMoves.isEmpty()) {
            currentPlayerDisc = currentPlayerDisc.switchColor();
            possibleMoves = Game.showPossibleMoves(currentPlayerDisc, board);
            if (possibleMoves.isEmpty()) {
                return true;
            }
        } else if (board.isFull()) {
            return true;
        }
        return false;
    }

    public Disc getPlayer() {
        return player;
    }

    public void setPlayer(Disc player) {
        this.player = player;
    }
}
