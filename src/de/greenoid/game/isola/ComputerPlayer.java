package de.greenoid.game.isola;

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

            // Diese Züge wurden bereits in generateAllPossibleMoves als gültig verifiziert
            // und werden hier auf dem geklonten Board ausgeführt.
            clonedBoard.movePlayer(currentPlayer, move.moveToRow, move.moveToCol); // Figurenbewegung
            clonedBoard.removeTile(move.removeTileRow, move.removeTileCol); // Stein entfernen

            double value = minimax(clonedBoard, maxSearchDepth - 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, getOpponent(currentPlayer));

            if (currentPlayer == IsolaBoard.PLAYER1) { // Computer ist PLAYER1 (Maximierer)
                if (value > bestValue) {
                    bestValue = value;
                    bestMoves.clear();
                    bestMoves.add(move);
                } else if (value == bestValue) {
                    bestMoves.add(move);
                }
            } else { // Computer ist PLAYER2 (Minimierer)
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

    private double minimax(IsolaBoard board, int depth, double alpha, double beta, int player) {
        // Basisfall: Maximale Tiefe erreicht oder Spiel beendet (Isolation)
        if (depth == 0) {
            return evaluateBoard(board, player);
        }

        // Prüfe Isolation am Knoten, um Gewinn/Verlust zu erkennen und Pruning zu ermöglichen
        if (board.isPlayerIsolated(player)) {
            return (player == IsolaBoard.PLAYER1) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        }
        if (board.isPlayerIsolated(getOpponent(player))) {
            return (player == IsolaBoard.PLAYER1) ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
        }


        // Maximierender Spieler (Computer versucht, seinen Score zu maximieren)
        if (player == IsolaBoard.PLAYER1) {
            double maxEval = Double.NEGATIVE_INFINITY;
            List<IsolaMove> possibleMoves = generateAllPossibleMoves(board, player);

            // Wenn keine Züge möglich sind, ist dieser Spieler isoliert. Dies ist ein Verlustzustand.
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
        // Minimierender Spieler (Gegner versucht, den Computer-Score zu minimieren)
        else {
            double minEval = Double.POSITIVE_INFINITY;
            List<IsolaMove> possibleMoves = generateAllPossibleMoves(board, player);

            // Wenn keine Züge möglich sind, ist dieser Spieler isoliert. Dies ist ein Gewinnzustand für den Gegner.
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
     * Generiert alle möglichen Züge (Figur bewegen + Stein entfernen) für einen Spieler.
     * DIESE METHODE WURDE KORRIGIERT.
     *
     * @param board  Das aktuelle Spielbrett.
     * @param player Der Spieler, für den Züge generiert werden sollen.
     * @return Eine Liste aller möglichen IsolaMove-Objekte.
     */
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

        // Iteriere über alle 8 möglichen Zielfelder (1 Feld Entfernung, auch diagonal)
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue; // Aktuelles Feld überspringen (keine Bewegung)

                int newRow = currentRow + dr;
                int newCol = currentCol + dc;

                // Stelle sicher, dass die neue Position innerhalb des Bretts ist
                if (newRow >= 0 && newRow < 6 && newCol >= 0 && newCol < 8) {
                    IsolaBoard tempBoardForMoveCheck = board.clone();

                    // VERSUCHE, die Figur auf dem geklonten Board zu bewegen.
                    // Nur WENN dieser Zug ERFOLGREICH ist (d.h. movePlayer gibt true zurück),
                    // dann generieren wir die möglichen Stein-Entfernungen.
                    if (tempBoardForMoveCheck.movePlayer(player, newRow, newCol)) {
                        // Wenn die Figur erfolgreich bewegt wurde, generiere alle möglichen Stein-Entfernungen
                        for (int r = 0; r < 6; r++) { // BOARD_ROWS
                            for (int c = 0; c < 8; c++) { // BOARD_COLS
                                IsolaBoard tempBoardForRemoveCheck = tempBoardForMoveCheck.clone(); // Brett nach Figurzug
                                // VERSUCHE, den Stein zu entfernen.
                                // Nur WENN dies ERFOLGREICH ist, ist es ein gültiger vollständiger Zug.
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


    /**
     * Heuristische Funktion zur Bewertung eines Spielzustandes.
     */
    private double evaluateBoard(IsolaBoard board, int player) {
        int player1Moves = countPossibleMoves(board, IsolaBoard.PLAYER1);
        int player2Moves = countPossibleMoves(board, IsolaBoard.PLAYER2);

        // Prüfen, ob ein Spieler bereits isoliert ist (Gewinn-/Verlustzustand)
        if (board.isPlayerIsolated(IsolaBoard.PLAYER1)) {
            return Double.NEGATIVE_INFINITY;
        }
        if (board.isPlayerIsolated(IsolaBoard.PLAYER2)) {
            return Double.POSITIVE_INFINITY;
        }

        double score = player1Moves - player2Moves;

        // Diese direkten Gewinn-/Verlustfälle sollten durch isPlayerIsolated bereits abgedeckt sein,
        // aber als Fallback kann es nicht schaden.
        // Der Spieler, der keine Züge mehr hat, hat verloren.
        if (player == IsolaBoard.PLAYER1 && player1Moves == 0) return Double.NEGATIVE_INFINITY;
        if (player == IsolaBoard.PLAYER2 && player2Moves == 0) return Double.POSITIVE_INFINITY;


        return score;
    }


    /**
     * Zählt die Anzahl der möglichen Züge für einen bestimmten Spieler auf dem aktuellen Board.
     * DIESE METHODE WURDE KORRIGIERT.
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
                    if (tempBoardForMoveCheck.movePlayer(player, newRow, newCol)) { // **Wichtig: Prüfung hier!**
                        for (int r = 0; r < 6; r++) {
                            for (int c = 0; c < 8; c++) {
                                IsolaBoard tempBoardForRemoveCheck = tempBoardForMoveCheck.clone();
                                if (tempBoardForRemoveCheck.removeTile(r, c)) { // **Wichtig: Prüfung hier!**
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