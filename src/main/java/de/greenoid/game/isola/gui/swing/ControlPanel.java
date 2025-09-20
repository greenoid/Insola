package de.greenoid.game.isola.gui.swing;

import de.greenoid.game.isola.IsolaGameState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Panel for game controls and status display.
 */
public class ControlPanel extends JPanel {
    private JLabel statusLabel;
    private JButton newGameButton;
    private JButton exitButton;
    private ActionListener buttonListener;
    
    /**
     * Constructor to create a ControlPanel.
     * 
     * @param gui The SwingGui instance to notify of user interactions
     */
    public ControlPanel(SwingGui gui) {
        initializeComponents();
    }
    
    /**
     * Initialize the control panel components.
     */
    private void initializeComponents() {
        setLayout(new FlowLayout());
        
        statusLabel = new JLabel("Game Status: ONGOING");
        add(statusLabel);
        
        newGameButton = new JButton("New Game");
        newGameButton.setActionCommand("New Game");
        add(newGameButton);
        
        exitButton = new JButton("Exit");
        exitButton.setActionCommand("Exit");
        add(exitButton);
    }
    
    /**
     * Set the action listener for the buttons.
     * 
     * @param listener The ActionListener to notify of button clicks
     */
    public void setButtonListener(ActionListener listener) {
        this.buttonListener = listener;
        newGameButton.addActionListener(listener);
        exitButton.addActionListener(listener);
    }
    
    /**
     * Update the status display with the current game state.
     * 
     * @param state The current game state
     */
    public void updateStatus(IsolaGameState state) {
        String currentPlayer = (state.getCurrentPlayer() == 4) ? "Player 1" : "Player 2";
        String gamePhase = (state.getGamePhase().toString().equals("MOVE_PLAYER")) ? 
                          "Move Player" : "Remove Tile";
        String gameStatus = state.getGameStatus().toString();
        
        statusLabel.setText("Current Player: " + currentPlayer + 
                           " | Phase: " + gamePhase + 
                           " | Status: " + gameStatus);
    }
}