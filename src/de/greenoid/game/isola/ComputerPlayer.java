package de.greenoid.IsolaGem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.HashSet;
import java.util.Set;

public class ComputerPlayer {

    private int maxSearchDepth;
    private Random random;
    private final ExecutorService executorService;
    private final int BOARD_SIZE = 6 * 8;

    public ComputerPlayer(int maxSearchDepth) {
        this.maxSearchDepth = maxSearchDepth;
        this.random = new Random();
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public IsolaMove findBestMove(IsolaBoard board, int currentPlayer) {
        long startTime = System.nanoTime();
        IsolaMove bestMove = null;

        // First, generate all possible moves. This list is needed for subsequent logic.
        List<IsolaMove> allPossibleMoves = generateAllPossibleMoves(board, currentPlayer);

        if (allPossibleMoves.isEmpty()) {
            return null;
        }

        // Now, with the allPossibleMoves list available, we can dynamically adjust the depth and branch factor.
        int tilesLeft = board.countRemovableTiles();
        int reachableTiles = countReachableTilesInTwoMoves(board, currentPlayer);
        int effectiveMaxDepth;
        int effectiveBranchFactor;

        // Revised Logic for Depth and Branch Factor
        if (tilesLeft >= 40) { // Early game
            effectiveMaxDepth = 3;
            effectiveBranchFactor = 12; // Increased to be more proactive
        } else if (tilesLeft >= 30) {
            effectiveMaxDepth = 4;
            effectiveBranchFactor = 15;
        } else if (tilesLeft >= 20) {
            effectiveMaxDepth = 5;
            effectiveBranchFactor = 20;
        } else { // Endgame
            effectiveMaxDepth = maxSearchDepth;
            effectiveBranchFactor = allPossibleMoves.size();
        }

        // Adjust based on immediate danger
        if (reachableTiles < 10) {
            effectiveMaxDepth += 2; // Add bonus depth if cornered
        }
        if (effectiveMaxDepth > maxSearchDepth) {
            effectiveMaxDepth = maxSearchDepth;
        }

        System.out.println("Current tiles left: " + tilesLeft + ". Reachable tiles: " + reachableTiles + ". Effective Max Depth: " + effectiveMaxDepth + ". Effective Branch Factor: " + effectiveBranchFactor);

        // Updated sorting heuristic
        allPossibleMoves.sort(Comparator.comparingDouble(move -> {
            int opponentPlayerId = getOpponent(currentPlayer);
            int[] opponentPos = (opponentPlayerId == IsolaBoard.PLAYER1) ? board.getPlayer1Position() : board.getPlayer2Position();

            // A good move removes a tile near the opponent, and far from the player
            double distToOpponent = Math.abs(move.removeTileRow - opponentPos[0]) + Math.abs(move.removeTileCol - opponentPos[1]);
            double distToPlayer = Math.abs(move.removeTileRow - move.moveToRow) + Math.abs(move.removeTileCol - move.moveToCol);

            // Weighting: High bonus for distance to opponent, small penalty for distance to self
            return distToOpponent - 0.5 * distToPlayer;
        }));

        List<IsolaMove> finalBestMoves = new ArrayList<>();
        double finalBestValue = (currentPlayer == IsolaBoard.PLAYER1) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;

        for (int currentDepth = 1; currentDepth <= effectiveMaxDepth; currentDepth++) {
            System.out.println("Starting search at depth: " + currentDepth);

            final int finalCurrentDepth = currentDepth;

            List<IsolaMove> movesToEvaluate = allPossibleMoves.size() > effectiveBranchFactor ?
                    allPossibleMoves.subList(0, effectiveBranchFactor) : allPossibleMoves;

            List<Future<Double>> futures = new ArrayList<>();
            List<IsolaMove> currentBestMoves = new ArrayList<>();
            double currentBestValue = (currentPlayer == IsolaBoard.PLAYER1) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;

            for (IsolaMove move : movesToEvaluate) {
                Callable<Double> task = () -> {
                    IsolaBoard clonedBoard = board.clone();
                    clonedBoard.movePlayer(currentPlayer, move.moveToRow, move.moveToCol);
                    clonedBoard.removeTile(move.removeTileRow, move.removeTileCol);
                    return minimax(clonedBoard, finalCurrentDepth - 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, getOpponent(currentPlayer));
                };
                futures.add(executorService.submit(task));
            }

            try {
                for (int i = 0; i < movesToEvaluate.size(); i++) {
                    IsolaMove move = movesToEvaluate.get(i);
                    double value = futures.get(i).get();

                    if (currentPlayer == IsolaBoard.PLAYER1) {
                        if (value > currentBestValue) {
                            currentBestValue = value;
                            currentBestMoves.clear();
                            currentBestMoves.add(move);
                        } else if (value == currentBestValue) {
                            currentBestMoves.add(move);
                        }
                    } else {
                        if (value < currentBestValue) {
                            currentBestValue = value;
                            currentBestMoves.clear();
                            currentBestMoves.add(move);
                        } else if (value == currentBestValue) {
                            currentBestMoves.add(move);
                        }
                    }
                }

                if (!currentBestMoves.isEmpty()) {
                    finalBestMoves = currentBestMoves;
                    finalBestValue = currentBestValue;
                } else {
                    break;
                }
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("An error occurred during move calculation: " + e.getMessage());
                break;
            }
        }

        if (!finalBestMoves.isEmpty()) {
            bestMove = finalBestMoves.get(random.nextInt(finalBestMoves.size()));
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000;
        System.out.println("Minimax-Suche abgeschlossen in " + duration + " ms. Bester Wert: " + finalBestValue);

        System.out.println("Computer wählt Zug: " + bestMove);

        return bestMove;
    }

    private int countReachableTilesInTwoMoves(IsolaBoard board, int player) {
        Set<String> reachableTiles = new HashSet<>();
        int currentRow, currentCol;

        if (player == IsolaBoard.PLAYER1) {
            currentRow = board.getPlayer1Position()[0];
            currentCol = board.getPlayer1Position()[1];
        } else {
            currentRow = board.getPlayer2Position()[0];
            currentCol = board.getPlayer2Position()[1];
        }

        // First move
        for (int dr1 = -1; dr1 <= 1; dr1++) {
            for (int dc1 = -1; dc1 <= 1; dc1++) {
                if (dr1 == 0 && dc1 == 0) continue;

                int firstMoveRow = currentRow + dr1;
                int firstMoveCol = currentCol + dc1;

                if (firstMoveRow >= 0 && firstMoveRow < 6 && firstMoveCol >= 0 && firstMoveCol < 8 &&
                        board.isTileEmpty(firstMoveRow, firstMoveCol)) {

                    // Second move from the first position
                    for (int dr2 = -1; dr2 <= 1; dr2++) {
                        for (int dc2 = -1; dc2 <= 1; dc2++) {
                            if (dr2 == 0 && dc2 == 0) continue;

                            int secondMoveRow = firstMoveRow + dr2;
                            int secondMoveCol = firstMoveCol + dc2;

                            if (secondMoveRow >= 0 && secondMoveRow < 6 && secondMoveCol >= 0 && secondMoveCol < 8 &&
                                    board.isTileEmpty(secondMoveRow, secondMoveCol)) {
                                reachableTiles.add(secondMoveRow + "," + secondMoveCol);
                            }
                        }
                    }
                }
            }
        }
        return reachableTiles.size();
    }

    private double minimax(IsolaBoard board, int depth, double alpha, double beta, int player) {
        if (depth == 0 || board.isPlayerIsolated(player) || board.isPlayerIsolated(getOpponent(player))) {
            return evaluateBoard(board, player);
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

                double eval = minimax(clonedBoard, depth - 1, alpha, beta, getOpponent(player));
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

                double eval = minimax(clonedBoard, depth - 1, alpha, beta, getOpponent(player));
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return minEval;
        }
    }

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

    private double evaluateBoard(IsolaBoard board, int player) {
        int player1Moves = countPossibleMoves(board, IsolaBoard.PLAYER1);
        int player2Moves = countPossibleMoves(board, IsolaBoard.PLAYER2);

        if (board.isPlayerIsolated(IsolaBoard.PLAYER1)) {
            return Double.NEGATIVE_INFINITY;
        }
        if (board.isPlayerIsolated(IsolaBoard.PLAYER2)) {
            return Double.POSITIVE_INFINITY;
        }

        double score = player1Moves - player2Moves;

        if (player == IsolaBoard.PLAYER1 && player2Moves == 0) return Double.POSITIVE_INFINITY;
        if (player == IsolaBoard.PLAYER2 && player1Moves == 0) return Double.NEGATIVE_INFINITY;

        return score;
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