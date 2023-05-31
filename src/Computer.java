import java.util.List;

public class Computer {
    private Disc player;
    private Board board;
    private int round = 1;
    private static final int MAX_DEPTH = 7;
    private int nodesVisited = 0;
    /*
     * heuristic functions:
     * 1. mobility
     * 2. corners
     * 3. stability
     */
    private static final int HEURISTIC_CHOICE = 1;

    public Computer(Disc player, Board board) {
        this.player = player;
        this.board = copyBoard(board);
    }

    public List<Integer> makeMove(Board boardState) {
        this.board = copyBoard(boardState);
        List<Integer> move;
//        if (this.player == Disc.BLACK) {
//            HEURISTIC_CHOICE = 3;
//            move = alphaBeta(this.board, this.getPlayer(), MAX_DEPTH, HEURISTIC_CHOICE, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
//        } else {
//            HEURISTIC_CHOICE = 2;
//            move = alphaBeta(this.board, this.getPlayer(), MAX_DEPTH, HEURISTIC_CHOICE, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
//        }
        move = alphaBeta(this.board, this.getPlayer(), MAX_DEPTH, HEURISTIC_CHOICE, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
//        move = alphaBetaAdaptive(this.board, this.getPlayer(), MAX_DEPTH, HEURISTIC_CHOICE, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
//        move = minMax(this.board, this.getPlayer(), MAX_DEPTH, HEURISTIC_CHOICE, true);
        round++;
        return move;
    }

    private Integer calculateHeuristic(Board board, Disc player, int option) {
        return switch (option) {
            case 1 -> mobilityStrategy(board, player);
            case 2 -> cornerStrategy(board, player);
            case 3 -> stabilityStrategy(board, player);
            default -> mobilityStrategy(board, player);
        };
    }

    private Integer mobilityStrategy(Board board, Disc player) {
        var maxPlayerMoves = Game.showPossibleMoves(player, board).size();
        var minPlayerMoves = Game.showPossibleMoves(player.opponent(), board).size();
        if (maxPlayerMoves + minPlayerMoves != 0) {
            return 100 * (maxPlayerMoves - minPlayerMoves) / (maxPlayerMoves + minPlayerMoves);
        } else {
            return 0;
        }
    }

    private Integer cornerStrategy(Board board, Disc player) {
        var maxPlayerCorners = 0;
        var minPlayerCorners = 0;
        for (List<Integer> corner : Board.CORNERS) {
            if (board.getBoard()[corner.get(0)][corner.get(1)] == player) {
                maxPlayerCorners++;
            } else if (board.getBoard()[corner.get(0)][corner.get(1)] == player.opponent()) {
                minPlayerCorners++;
            }
        }
        return 25 * (maxPlayerCorners - minPlayerCorners);
    }

    private Integer stabilityStrategy(Board board, Disc player) {
        var maxPlayerStability = 0;
        var minPlayerStability = 0;
        for (int row = 0; row < Board.BOARD_SIZE; row++) {
            for (int column = 0; column < Board.BOARD_SIZE; column++) {
                if (board.getBoard()[row][column] == player) {
                    maxPlayerStability += Board.STABILITY_SCORES[row][column];
                } else if (board.getBoard()[row][column] == player.opponent()) {
                    minPlayerStability += Board.STABILITY_SCORES[row][column];
                }
            }
        }
        return 100 * (maxPlayerStability - minPlayerStability);
    }

    public List<Integer> minMax(Board board, Disc player, int depth, int heuristic, boolean isMaximizing) {
        if (isGameOver(this.getPlayer())) {
            return List.of(calculateHeuristic(board, player, heuristic));
        }
        if (depth == 0) {
            return List.of(calculateHeuristic(board, player, heuristic));
        }
        List<List<Integer>> possibleMoves = Game.showPossibleMoves(player, board);
        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            List<Integer> bestMove = null;
            for (List<Integer> move : possibleMoves) {
                Board newBoard = copyBoard(board);
                makeMove(move.get(0), move.get(1), newBoard, player);
                int eval = minMax(newBoard, player.opponent(), depth - 1, heuristic, false).get(0);
                if (eval >= maxEval) {
                    maxEval = eval;
                    bestMove = List.of(move.get(0), move.get(1));
                }
                nodesVisited++;
            }
            return bestMove != null ? List.of(maxEval, bestMove.get(0), bestMove.get(1)) : List.of(maxEval);
        } else {
            int minEval = Integer.MAX_VALUE;
            List<Integer> bestMove = null;
            for (List<Integer> move : possibleMoves) {
                Board newBoard = copyBoard(board);
                makeMove(move.get(0), move.get(1), newBoard, player);
                int eval = minMax(newBoard, player.opponent(), depth - 1, heuristic, true).get(0);
                if (eval <= minEval) {
                    minEval = eval;
                    bestMove = List.of(move.get(0), move.get(1));
                }
                nodesVisited++;
            }
            return bestMove != null ? List.of(minEval, bestMove.get(0), bestMove.get(1)) : List.of(minEval);
        }
    }

