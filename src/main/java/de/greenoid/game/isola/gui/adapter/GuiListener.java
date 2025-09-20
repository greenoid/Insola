package de.greenoid.game.isola.gui.adapter;

/**
 * Interface for handling GUI events.
 */
public interface GuiListener {
    /**
     * Handle a player move event.
     * 
     * @param player The player making the move
     * @param fromRow The row the player is moving from
     * @param fromCol The column the player is moving from
     * @param toRow The row the player is moving to
     * @param toCol The column the player is moving to
     */
    void onPlayerMove(int player, int fromRow, int fromCol, int toRow, int toCol);
    
    /**
     * Handle a tile removal event.
     * 
     * @param row The row of the tile being removed
     * @param col The column of the tile being removed
     */
    void onTileRemove(int row, int col);
    
    /**
     * Handle a new game event.
     */
    void onNewGame();
    
    /**
     * Handle an exit event.
     */
    void onExit();
}