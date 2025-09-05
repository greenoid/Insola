package de.greenoid.game.isola;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Enum representing the current status of the Isola game.
 */
public enum GameStatus {
    ONGOING,        // Game is still in progress
    PLAYER1_WON,    // Player 1 has won
    PLAYER2_WON,    // Player 2 has won
    DRAW;            // Game ended in a draw (unlikely in Isola)
    
    private static final Logger log = LogManager.getLogger(GameStatus.class);
}