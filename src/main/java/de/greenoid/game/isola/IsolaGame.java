package de.greenoid.game.isola;

import java.util.InputMismatchException;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IsolaGame {
    private static final Logger log = LogManager.getLogger(IsolaGame.class);

    private IsolaBoard board;
    private int currentPlayer;
    private Scanner scanner;
    private ComputerPlayer computerPlayer;
    private final int COMPUTER_PLAYER_ID = IsolaBoard.PLAYER2;
    
    // Track the current game phase
    private GamePhase currentGamePhase;
    
    // Track game status
    private GameStatus gameStatus;

    public IsolaGame() {
        board = new IsolaBoard();
        currentPlayer = IsolaBoard.PLAYER1;
        scanner = new Scanner(System.in);
        computerPlayer = new ComputerPlayer(5);
        currentGamePhase = GamePhase.MOVE_PLAYER;  // Game starts with moving player
        gameStatus = GameStatus.ONGOING;  // Game starts ongoing
    }

    public void startGame() {
        System.out.println("Willkommen zu Isola!");
        System.out.println("Du spielst als Spieler 1 (P1). Der Computer spielt als Spieler 2 (P2).");
        board.printBoard();

        while (true) {
            System.out.println("\n-------------------------");
            System.out.println("Aktueller Spieler: " + (currentPlayer == IsolaBoard.PLAYER1 ? "Mensch (P1)" : "Computer (P2)"));

            IsolaMove currentMove = null;

            if (currentPlayer == COMPUTER_PLAYER_ID) {
                System.out.println("Computer berechnet seinen Zug...");
                currentMove = computerPlayer.findBestMove(board, currentPlayer);

                if (currentMove == null) {
                    System.out.println("Computer kann keinen Zug mehr machen! Mensch gewinnt!");
                    break;
                }

                // Computer moves player and removes tile
                currentGamePhase = GamePhase.MOVE_PLAYER;
                board.movePlayer(currentPlayer, currentMove.moveToRow, currentMove.moveToCol);
                currentGamePhase = GamePhase.REMOVE_TILE;
                board.removeTile(currentMove.removeTileRow, currentMove.removeTileCol);
                currentGamePhase = GamePhase.MOVE_PLAYER;  // Reset for next player

                System.out.println("Computer zieht: " + currentMove);
                board.printBoard();

            } else {
                // Human player's turn - first move the player
                currentGamePhase = GamePhase.MOVE_PLAYER;
                boolean moveSuccessful = false;
                while (!moveSuccessful) {
                    System.out.println("Figur bewegen (aktuelle Position: P1 bei (" + board.getPlayer1Position()[0] + "," + board.getPlayer1Position()[1] + "))");
                    System.out.print("Gib die neue Zeile ein: ");
                    int newRow = getUserInput();
                    System.out.print("Gib die neue Spalte ein: ");
                    int newCol = getUserInput();

                    moveSuccessful = board.movePlayer(currentPlayer, newRow, newCol);
                    if (!moveSuccessful) {
                        System.out.println("Ungültiger Zug, bitte erneut versuchen.");
                    }
                }
                board.printBoard();

                // Then remove a tile
                currentGamePhase = GamePhase.REMOVE_TILE;
                boolean removeSuccessful = false;
                while (!removeSuccessful) {
                    System.out.println("Spielstein entfernen:");
                    System.out.print("Gib die Zeile des zu entfernenden Steins ein: ");
                    int removeRow = getUserInput();
                    System.out.print("Gib die Spalte des zu entfernenden Steins ein: ");
                    int removeCol = getUserInput();

                    removeSuccessful = board.removeTile(removeRow, removeCol);
                    if (!removeSuccessful) {
                        System.out.println("Ungültige Auswahl, bitte erneut versuchen.");
                    }
                }
                board.printBoard();
                // Reset game phase for next player
                currentGamePhase = GamePhase.MOVE_PLAYER;
            }

            int otherPlayer = (currentPlayer == IsolaBoard.PLAYER1) ? IsolaBoard.PLAYER2 : IsolaBoard.PLAYER1;
            if (board.isPlayerIsolated(otherPlayer)) {
                System.out.println("\n-------------------------");
                System.out.println("Spieler " + (otherPlayer == IsolaBoard.PLAYER1 ? "1" : "2") + " ist isoliert!");
                System.out.println("Spieler " + (currentPlayer == IsolaBoard.PLAYER1 ? "1 (Mensch)" : "2 (Computer)") + " GEWINNT!");
                // Set game status based on who won
                gameStatus = (currentPlayer == IsolaBoard.PLAYER1) ? GameStatus.PLAYER1_WON : GameStatus.PLAYER2_WON;
                break;
            }

            switchPlayer();
        }
        scanner.close();
        // After the game loop ends, shut down the computer player
        computerPlayer.shutdown();
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == IsolaBoard.PLAYER1) ? IsolaBoard.PLAYER2 : IsolaBoard.PLAYER1;
    }

    private int getUserInput() {
        while (true) {
            try {
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Ungültige Eingabe. Bitte gib eine ganze Zahl ein.");
                scanner.next();
                System.out.print("Erneut versuchen: ");
            }
        }
    }

    /**
     * Returns the current state of the game for UI consumption
     * @return IsolaGameState object representing the current game state
     */
    public IsolaGameState getGameState() {
        // Create a BoardState object from the current board
        BoardState currentBoardState = new BoardState(
            6,  // rows
            8,  // cols
            board.getBoard(),
            board.getPlayer1Row(),
            board.getPlayer1Col(),
            board.getPlayer2Row(),
            board.getPlayer2Col()
        );
        
        return new IsolaGameState(currentPlayer, currentGamePhase, currentBoardState, gameStatus);
    }
    
    /**
     * Move a player to a new position on the board.
     *
     * @param player The player to move (IsolaBoard.PLAYER1 or IsolaBoard.PLAYER2)
     * @param newRow The row to move to
     * @param newCol The column to move to
     * @return true if the move was successful, false otherwise
     */
    public boolean movePlayer(int player, int newRow, int newCol) {
        boolean result = board.movePlayer(player, newRow, newCol);
        
        // Check if the move resulted in a win
        if (result) {
            int otherPlayer = (player == IsolaBoard.PLAYER1) ?
                             IsolaBoard.PLAYER2 :
                             IsolaBoard.PLAYER1;
            if (board.isPlayerIsolated(otherPlayer)) {
                // Update game status
                gameStatus = (player == IsolaBoard.PLAYER1) ?
                            GameStatus.PLAYER1_WON :
                            GameStatus.PLAYER2_WON;
            }
        }
        
        return result;
    }
    
    /**
     * Remove a tile from the board.
     *
     * @param row The row of the tile to remove
     * @param col The column of the tile to remove
     * @return true if the tile was successfully removed, false otherwise
     */
    public boolean removeTile(int row, int col) {
        boolean result = board.removeTile(row, col);
        
        // Check if the tile removal resulted in a win
        if (result) {
            int otherPlayer = (currentPlayer == IsolaBoard.PLAYER1) ?
                             IsolaBoard.PLAYER2 :
                             IsolaBoard.PLAYER1;
            if (board.isPlayerIsolated(otherPlayer)) {
                // Update game status
                gameStatus = (currentPlayer == IsolaBoard.PLAYER1) ?
                            GameStatus.PLAYER1_WON :
                            GameStatus.PLAYER2_WON;
            }
        }
        
        return result;
    }
    
    /**
     * Check if a player is isolated (cannot make any valid move).
     *
     * @param player The player to check (IsolaBoard.PLAYER1 or IsolaBoard.PLAYER2)
     * @return true if the player is isolated, false otherwise
     */
    public boolean isPlayerIsolated(int player) {
        return board.isPlayerIsolated(player);
    }
    
    /**
     * Switch to the next player's turn.
     */
    public void switchToNextPlayer() {
        switchPlayer();
    }
    
    /**
     * Get the current game phase.
     *
     * @return The current game phase (MOVE_PLAYER or REMOVE_TILE)
     */
    public GamePhase getCurrentGamePhase() {
        return currentGamePhase;
    }
    
    /**
     * Set the current game phase.
     *
     * @param phase The game phase to set (MOVE_PLAYER or REMOVE_TILE)
     */
    public void setCurrentGamePhase(GamePhase phase) {
        this.currentGamePhase = phase;
    }
    
    /**
     * Get the current game board.
     *
     * @return The current game board
     */
    public IsolaBoard getBoard() {
        return board;
    }

    public static void main(String[] args) {
        IsolaGame game = new IsolaGame();
        game.startGame();
    }
}