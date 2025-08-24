package de.greenoid.IsolaGem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.HashSet;
import java.util.Set;
import java.util.Queue;
import java.util.LinkedList;

public class ComputerPlayer {

    private int maxSearchDepth;
    private Random random;
    private final ExecutorService executorService;
    private final int BOARD_SIZE = 6 * 8;

    // Configurable threshold to increase search depth
    private static final int DEPTH_INCREASE_THRESHOLD = 8;

    public ComputerPlayer(int maxSearchDepth) {
        this.maxSearchDepth = maxSearchDepth;
        this.random = new Random();
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public IsolaMove findBestMove(IsolaBoard board, int currentPlayer) {
        long startTime = System.nanoTime();
        IsolaMove bestMove = null;

        // 1. Determine the effective search depth based on the opponent's mobility
        int opponentReachableTiles = countOpponentReachableTiles(board, currentPlayer);
        int effectiveMaxDepth = 3;

        if (opponentReachableTiles < DEPTH_INCREASE_THRESHOLD) {
            effectiveMaxDepth = 4;
        }

        if (effectiveMaxDepth > maxSearchDepth) {
            effectiveMaxDepth = maxSearchDepth;
        }

        System.out.println("Opponent reachable tiles: " + opponentReachableTiles + ". Effective Max Depth: " + effectiveMaxDepth);

        // 2. Generate moves using the heuristic
        List<IsolaMove> movesToEvaluate = getHeuristicBestMoves(board, currentPlayer);

        // Fallback: If heuristic pruning gives an an empty list, use all moves.
        if (movesToEvaluate.isEmpty()) {
            movesToEvaluate = getAllPossibleMoves(board, currentPlayer);
        }

        // Final sanity check for game end
        if (movesToEvaluate.isEmpty()) {
            return null;
        }

        // Sort the moves to evaluate the most promising ones first
        movesToEvaluate.sort(Comparator.comparingDouble(move -> {
            int opponentPlayerId = getOpponent(currentPlayer);
            int[] opponentPos = (opponentPlayerId == IsolaBoard.PLAYER1) ? board.getPlayer1Position() : board.getPlayer2Position();
            double distToOpponent = Math.abs(move.removeTileRow - opponentPos[0]) + Math.abs(move.removeTileCol - opponentPos[1]);
            return distToOpponent;
        }));

        List<IsolaMove> finalBestMoves = new ArrayList<>();
        double finalBestValue = (currentPlayer == IsolaBoard.PLAYER1) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;

        for (int currentDepth = 1; currentDepth <= effectiveMaxDepth; currentDepth++) {
            System.out.println("Starting search at depth: " + currentDepth);

            final int finalCurrentDepth = currentDepth;

            // Limit the branch factor to manage performance.
            int effectiveBranchFactor = 10;
            if (opponentReachableTiles < 25) {
                effectiveBranchFactor = 15;
            }
            if (opponentReachableTiles < 15) {
                effectiveBranchFactor = movesToEvaluate.size();
            }

            List<IsolaMove> movesForMinimax = movesToEvaluate.size() > effectiveBranchFactor ?
                    movesToEvaluate.subList(0, effectiveBranchFactor) : movesToEvaluate;

            List<Future<Double>> futures = new ArrayList<>();
            List<IsolaMove> currentBestMoves = new ArrayList<>();
            double currentBestValue = (currentPlayer == IsolaBoard.PLAYER1) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;

            for (IsolaMove move : movesForMinimax) {
                Callable<Double> task = () -> {
                    IsolaBoard clonedBoard = board.clone();
                    clonedBoard.movePlayer(currentPlayer, move.moveToRow, move.moveToCol);
                    clonedBoard.removeTile(move.removeTileRow, move.removeTileCol);
                    return minimax(clonedBoard, finalCurrentDepth - 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, getOpponent(currentPlayer));
                };
                futures.add(executorService.submit(task));
            }

            try {
                for (int i = 0; i < movesForMinimax.size(); i++) {
                    IsolaMove move = movesForMinimax.get(i);
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

    /**
     * Generates a pruned list of moves by only considering removing tiles
     * that are within 3 steps of the opponent's current position.
     * @param board The current game board.
     * @param player The current player.
     * @return A list of heuristically chosen best moves.
     */
    private List<IsolaMove> getHeuristicBestMoves(IsolaBoard board, int player) {
        List<IsolaMove> moves = new ArrayList<>();
        int currentRow, currentCol;
        int opponentRow, opponentCol;

        if (player == IsolaBoard.PLAYER1) {
            currentRow = board.getPlayer1Position()[0];
            currentCol = board.getPlayer1Position()[1];
            opponentRow = board.getPlayer2Position()[0];
            opponentCol = board.getPlayer2Position()[1];
        } else {
            currentRow = board.getPlayer2Position()[0];
            currentCol = board.getPlayer2Position()[1];
            opponentRow = board.getPlayer1Position()[0];
            opponentCol = board.getPlayer1Position()[1];
        }

        // Iterate over all possible player moves
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;

                int newRow = currentRow + dr;
                int newCol = currentCol + dc;

                if (newRow >= 0 && newRow < 6 && newCol >= 0 && newCol < 8) {
                    IsolaBoard tempBoardForMoveCheck = board.clone();
                    if (tempBoardForMoveCheck.movePlayer(player, newRow, newCol)) {
                        // For each valid player move, iterate over possible tile removals
                        for (int r = 0; r < 6; r++) {
                            for (int c = 0; c < 8; c++) {
                                // Calculate the Manhattan distance from the tile to the opponent
                                int distToOpponent = Math.abs(r - opponentRow) + Math.abs(c - opponentCol);

                                // Only consider removing tiles within a certain distance
                                if (distToOpponent <= 3) {
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
        }
        return moves;
    }

    /**
     * Generates all physically possible moves (figure move + tile removal).
     * This is used as a fallback and for game-ending condition checks.
     * @param board The current game board.
     * @param player The current player.
     * @return A list of all physically possible moves.
     */
    private List<IsolaMove> getAllPossibleMoves(IsolaBoard board, int player) {
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

    private double minimax(IsolaBoard board, int depth, double alpha, double beta, int player) {
        if (depth == 0 || board.isPlayerIsolated(player) || board.isPlayerIsolated(getOpponent(player))) {
            return evaluateBoard(board, player);
        }

        if (player == IsolaBoard.PLAYER1) {
            double maxEval = Double.NEGATIVE_INFINITY;
            List<IsolaMove> possibleMoves = getAllPossibleMoves(board, player);

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
            List<IsolaMove> possibleMoves = getAllPossibleMoves(board, player);

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

    /**
     * Counts the number of tiles a player can reach in up to 3 moves using a BFS.
     * @param board The current board.
     * @param player The player to check.
     * @return The number of reachable tiles.
     */
    private int countOpponentReachableTiles(IsolaBoard board, int player) {
        Set<String> visited = new HashSet<>();
        Queue<int[]> queue = new LinkedList<>();

        int opponentPlayerId = getOpponent(player);
        int[] opponentPos = (opponentPlayerId == IsolaBoard.PLAYER1) ? board.getPlayer1Position() : board.getPlayer2Position();

        // Initial position added
        String startKey = opponentPos[0] + "," + opponentPos[1];
        queue.add(opponentPos);
        visited.add(startKey);

        int depth = 0;

        while (!queue.isEmpty() && depth < 3) {
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                int[] currentPos = queue.poll();

                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        if (dr == 0 && dc == 0) continue;

                        int newRow = currentPos[0] + dr;
                        int newCol = currentPos[1] + dc;

                        String newKey = newRow + "," + newCol;

                        if (newRow >= 0 && newRow < 6 && newCol >= 0 && newCol < 8 &&
                                board.isTileEmpty(newRow, newCol) && !visited.contains(newKey)) {

                            visited.add(newKey);
                            queue.add(new int[]{newRow, newCol});
                        }
                    }
                }
            }
            depth++;
        }

        // Subtract 1 because we don't count the opponent's starting tile as a reachable move.
        return visited.size() - 1;
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

        // New heuristic: distance to the nearest starting point
        int[] p1Pos = board.getPlayer1Position();
        int[] p2Pos = board.getPlayer2Position();

        int[] p1Start = new int[]{0, 3};
        int[] p2Start = new int[]{5, 4};

        // Calculate distance for Player 1 to both starting points
        double p1DistToP1Start = Math.abs(p1Pos[0] - p1Start[0]) + Math.abs(p1Pos[1] - p1Start[1]);
        double p1DistToP2Start = Math.abs(p1Pos[0] - p2Start[0]) + Math.abs(p1Pos[1] - p2Start[1]);
        double p1DistToNearestStart = Math.min(p1DistToP1Start, p1DistToP2Start);

        // Calculate distance for Player 2 to both starting points
        double p2DistToP1Start = Math.abs(p2Pos[0] - p1Start[0]) + Math.abs(p2Pos[1] - p1Start[1]);
        double p2DistToP2Start = Math.abs(p2Pos[0] - p2Start[0]) + Math.abs(p2Pos[1] - p2Start[1]);
        double p2DistToNearestStart = Math.min(p2DistToP1Start, p2DistToP2Start);

        // A smaller distance is better, so we subtract it from the score for the current player
        // and add it for the opponent. We use a small factor to not make it the only deciding factor.
        if (player == IsolaBoard.PLAYER1) {
            score -= p1DistToNearestStart * 0.1;
            score += p2DistToNearestStart * 0.1;
        } else {
            score -= p2DistToNearestStart * 0.1;
            score += p1DistToNearestStart * 0.1;
        }

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
