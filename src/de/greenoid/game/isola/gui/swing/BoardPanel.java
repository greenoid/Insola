package de.greenoid.game.isola.gui.swing;

import de.greenoid.game.isola.BoardState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Panel for displaying the game board.
 */
public class BoardPanel extends JPanel {
    private static final int BOARD_ROWS = 6;
    private static final int BOARD_COLS = 8;
    private static final int CELL_SIZE = 60;
    
    private int[][] boardState;
    private int player1Row, player1Col;
    private int player2Row, player2Col;
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
     */
    public void updateBoard(BoardState newBoardState) {
        this.boardState = newBoardState.getBoard();
        this.player1Row = newBoardState.getPlayer1Row();
        this.player1Col = newBoardState.getPlayer1Col();
        this.player2Row = newBoardState.getPlayer2Row();
        this.player2Col = newBoardState.getPlayer2Col();
        repaint();
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
        
        // Draw cell background
        switch (cellState) {
            case 0: // EMPTY (removed tile, sea water)
                g.setColor(Color.BLUE);
                break;
            case 1: // TILE (sand)
                g.setColor(new Color(255, 255, 224)); // Yellow-beige as sand
                break;
            case 2: // PLAYER1_START (brown)
                g.setColor(new Color(139, 69, 19)); // Brown
                break;
            case 3: // PLAYER2_START (brown)
                g.setColor(new Color(139, 69, 19)); // Brown
                break;
            default:
                g.setColor(new Color(255, 255, 224)); // Yellow-beige as sand
                break;
        }
        
        g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
        
        // Draw highlight if this is the selected cell
        if (row == highlightedRow && col == highlightedCol) {
            g.setColor(Color.YELLOW);
            Graphics2D g2d = (Graphics2D) g;
            Stroke originalStroke = g2d.getStroke();
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRect(x, y, CELL_SIZE, CELL_SIZE);
            g2d.setStroke(originalStroke);
        }
        
        // Draw cell border
        g.setColor(Color.BLACK);
        g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
        
        // Draw player positions
        if (boardRow == player1Row && boardCol == player1Col) {
            g.setColor(Color.RED); // Human Player 1 is red
            g.fillOval(x + 10, y + 10, CELL_SIZE - 20, CELL_SIZE - 20);
            g.setColor(Color.WHITE);
            g.drawString("P1", x + 20, y + 35);
        } else if (boardRow == player2Row && boardCol == player2Col) {
            g.setColor(Color.BLACK); // Computer Player 2 is black
            g.fillOval(x + 10, y + 10, CELL_SIZE - 20, CELL_SIZE - 20);
            g.setColor(Color.WHITE);
            g.drawString("P2", x + 20, y + 35);
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