    public List<Integer> alphaBeta(Board board, Disc player, int depth, int heuristic, boolean isMaximizing, int alphaValue, int betaValue) {
        if (isGameOver(this.getPlayer()) || depth == 0) {
            return List.of(calculateHeuristic(board, player, heuristic));
        }
        int alpha = alphaValue;
        int beta = betaValue;
        List<List<Integer>> possibleMoves = Game.showPossibleMoves(player, board);
        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            List<Integer> bestMove = null;
            for (List<Integer> move : possibleMoves) {
                Board newBoard = copyBoard(board);
                makeMove(move.get(0), move.get(1), newBoard, player);
                int eval = alphaBeta(newBoard, player.opponent(), depth - 1, heuristic, false, alpha, beta).get(0);
                if (eval >= maxEval) {
                    maxEval = eval;
                    bestMove = List.of(move.get(0), move.get(1));
                }
                if (maxEval >= alpha) {
                    alpha = maxEval;
                }
                if (beta <= alpha) {
                    break;
                }
                nodesVisited++;
            }
            return bestMove != null ? List.of(maxEval, bestMove.get(0), bestMove.get(1)) : List.of(maxEval);
        } else {
            int minEval = Integer.MAX_VALUE;
            List<Integer> bestMove = null;
            for (List<Integer> move : possibleMoves) {
                Board newBoard = copyBoard(board);
                makeMove(move.get(0), move.get(1), newBoard, player);
                int eval = alphaBeta(newBoard, player.opponent(), depth - 1, heuristic, true, alpha, beta).get(0);
                if (eval <= minEval) {
                    minEval = eval;
                    bestMove = List.of(move.get(0), move.get(1));
                }
                if (minEval <= beta) {
                    beta = minEval;
                }
                if (beta <= alpha) {
                    break;
                }
                nodesVisited++;
            }
            return bestMove != null ? List.of(minEval, bestMove.get(0), bestMove.get(1)) : List.of(minEval);
        }
    }

    private Integer calculateAdaptiveHeuristic(Board board, Disc player) {
        int heuristic;
        if (round < 20) {
            heuristic = calculateHeuristic(board, player, 1);
        } else if (round < 40) {
            heuristic = calculateHeuristic(board, player, 2);
        } else {
            heuristic = calculateHeuristic(board, player, 3);
        }
        return heuristic;
    }


    public List<Integer> alphaBetaAdaptive(Board board, Disc player, int depth, int heuristic, boolean isMaximizing, int alphaValue, int betaValue) {
        if (isGameOver(this.getPlayer()) || depth == 0) {
            return List.of(calculateAdaptiveHeuristic(board, player));
        }
        int alpha = alphaValue;
        int beta = betaValue;
        List<List<Integer>> possibleMoves = Game.showPossibleMoves(player, board);
        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            List<Integer> bestMove = null;
            for (List<Integer> move : possibleMoves) {
                Board newBoard = copyBoard(board);
                makeMove(move.get(0), move.get(1), newBoard, player);
                int eval = alphaBetaAdaptive(newBoard, player.opponent(), depth - 1, heuristic, false, alpha, beta).get(0);
                if (eval >= maxEval) {
                    maxEval = eval;
                    bestMove = List.of(move.get(0), move.get(1));
                }
                if (maxEval >= alpha) {
                    alpha = maxEval;
                }
                if (beta <= alpha) {
                    break;
                }
                nodesVisited++;
            }
            return bestMove != null ? List.of(maxEval, bestMove.get(0), bestMove.get(1)) : List.of(maxEval);
        } else {
            int minEval = Integer.MAX_VALUE;
            List<Integer> bestMove = null;
            for (List<Integer> move : possibleMoves) {
                Board newBoard = copyBoard(board);
                makeMove(move.get(0), move.get(1), newBoard, player);
                int eval = alphaBetaAdaptive(newBoard, player.opponent(), depth - 1, heuristic, true, alpha, beta).get(0);
                if (eval <= minEval) {
                    minEval = eval;
                    bestMove = List.of(move.get(0), move.get(1));
                }
                if (minEval <= beta) {
                    beta = minEval;
                }
                if (beta <= alpha) {
                    break;
                }
                nodesVisited++;
            }
            return bestMove != null ? List.of(minEval, bestMove.get(0), bestMove.get(1)) : List.of(minEval);
        }
    }


    public void makeMove(int row, int column, Board board, Disc discColor) {
        board.setDisc(discColor, row, column);
        for (List<Integer> direction : Board.DIRECTIONS) {
            List<List<Integer>> line = board.getLine(row, column, direction.get(0), direction.get(1), discColor);
            if (!line.isEmpty()) {
                for (List<Integer> disc : line) {
                    board.setDisc(discColor, disc.get(0), disc.get(1));
                }
            }
        }
    }

    public boolean isGameOver(Disc currentPlayerDisc) {
        List<List<Integer>> possibleMoves = Game.showPossibleMoves(currentPlayerDisc, board);
        if (possibleMoves.isEmpty()) {
            currentPlayerDisc = currentPlayerDisc.switchColor();
            possibleMoves = Game.showPossibleMoves(currentPlayerDisc, board);
            return possibleMoves.isEmpty();
        } else return board.isFull();
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

    public Disc getPlayer() {
        return player;
    }

    public void setPlayer(Disc player) {
        this.player = player;
    }

    public int getRound() {
        return round;
    }

    public int getNodesVisited() {
        return nodesVisited;
    }
}
