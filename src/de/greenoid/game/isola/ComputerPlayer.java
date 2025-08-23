package de.greenoid.IsolaGem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class ComputerPlayer {

    private int maxSearchDepth;
    private Random random;
    private final ExecutorService executorService;

    public ComputerPlayer(int maxSearchDepth) {
        this.maxSearchDepth = maxSearchDepth;
        this.random = new Random();
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public IsolaMove findBestMove(IsolaBoard board, int currentPlayer) {
        long startTime = System.nanoTime();
        IsolaMove bestMove = null;
        double bestValue = (currentPlayer == IsolaBoard.PLAYER1) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        List<IsolaMove> bestMoves = new ArrayList<>();

        List<IsolaMove> possibleMoves = generateAllPossibleMoves(board, currentPlayer);

        if (possibleMoves.isEmpty()) {
            return null;
        }

        List<Future<Double>> futures = new ArrayList<>();

        for (IsolaMove move : possibleMoves) {
            Callable<Double> task = () -> {
                IsolaBoard clonedBoard = board.clone();
                clonedBoard.movePlayer(currentPlayer, move.moveToRow, move.moveToCol);
                clonedBoard.removeTile(move.removeTileRow, move.removeTileCol);

                // Pass the move to minimax for evaluation
                return minimax(clonedBoard, maxSearchDepth - 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, getOpponent(currentPlayer), move);
            };
            futures.add(executorService.submit(task));
        }

        try {
            for (int i = 0; i < possibleMoves.size(); i++) {
                IsolaMove move = possibleMoves.get(i);
                double value = futures.get(i).get();

                if (currentPlayer == IsolaBoard.PLAYER1) {
                    if (value > bestValue) {
                        bestValue = value;
                        bestMoves.clear();
                        bestMoves.add(move);
                    } else if (value == bestValue) {
                        bestMoves.add(move);
                    }
                } else {
                    if (value < bestValue) {
                        bestValue = value;
                        bestMoves.clear();
                        bestMoves.add(move);
                    } else if (value == bestValue) {
                        bestMoves.add(move);
                    }
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("An error occurred during move calculation: " + e.getMessage());
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000;
        System.out.println("Minimax-Suche abgeschlossen in " + duration + " ms. Bester Wert: " + bestValue);

        if (!bestMoves.isEmpty()) {
            bestMove = bestMoves.get(random.nextInt(bestMoves.size()));
        }

        System.out.println("Computer wählt Zug: " + bestMove);

        return bestMove;
    }

    // New minimax signature to include the IsolaMove from the previous level
    private double minimax(IsolaBoard board, int depth, double alpha, double beta, int player, IsolaMove lastMove) {
        // Base case: Maximum depth reached
        if (depth == 0) {
            return evaluateBoard(board, player, lastMove); // Use the more detailed evaluation
        }

        // Base case: Check for win/loss state
        if (board.isPlayerIsolated(player)) {
            return (player == IsolaBoard.PLAYER1) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        }
        if (board.isPlayerIsolated(getOpponent(player))) {
            return (player == IsolaBoard.PLAYER1) ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
        }

        if (player == IsolaBoard.PLAYER1) {
            double maxEval = Double.NEGATIVE_INFINITY;
            List<IsolaMove> possibleMoves = generateAllPossibleMoves(board, player);

            if (possibleMoves.isEmpty()) {
                return Double.NEGATIVE_INFINITY;
            }

            for (IsolaMove move : possibleMoves) {
                IsolaBoard clonedBoard = board.clone();
                clonedBoard.movePlayer(player, move.moveToRow, move.moveToCol);
                clonedBoard.removeTile(move.removeTileRow, move.removeTileCol);

                double eval = minimax(clonedBoard, depth - 1, alpha, beta, getOpponent(player), move);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return maxEval;
        } else {
            double minEval = Double.POSITIVE_INFINITY;
            List<IsolaMove> possibleMoves = generateAllPossibleMoves(board, player);

            if (possibleMoves.isEmpty()) {
                return Double.POSITIVE_INFINITY;
            }

            for (IsolaMove move : possibleMoves) {
                IsolaBoard clonedBoard = board.clone();
                clonedBoard.movePlayer(player, move.moveToRow, move.moveToCol);
                clonedBoard.removeTile(move.removeTileRow, move.removeTileCol);

                double eval = minimax(clonedBoard, depth - 1, alpha, beta, getOpponent(player), move);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return minEval;
        }
    }

    // A simplified evaluateBoard for the base case that doesn't need a move object
    private double evaluateBoard(IsolaBoard board, int player, IsolaMove move) {
        int player1Moves = countPossibleMoves(board, IsolaBoard.PLAYER1);
        int player2Moves = countPossibleMoves(board, IsolaBoard.PLAYER2);

        double score = player1Moves - player2Moves;

        final double BONUS_FACTOR = 0.5;
        final double MALUS_FACTOR = 0.2;

        int opponent = getOpponent(player);
        int[] playerPos = (player == IsolaBoard.PLAYER1) ? board.getPlayer1Position() : board.getPlayer2Position();
        int[] opponentPos = (opponent == IsolaBoard.PLAYER1) ? board.getPlayer1Position() : board.getPlayer2Position();

        int distToOpponent = Math.abs(move.removeTileRow - opponentPos[0]) + Math.abs(move.removeTileCol - opponentPos[1]);
        int distToPlayer = Math.abs(move.removeTileRow - playerPos[0]) + Math.abs(move.removeTileCol - playerPos[1]);

        double bonus = 1.0 / (distToOpponent + 1) * BONUS_FACTOR;
        double malus = 1.0 / (distToPlayer + 1) * MALUS_FACTOR;

        score += bonus - malus;

        return score;
    }

    private double minimax(IsolaBoard board, int depth, double alpha, double beta, int player) {
        // Fallback for the first call from findBestMove
        return minimax(board, depth, alpha, beta, player, null);
    }

    private double evaluateBoard(IsolaBoard board, int player) {
        int player1Moves = countPossibleMoves(board, IsolaBoard.PLAYER1);
        int player2Moves = countPossibleMoves(board, IsolaBoard.PLAYER2);
        return player1Moves - player2Moves;
    }

    // ... rest of the code is unchanged ...
    private List<IsolaMove> generateAllPossibleMoves(IsolaBoard board, int player) {
        List<IsolaMove> moves = new ArrayList<>();
        int currentRow, currentCol;

        if (player == IsolaBoard.PLAYER1) {
            currentRow = board.getPlayer1Position()[0];
            currentCol = board.getPlayer1Position()[1];
        } else {
            currentRow = board.getPlayer2Position()[0];
            currentCol = board.getPlayer2Position()[1];
        }

        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;

                int newRow = currentRow + dr;
                int newCol = currentCol + dc;

                if (newRow >= 0 && newRow < 6 && newCol >= 0 && newCol < 8) {
                    IsolaBoard tempBoardForMoveCheck = board.clone();
                    if (tempBoardForMoveCheck.movePlayer(player, newRow, newCol)) {
                        for (int r = 0; r < 6; r++) {
                            for (int c = 0; c < 8; c++) {
                                IsolaBoard tempBoardForRemoveCheck = tempBoardForMoveCheck.clone();
                                if (tempBoardForRemoveCheck.removeTile(r, c)) {
                                    moves.add(new IsolaMove(currentRow, currentCol, newRow, newCol, r, c));
                                }
                            }
                        }
                    }
                }
            }
        }
        return moves;
    }

    private int countPossibleMoves(IsolaBoard board, int player) {
        int count = 0;
        int currentRow, currentCol;

        if (player == IsolaBoard.PLAYER1) {
            currentRow = board.getPlayer1Position()[0];
            currentCol = board.getPlayer1Position()[1];
        } else {
            currentRow = board.getPlayer2Position()[0];
            currentCol = board.getPlayer2Position()[1];
        }

        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;

                int newRow = currentRow + dr;
                int newCol = currentCol + dc;

                if (newRow >= 0 && newRow < 6 && newCol >= 0 && newCol < 8) {
                    IsolaBoard tempBoardForMoveCheck = board.clone();
                    if (tempBoardForMoveCheck.movePlayer(player, newRow, newCol)) {
                        for (int r = 0; r < 6; r++) {
                            for (int c = 0; c < 8; c++) {
                                IsolaBoard tempBoardForRemoveCheck = tempBoardForMoveCheck.clone();
                                if (tempBoardForRemoveCheck.removeTile(r, c)) {
                                    count++;
                                }
                            }
                        }
                    }
                }
            }
        }
        return count;
    }

    private int getOpponent(int player) {
        return (player == IsolaBoard.PLAYER1) ? IsolaBoard.PLAYER2 : IsolaBoard.PLAYER1;
    }

    public void shutdown() {
        executorService.shutdown();
    }
}