package de.greenoid.game.isola.gui.swing;

import de.greenoid.game.isola.gui.adapter.GuiAdapter;
import de.greenoid.game.isola.gui.common.GuiController;
import de.greenoid.game.isola.IsolaGameState;
import de.greenoid.game.isola.GamePhase;
import de.greenoid.game.isola.IsolaBoard;
import de.greenoid.game.isola.ComputerPlayer;
import de.greenoid.game.isola.IsolaMove;
import de.greenoid.game.isola.IsolaGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main Swing GUI implementation.
 */
public class SwingGui extends GuiAdapter implements ActionListener {
    private GameWindow gameWindow;
    private BoardPanel boardPanel;
    private ControlPanel controlPanel;
    
    // Game state tracking
    private boolean movePhase = true; // true for move phase, false for remove tile phase
    
    /**
     * Constructor to create a SwingGui with a GuiController.
     *
     * @param controller The GuiController to use for game logic interaction
     */
    public SwingGui(GuiController controller) {
        super(controller);
        initializeComponents();
    }
    
    /**
     * Initialize the GUI components.
     */
    private void initializeComponents() {
        boardPanel = new BoardPanel(this);
        controlPanel = new ControlPanel(this);
        gameWindow = new GameWindow(boardPanel, controlPanel);
        
        // Register this as action listener for control panel buttons
        controlPanel.setButtonListener(this);
    }
    
