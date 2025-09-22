package de.greenoid.game.isola.gui.swing;

import de.greenoid.game.isola.BoardState;
import de.greenoid.game.isola.GameStatus;
import de.greenoid.game.isola.gui.util.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * Panel for displaying the game board.
 */
public class BoardPanel extends JPanel {
    private static final int BOARD_ROWS = 6;
    private static final int BOARD_COLS = 8;
    private static final int CELL_SIZE = 128;
    
    private int[][] boardState;
    private int player1Row, player1Col;
    private int player2Row, player2Col;
    private GameStatus gameStatus = GameStatus.ONGOING;
    private SwingGui gui;
    private int highlightedRow = -1;
    private int highlightedCol = -1;
    
    /**
     * Constructor to create a BoardPanel.
     *
     * @param gui The SwingGui instance to notify of user interactions
     */
    public BoardPanel(SwingGui gui) {
        this.gui = gui;
        this.boardState = new int[BOARD_ROWS][BOARD_COLS];
        initializeBoard();
    }
    
    /**
     * Initialize the board panel.
     */
    private void initializeBoard() {
        setPreferredSize(new Dimension(BOARD_COLS * CELL_SIZE, BOARD_ROWS * CELL_SIZE));
        addMouseListener(new BoardMouseListener());
    }
    
    /**
     * Update the board display with new state.
     *
     * @param newBoardState The new board state to display
     * @param gameStatus The current game status
     */
    public void updateBoard(BoardState newBoardState, GameStatus gameStatus) {
        this.boardState = newBoardState.getBoard();
        this.player1Row = newBoardState.getPlayer1Row();
        this.player1Col = newBoardState.getPlayer1Col();
        this.player2Row = newBoardState.getPlayer2Row();
        this.player2Col = newBoardState.getPlayer2Col();
        this.gameStatus = gameStatus;
        repaint();
    }
    
    /**
     * Update the board display with new state (backward compatibility).
     *
     * @param newBoardState The new board state to display
     */
    public void updateBoard(BoardState newBoardState) {
        updateBoard(newBoardState, GameStatus.ONGOING);
    }
    
    /**
     * Highlight a specific cell.
     *
     * @param row The row of the cell to highlight
     * @param col The column of the cell to highlight
     */
    public void highlightCell(int row, int col) {
        highlightedRow = row;
        highlightedCol = col;
        repaint();
    }
    
    /**
     * Clear any highlighted cell.
     */
    public void clearHighlight() {
        highlightedRow = -1;
        highlightedCol = -1;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
    }
    
    /**
     * Draw the game board.
     *
     * @param g The graphics context to draw with
     */
    private void drawBoard(Graphics g) {
        // Draw the game board with current state
        for (int row = 0; row < BOARD_ROWS; row++) {
            for (int col = 0; col < BOARD_COLS; col++) {
                drawCell(g, row, col, boardState[row][col], row, col);
            }
        }
    }
    
    /**
     * Draw a single cell of the board.
     *
     * @param g The graphics context to draw with
     * @param row The row of the cell
     * @param col The column of the cell
     * @param cellState The state of the cell
     * @param boardRow The actual row on the board
     * @param boardCol The actual column on the board
     */
    private void drawCell(Graphics g, int row, int col, int cellState, int boardRow, int boardCol) {
        int x = col * CELL_SIZE;
        int y = row * CELL_SIZE;
        
        // Draw cell background image
        java.awt.Image backgroundImage = null;
        switch (cellState) {
            case 0: // EMPTY (removed tile, sea water)
                backgroundImage = ImageLoader.getBlockedTileImage();
                break;
            case 1: // TILE (sand)
                backgroundImage = ImageLoader.getNormalTileImage();
                break;
            case 2: // PLAYER1_START (red base)
                backgroundImage = ImageLoader.getPlayer1BaseImage();
                break;
            case 3: // PLAYER2_START (black base)
                backgroundImage = ImageLoader.getPlayer2BaseImage();
                break;
            default:
                backgroundImage = ImageLoader.getNormalTileImage();
                break;
        }
        
        // Draw the background image if available
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, x, y, CELL_SIZE, CELL_SIZE, null);
        }
        
        // Draw highlight if this is the selected cell
        if (row == highlightedRow && col == highlightedCol) {
            Graphics2D g2d = (Graphics2D) g;
            Stroke originalStroke = g2d.getStroke();
            g2d.setStroke(new BasicStroke(3));
            g2d.setColor(Color.YELLOW);
            g2d.drawRect(x, y, CELL_SIZE, CELL_SIZE);
            g2d.setStroke(originalStroke);
        }
        
        // Draw cell border
        g.setColor(Color.BLACK);
        g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
        
        // Draw player positions with images
        java.awt.Image playerImage = null;
        if (boardRow == player1Row && boardCol == player1Col) {
            // Determine which image to use for Player 1 based on game status
            if (gameStatus == GameStatus.PLAYER1_WON) {
                playerImage = ImageLoader.getPlayer1VictoryImage();
            } else {
                playerImage = ImageLoader.getPlayer1CharacterImage();
            }
        } else if (boardRow == player2Row && boardCol == player2Col) {
            // Determine which image to use for Player 2 based on game status
            if (gameStatus == GameStatus.PLAYER2_WON) {
                playerImage = ImageLoader.getPlayer2VictoryImage();
            } else {
                playerImage = ImageLoader.getPlayer2CharacterImage();
            }
        }
        
        // Draw the player image if available
        if (playerImage != null) {
            // Scale the image to fit within the cell (slightly smaller than cell size)
            int imageSize = CELL_SIZE - 10; // 10 pixels padding
            int imageX = x + 5; // 5 pixels padding on left
            int imageY = y + 5; // 5 pixels padding on top
            g.drawImage(playerImage, imageX, imageY, imageSize, imageSize, null);
        }
    }
    
    /**
     * Mouse listener for handling board clicks.
     */
    private class BoardMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            int col = e.getX() / CELL_SIZE;
            int row = e.getY() / CELL_SIZE;
            
            // Notify the GUI about the click
            gui.handleBoardClick(row, col);
        }
    }
}