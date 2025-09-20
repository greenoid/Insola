package de.greenoid.game.isola.gui.common;

import de.greenoid.game.isola.IsolaGame;
import de.greenoid.game.isola.IsolaGameState;

/**
 * Controller class that acts as an intermediary between the GUI and the core game logic.
 */
public class GuiController {
    private IsolaGame game;
    
    /**
     * Constructor to create a GuiController with an IsolaGame instance.
     *
     * @param game The IsolaGame instance to control
     */
    public GuiController(IsolaGame game) {
        this.game = game;
    }
    
    /**
     * Get the current game state for UI display.
     *
     * @return IsolaGameState object representing the current game state
     */
    public IsolaGameState getGameState() {
        return game.getGameState();
    }
    
    /**
     * Move a player to a new position.
     *
     * @param player The player to move (IsolaBoard.PLAYER1 or IsolaBoard.PLAYER2)
     * @param newRow The row to move to
     * @param newCol The column to move to
     * @return true if the move was successful, false otherwise
     */
    public boolean movePlayer(int player, int newRow, int newCol) {
        return game.movePlayer(player, newRow, newCol);
    }
    
    /**
     * Remove a tile from the board.
     *
     * @param row The row of the tile to remove
     * @param col The column of the tile to remove
     * @return true if the tile was successfully removed, false otherwise
     */
    public boolean removeTile(int row, int col) {
        return game.removeTile(row, col);
    }
    
    /**
     * Check if a player is isolated (cannot make any valid move).
     *
     * @param player The player to check (IsolaBoard.PLAYER1 or IsolaBoard.PLAYER2)
     * @return true if the player is isolated, false otherwise
     */
    public boolean isPlayerIsolated(int player) {
        return game.isPlayerIsolated(player);
    }
    
    /**
     * Start a new game.
     */
    public void startNewGame() {
        game = new IsolaGame();
    }
    
    /**
     * Switch to the next player's turn.
     */
    public void switchToNextPlayer() {
        game.switchToNextPlayer();
    }
    
    /**
     * Get the current game phase.
     *
     * @return The current game phase (MOVE_PLAYER or REMOVE_TILE)
     */
    public de.greenoid.game.isola.GamePhase getCurrentGamePhase() {
        return game.getCurrentGamePhase();
    }
    
    /**
     * Set the current game phase.
     *
     * @param phase The game phase to set (MOVE_PLAYER or REMOVE_TILE)
     */
    public void setCurrentGamePhase(de.greenoid.game.isola.GamePhase phase) {
        game.setCurrentGamePhase(phase);
    }
    
    /**
     * Get the underlying IsolaGame instance.
     *
     * @return The IsolaGame instance
     */
    public IsolaGame getGame() {
        return game;
    }
}