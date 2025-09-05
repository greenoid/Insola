package de.greenoid.game.isola.gui.adapter;

import de.greenoid.game.isola.gui.common.GuiController;
import de.greenoid.game.isola.IsolaGameState;

/**
 * Abstract class that provides a common interface for different GUI frameworks.
 */
public abstract class GuiAdapter {
    protected GuiController controller;
    
    /**
     * Constructor to create a GuiAdapter with a GuiController.
     * 
     * @param controller The GuiController to use for game logic interaction
     */
    public GuiAdapter(GuiController controller) {
        this.controller = controller;
    }
    
    /**
     * Update the GUI with the current game state.
     * 
     * @param state The current game state
     */
    public abstract void updateGameState(IsolaGameState state);
    
    /**
     * Show the main game window.
     */
    public abstract void showGameWindow();
    
    /**
     * Close the main game window.
     */
    public abstract void closeGameWindow();
    
    /**
     * Show a message to the user.
     * 
     * @param message The message to display
     */
    public abstract void showMessage(String message);
}