package de.greenoid.IsolaGem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ComputerPlayer {

    private int maxSearchDepth;
    private Random random;

    public ComputerPlayer(int maxSearchDepth) {
        this.maxSearchDepth = maxSearchDepth;
        this.random = new Random();
    }

    /**
     * Ermittelt den besten Zug für den Computergegner mithilfe des Minimax-Algorithmus.
     *
     * @param board          Das aktuelle Spielbrett.
     * @param currentPlayer  Der Spieler, für den der Zug berechnet werden soll.
     * @return Der beste IsolaMove, der gefunden wurde.
     */
    public IsolaMove findBestMove(IsolaBoard board, int currentPlayer) {
        long startTime = System.nanoTime();
        IsolaMove bestMove = null;
        double bestValue = (currentPlayer == IsolaBoard.PLAYER1) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        List<IsolaMove> bestMoves = new ArrayList<>();

        List<IsolaMove> possibleMoves = generateAllPossibleMoves(board, currentPlayer);

        if (possibleMoves.isEmpty()) {
            System.out.println("ComputerPlayer: Keine möglichen Züge gefunden.");
            return null;
        }

        for (IsolaMove move : possibleMoves) {
            IsolaBoard clonedBoard = board.clone();

            clonedBoard.movePlayer(currentPlayer, move.moveToRow, move.moveToCol);
            clonedBoard.removeTile(move.removeTileRow, move.removeTileCol);

            double value = minimax(clonedBoard, maxSearchDepth - 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, getOpponent(currentPlayer));

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

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000;
        System.out.println("Minimax-Suche abgeschlossen in " + duration + " ms. Bester Wert: " + bestValue);

        if (!bestMoves.isEmpty()) {
            bestMove = bestMoves.get(random.nextInt(bestMoves.size()));
        }

        return bestMove;
    }

    /**
     * Der Minimax-Algorithmus mit Alpha-Beta-Pruning.
     * Die Rekursion bricht ab, wenn die maximale Tiefe erreicht oder ein Gewinn-/Verlustzustand gefunden wird.
     *
     * @param board  Das aktuelle Spielbrett.
     * @param depth  Aktuelle Suchtiefe.
     * @param alpha  Bestmöglicher Wert für den Maximierer.
     * @param beta   Bestmöglicher Wert für den Minimierer.
     * @param player Der Spieler, dessen Zug bewertet wird.
     * @return Die Bewertung des aktuellen Knotens.
     */
    private double minimax(IsolaBoard board, int depth, double alpha, double beta, int player) {
        // Basisfall: Maximale Tiefe erreicht
        if (depth == 0) {
            return evaluateBoard(board, player);
        }

        // Basisfall: Gewinn-/Verlust-Zustand direkt am Knoten prüfen (wichtig für Pruning)
        if (board.isPlayerIsolated(player)) {
            // Der Spieler, der am Zug ist, ist isoliert. Das ist ein Verlust für ihn.
            return (player == IsolaBoard.PLAYER1) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        }
        if (board.isPlayerIsolated(getOpponent(player))) {
            // Der Gegner des Spielers, der am Zug ist, ist isoliert. Das ist ein Gewinn für ihn.
            return (player == IsolaBoard.PLAYER1) ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
        }

        // Maximierender Spieler (Computer)
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
        }
        // Minimierender Spieler (Gegner)
        else {
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

    /**
     * Generates all possible moves (figure move + remove tile) for a player.
     * This version optimizes by only considering tile removals that are
     * within a certain distance from both players to speed up the search.
     *
     * @param board The current game board.
     * @param player The player for whom moves should be generated.
     * @return A list of all possible IsolaMove objects.
     */
    private List<IsolaMove> generateAllPossibleMoves(IsolaBoard board, int player) {
        List<IsolaMove> moves = new ArrayList<>();
        int currentRow, currentCol;
        int opponentRow, opponentCol;

        final int MAX_REMOVE_DISTANCE = 3; // You can adjust this value

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

        // Iterate over all 8 possible target fields (1 field distance, also diagonal)
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;

                int newRow = currentRow + dr;
                int newCol = currentCol + dc;

                if (newRow >= 0 && newRow < 6 && newCol >= 0 && newCol < 8) {
                    IsolaBoard tempBoardForMoveCheck = board.clone();

                    if (tempBoardForMoveCheck.movePlayer(player, newRow, newCol)) {

                        // Generate all possible tile removals
                        for (int r = 0; r < 6; r++) {
                            for (int c = 0; c < 8; c++) {
                                // Calculate Manhattan distance from the new player position
                                int distFromPlayer = Math.abs(r - newRow) + Math.abs(c - newCol);
                                // Calculate Manhattan distance from the opponent's position
                                int distFromOpponent = Math.abs(r - opponentRow) + Math.abs(c - opponentCol);

                                // Heuristic: Only consider removing tiles that are within the set distance from both players.
                                // This is a more robust check.
                                if (distFromPlayer <= MAX_REMOVE_DISTANCE && distFromOpponent <= MAX_REMOVE_DISTANCE) {
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
     * Heuristische Funktion zur Bewertung eines Spielzustandes.
     *
     * @param board  Das zu bewertende Spielbrett.
     * @param player Der Spieler, der als nächstes am Zug wäre (für diesen wird bewertet).
     * @return Eine heuristische Bewertung des Bretts.
     */
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

    /**
     * Zählt die Anzahl der möglichen Züge für einen bestimmten Spieler auf dem aktuellen Board.
     *
     * @param board  Das zu prüfende Board.
     * @param player Der Spieler, dessen Züge gezählt werden sollen.
     * @return Die Anzahl der möglichen Züge für diesen Spieler.
     */
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
}