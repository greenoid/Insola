package de.greenoid.game.isola;

import de.greenoid.game.isola.gui.swing.SwingGui;
import de.greenoid.game.isola.gui.common.GuiController;

/**
 * Main class to start the Isola game.
 * This class is the entry point for the application,
 * making it runnable from a JAR file.
 */
public class Main {
    
    /**
     * The main method that creates an instance of IsolaGame
     * and starts the game.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // Check if GUI mode is requested
        boolean guiMode = false;
        for (String arg : args) {
            if ("-gui".equals(arg)) {
                guiMode = true;
                break;
            }
        }
        
        if (guiMode) {
            // Initialize and start the Swing GUI
            IsolaGame game = new IsolaGame();
            GuiController controller = new GuiController(game);
            SwingGui swingGui = new SwingGui(controller);
            swingGui.showGameWindow();
        } else {
            // Run the console version
            IsolaGame game = new IsolaGame();
            game.startGame();
        }
    }
}
