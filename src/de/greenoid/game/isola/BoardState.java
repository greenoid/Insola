package de.greenoid.game.isola;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Represents the state of an Isola game board for UI rendering
 */
public class BoardState {
    private static final Logger log = LogManager.getLogger(BoardState.class);
    private int rows;
    private int cols;
    private int[][] board;
    private int player1Row, player1Col;
    private int player2Row, player2Col;
    
    /**
     * Constructor to initialize the BoardState
     * 
     * @param rows Number of rows in the board
     * @param cols Number of columns in the board
     * @param board 2D array representing the board state
     * @param player1Row Player 1's row position
     * @param player1Col Player 1's column position
     * @param player2Row Player 2's row position
     * @param player2Col Player 2's column position
     */
    public BoardState(int rows, int cols, int[][] board, int player1Row, int player1Col, int player2Row, int player2Col) {
        this.rows = rows;
        this.cols = cols;
        // Create a deep copy of the board to ensure encapsulation
        this.board = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(board[i], 0, this.board[i], 0, cols);
        }
        this.player1Row = player1Row;
        this.player1Col = player1Col;
        this.player2Row = player2Row;
        this.player2Col = player2Col;
    }
    
    /**
     * Get the number of rows in the board
     * 
     * @return Number of rows
     */
    public int getRows() {
        return rows;
    }
    
    /**
     * Get the number of columns in the board
     * 
     * @return Number of columns
     */
    public int getCols() {
        return cols;
    }
    
    /**
     * Get the board state as a 2D array
     * 
     * @return 2D array representing the board state
     */
    public int[][] getBoard() {
        // Return a deep copy to ensure encapsulation
        int[][] boardCopy = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(this.board[i], 0, boardCopy[i], 0, cols);
        }
        return boardCopy;
    }
    
    /**
     * Get the state of a specific cell
     * 
     * @param row Row index
     * @param col Column index
     * @return State of the cell at the specified position
     */
    public int getCellState(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            throw new IllegalArgumentException("Invalid row or column index");
        }
        return board[row][col];
    }
    
    /**
     * Get Player 1's row position
     * 
     * @return Player 1's row position
     */
    public int getPlayer1Row() {
        return player1Row;
    }
    
    /**
     * Get Player 1's column position
     * 
     * @return Player 1's column position
     */
    public int getPlayer1Col() {
        return player1Col;
    }
    
    /**
     * Get Player 1's position as an array
     * 
     * @return Array containing [row, col] for Player 1
     */
    public int[] getPlayer1Position() {
        return new int[]{player1Row, player1Col};
    }
    
    /**
     * Get Player 2's row position
     * 
     * @return Player 2's row position
     */
    public int getPlayer2Row() {
        return player2Row;
    }
    
    /**
     * Get Player 2's column position
     * 
     * @return Player 2's column position
     */
    public int getPlayer2Col() {
        return player2Col;
    }
    
    /**
     * Get Player 2's position as an array
     * 
     * @return Array containing [row, col] for Player 2
     */
    public int[] getPlayer2Position() {
        return new int[]{player2Row, player2Col};
    }
    
    /**
     * Check if a position is occupied by Player 1
     * 
     * @param row Row index
     * @param col Column index
     * @return true if Player 1 is at the specified position, false otherwise
     */
    public boolean isPlayer1At(int row, int col) {
        return player1Row == row && player1Col == col;
    }
    
    /**
     * Check if a position is occupied by Player 2
     * 
     * @param row Row index
     * @param col Column index
     * @return true if Player 2 is at the specified position, false otherwise
     */
    public boolean isPlayer2At(int row, int col) {
        return player2Row == row && player2Col == col;
    }
}