    @Override
    public void updateGameState(IsolaGameState state) {
        boardPanel.updateBoard(state.getBoardState());
        controlPanel.updateStatus(state);
        
        // Update internal state tracking
        movePhase = (state.getGamePhase() == GamePhase.MOVE_PLAYER);
        
        // Check if it's computer player's turn (P2)
        if (state.getCurrentPlayer() == IsolaBoard.PLAYER2 &&
            state.getGameStatus() == de.greenoid.game.isola.GameStatus.ONGOING) {
            // Use SwingUtilities.invokeLater to ensure UI updates are processed first
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    handleComputerMove();
                }
            });
        }
    }
    
    @Override
    public void showGameWindow() {
        gameWindow.setVisible(true);
        
        // Initial update
        updateGameState(controller.getGameState());
    }
    
    @Override
    public void closeGameWindow() {
        gameWindow.dispose();
    }
    
    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(gameWindow, message);
    }
    
    /**
     * Handle a board cell click.
     *
     * @param row The row of the clicked cell
     * @param col The column of the clicked cell
     */
    public void handleBoardClick(int row, int col) {
        IsolaGameState currentState = controller.getGameState();
        
        if (movePhase) {
            // Move phase - directly use clicked position as destination
            // Automatically determine source as current player's position
            int player = currentState.getCurrentPlayer();
            int sourceRow, sourceCol;
            
            if (player == IsolaBoard.PLAYER1) {
                sourceRow = currentState.getBoardState().getPlayer1Row();
                sourceCol = currentState.getBoardState().getPlayer1Col();
            } else {
                sourceRow = currentState.getBoardState().getPlayer2Row();
                sourceCol = currentState.getBoardState().getPlayer2Col();
            }
            
            // Check if the move is valid
            if (isValidMove(sourceRow, sourceCol, row, col, currentState)) {
                // Perform the move
                if (controller.movePlayer(player, row, col)) {
                    // Move successful, now switch to remove tile phase
                    controller.setCurrentGamePhase(de.greenoid.game.isola.GamePhase.REMOVE_TILE);
                    movePhase = false;
                    boardPanel.clearHighlight();
                    
                    // Check if game is over
                    int otherPlayer = (player == IsolaBoard.PLAYER1) ? IsolaBoard.PLAYER2 : IsolaBoard.PLAYER1;
                    if (controller.isPlayerIsolated(otherPlayer)) {
                        // Update the display to show the final state before announcing the winner
                        updateGameState(controller.getGameState());
                        
                        // Use SwingUtilities.invokeLater to ensure UI updates are processed first
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                String winner = (player == IsolaBoard.PLAYER1) ? "Player 1" : "Player 2";
                                showMessage(winner + " wins!");
                            }
                        });
                        return;
                    }
                } else {
                    showMessage("Invalid move!");
                }
            } else {
                showMessage("Invalid move!");
            }
        } else {
            // Remove tile phase
            if (isValidTileToRemove(row, col, currentState)) {
                if (controller.removeTile(row, col)) {
                    // Tile removal successful, check if game is over
                    int player = currentState.getCurrentPlayer();
                    int otherPlayer = (player == IsolaBoard.PLAYER1) ? IsolaBoard.PLAYER2 : IsolaBoard.PLAYER1;
                    if (controller.isPlayerIsolated(otherPlayer)) {
                        // Update the display to show the final state before announcing the winner
                        updateGameState(controller.getGameState());
                        
                        // Use SwingUtilities.invokeLater to ensure UI updates are processed first
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                String winner = (player == IsolaBoard.PLAYER1) ? "Player 1" : "Player 2";
                                showMessage(winner + " wins!");
                            }
                        });
                        return;
                    }
                    
                    // Switch to next player
                    controller.switchToNextPlayer();
                    controller.setCurrentGamePhase(de.greenoid.game.isola.GamePhase.MOVE_PLAYER);
                    movePhase = true;
                } else {
                    showMessage("Cannot remove this tile!");
                }
            } else {
                showMessage("Invalid tile to remove!");
            }
        }
        
        // Update the display
        updateGameState(controller.getGameState());
    }
    
    /**
     * Check if a position is a valid source for moving a player.
     *
     * @param row The row to check
     * @param col The column to check
     * @param state The current game state
     * @return true if the position is a valid source, false otherwise
     */
    
    /**
     * Check if a move is valid.
     *
     * @param fromRow The row to move from
     * @param fromCol The column to move from
     * @param toRow The row to move to
     * @param toCol The column to move to
     * @param state The current game state
     * @return true if the move is valid, false otherwise
     */
    private boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol, IsolaGameState state) {
        // Check if destination is within board boundaries
        if (toRow < 0 || toRow >= 6 || toCol < 0 || toCol >= 8) {
            return false;
        }
        
        // Check if move is to an adjacent cell (including diagonally)
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);
        if (rowDiff > 1 || colDiff > 1 || (rowDiff == 0 && colDiff == 0)) {
            return false;
        }
        
        // Check if destination is not occupied by another player
        int player = state.getCurrentPlayer();
        
        if (player == IsolaBoard.PLAYER1) {
            if (toRow == state.getBoardState().getPlayer2Row() &&
                toCol == state.getBoardState().getPlayer2Col()) {
                return false;
            }
        } else {
            if (toRow == state.getBoardState().getPlayer1Row() &&
                toCol == state.getBoardState().getPlayer1Col()) {
                return false;
            }
        }
        
        // Check if destination is not an empty tile
        int[][] board = state.getBoardState().getBoard();
        if (board[toRow][toCol] == 0) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Check if a tile is valid to remove.
     *
     * @param row The row of the tile
     * @param col The column of the tile
     * @param state The current game state
     * @return true if the tile is valid to remove, false otherwise
     */
    private boolean isValidTileToRemove(int row, int col, IsolaGameState state) {
        // Check if position is within board boundaries
        if (row < 0 || row >= 6 || col < 0 || col >= 8) {
            return false;
        }
        
        // Check if tile is not occupied by a player
        if ((row == state.getBoardState().getPlayer1Row() &&
             col == state.getBoardState().getPlayer1Col()) ||
            (row == state.getBoardState().getPlayer2Row() &&
             col == state.getBoardState().getPlayer2Col())) {
            return false;
        }
        
        // Check if tile is a regular tile (not a starting position)
        int[][] board = state.getBoardState().getBoard();
        if (board[row][col] != 1) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        
        switch (command) {
            case "New Game":
                handleNewGame();
                break;
            case "Exit":
                handleExit();
                break;
        }
    }
    
    /**
     * Handle a new game event.
     */
    public void handleNewGame() {
        controller.startNewGame();
        movePhase = true;
        boardPanel.clearHighlight();
        updateGameState(controller.getGameState());
    }
    
    /**
     * Handle an exit event.
     */
    public void handleExit() {
        int result = JOptionPane.showConfirmDialog(
            gameWindow,
            "Are you sure you want to exit?",
            "Confirm Exit",
            JOptionPane.YES_NO_OPTION
        );
        
        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
    
    /**
     * Handle computer player move.
     */
    private void handleComputerMove() {
        // Create a computer player instance
        ComputerPlayer computerPlayer = new ComputerPlayer(5);
        
        // Get the current game state
        IsolaGameState currentState = controller.getGameState();
        
        // Get the underlying IsolaGame instance from the controller
        IsolaGame game = controller.getGame();
        
        // Get the computer's move
        IsolaMove move = computerPlayer.findBestMove(game.getBoard(), IsolaBoard.PLAYER2);
        
        if (move != null) {
            // Perform the computer's move
            if (controller.movePlayer(IsolaBoard.PLAYER2, move.moveToRow, move.moveToCol)) {
                // Move successful, now remove tile
                if (controller.removeTile(move.removeTileRow, move.removeTileCol)) {
                    // Tile removal successful, check if game is over
                    if (controller.isPlayerIsolated(IsolaBoard.PLAYER1)) {
                        // Update the display to show the final state before announcing the winner
                        updateGameState(controller.getGameState());
                        
                        // Use SwingUtilities.invokeLater to ensure UI updates are processed first
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                showMessage("Player 2 wins!");
                            }
                        });
                        return;
                    }
                    
                    // Switch to next player
                    controller.switchToNextPlayer();
                }
            }
        } else {
            // Computer cannot make a move, human player wins
            // Update the display to show the final state before announcing the winner
            updateGameState(controller.getGameState());
            
            // Use SwingUtilities.invokeLater to ensure UI updates are processed first
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    showMessage("Computer cannot make a move! Player 1 wins!");
                }
            });
        }
        
        // Update the display
        updateGameState(controller.getGameState());
        
        // Shutdown the computer player
        computerPlayer.shutdown();
    }
}