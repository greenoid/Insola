package de.greenoid.game.isola.gui.swing;

import javax.swing.*;
import java.awt.*;

/**
 * Main game window for the Isola game.
 */
public class GameWindow extends JFrame {
    /**
     * Constructor to create a GameWindow with board and control panels.
     *
     * @param boardPanel The panel to display the game board
     * @param controlPanel The panel to display game controls
     */
    public GameWindow(JPanel boardPanel, JPanel controlPanel) {
        setTitle("Isola Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }
}