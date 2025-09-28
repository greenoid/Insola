package de.greenoid.game.isola;

/**
 * Enum representing the current status of the Isola game.
 */
public enum GameStatus {
    ONGOING,        // Game is still in progress
    PLAYER1_WON,    // Player 1 has won
    PLAYER2_WON,    // Player 2 has won
    DRAW;            // Game ended in a draw (unlikely in Isola)
    
}