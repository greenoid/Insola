package de.greenoid.game.isola;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Enum representing the current phase of a player's turn in Isola game.
 */
public enum GamePhase {
    MOVE_PLAYER,    // Player needs to move their position
    REMOVE_TILE;     // Player needs to remove a tile
    
    private static final Logger log = LogManager.getLogger(GamePhase.class);
}