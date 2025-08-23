package de.greenoid.IsolaGem;

import de.greenoid.IsolaGem.IsolaGame;

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
        IsolaGame game = new IsolaGame();
        game.startGame();
    }
}
