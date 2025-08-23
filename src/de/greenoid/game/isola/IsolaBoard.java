package de.greenoid.IsolaGem;

public class IsolaBoard {

    public static final int EMPTY = 0;
    public static final int TILE = 1;
    public static final int PLAYER1_START = 2;
    public static final int PLAYER2_START = 3;

    public static final int PLAYER1 = 4;
    public static final int PLAYER2 = 5;

    private final int BOARD_ROWS = 6;
    private final int BOARD_COLS = 8;

    int[][] board;
    int player1Row, player1Col;
    int player2Row, player2Col;

    public IsolaBoard() {
        this.board = new int[BOARD_ROWS][BOARD_COLS];
        initializeBoard();
    }

    private IsolaBoard(boolean forCloning) {
        this.board = new int[BOARD_ROWS][BOARD_COLS];
    }

    private void initializeBoard() {
        for (int r = 0; r < BOARD_ROWS; r++) {
            for (int c = 0; c < BOARD_COLS; c++) {
                board[r][c] = TILE;
            }
        }
        board[5][3] = PLAYER1_START;
        board[0][4] = PLAYER2_START;

        player1Row = 5;
        player1Col = 3;
        player2Row = 0;
        player2Col = 4;
    }

    public boolean movePlayer(int player, int newRow, int newCol) {
        if (newRow < 0 || newRow >= BOARD_ROWS || newCol < 0 || newCol >= BOARD_COLS) {
            return false;
        }

        int currentRow, currentCol;
        if (player == PLAYER1) {
            currentRow = player1Row;
            currentCol = player1Col;
        } else if (player == PLAYER2) {
            currentRow = player2Row;
            currentCol = player2Col;
        } else {
            return false;
        }

        int rowDiff = Math.abs(newRow - currentRow);
        int colDiff = Math.abs(newCol - currentCol);

        if (rowDiff > 1 || colDiff > 1 || (rowDiff == 0 && colDiff == 0)) {
            return false;
        }

        if ((newRow == player1Row && newCol == player1Col && player != PLAYER1) ||
                (newRow == player2Row && newCol == player2Col && player != PLAYER2)) {
            return false;
        }

        if (board[newRow][newCol] == EMPTY &&
                board[newRow][newCol] != PLAYER1_START &&
                board[newRow][newCol] != PLAYER2_START) {
            return false;
        }

        if (player == PLAYER1) {
            player1Row = newRow;
            player1Col = newCol;
        } else {
            player2Row = newRow;
            player2Col = newCol;
        }
        return true;
    }

    public boolean removeTile(int row, int col) {
        if (row < 0 || row >= BOARD_ROWS || col < 0 || col >= BOARD_COLS) {
            return false;
        }

        if ((row == player1Row && col == player1Col) || (row == player2Row && col == player2Col)) {
            return false;
        }

        if (board[row][col] == TILE || board[row][col] == PLAYER1_START || board[row][col] == PLAYER2_START) {
            board[row][col] = EMPTY;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if a player is isolated (cannot make any valid move + tile removal).
     *
     * @param player The player to check.
     * @return true if the player is isolated, otherwise false.
     */
    public boolean isPlayerIsolated(int player) {
        int currentRow, currentCol;
        if (player == PLAYER1) {
            currentRow = player1Row;
            currentCol = player1Col;
        } else if (player == PLAYER2) {
            currentRow = player2Row;
            currentCol = player2Col;
        } else {
            return false; // Invalid player ID
        }

        // Check all 8 neighboring squares for a valid move
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;

                int newRow = currentRow + dr;
                int newCol = currentCol + dc;

                // Check if the destination is within board boundaries
                if (newRow >= 0 && newRow < BOARD_ROWS && newCol >= 0 && newCol < BOARD_COLS) {

                    // Create a deep copy of the board to simulate the move
                    IsolaBoard tempBoard = this.clone();

                    // Simulate the player's move. If it is valid...
                    if (tempBoard.movePlayer(player, newRow, newCol)) {

                        // ...then check if there's at least one tile to remove on the *new* board state
                        for (int r = 0; r < BOARD_ROWS; r++) {
                            for (int c = 0; c < BOARD_COLS; c++) {
                                // We check if the removal is valid on the cloned board.
                                // `removeTile()` correctly checks if the tile is not empty and not occupied by a player.
                                if (tempBoard.removeTile(r, c)) {
                                    return false; // A valid move + tile removal combo was found, so the player is NOT isolated
                                }
                            }
                        }
                    }
                }
            }
        }
        return true; // No valid move + tile removal combinations were found
    }

    public int[] getPlayer1Position() {
        return new int[]{player1Row, player1Col};
    }

    public int[] getPlayer2Position() {
        return new int[]{player2Row, player2Col};
    }

    public void printBoard() {
        System.out.println("--------------------------------");
        System.out.print("   ");
        for (int c = 0; c < BOARD_COLS; c++) {
            System.out.print(" " + c + "  ");
        }
        System.out.println();
        System.out.println("   --------------------------------");

        for (int r = 0; r < BOARD_ROWS; r++) {
            System.out.print(r + " |");
            for (int c = 0; c < BOARD_COLS; c++) {
                if (r == player1Row && c == player1Col) {
                    System.out.print(" P1 ");
                } else if (r == player2Row && c == player2Col) {
                    System.out.print(" P2 ");
                } else {
                    switch (board[r][c]) {
                        case EMPTY:
                            System.out.print(" -- ");
                            break;
                        case TILE:
                            System.out.print(" [] ");
                            break;
                        case PLAYER1_START:
                            System.out.print(" S1 ");
                            break;
                        case PLAYER2_START:
                            System.out.print(" S2 ");
                            break;
                    }
                }
            }
            System.out.println("| " + r);
        }
        System.out.println("   --------------------------------");
        System.out.print("   ");
        for (int c = 0; c < BOARD_COLS; c++) {
            System.out.print(" " + c + "  ");
        }
        System.out.println();
    }

    public IsolaBoard clone() {
        IsolaBoard clonedBoard = new IsolaBoard(true);

        for (int r = 0; r < BOARD_ROWS; r++) {
            for (int c = 0; c < BOARD_COLS; c++) {
                clonedBoard.board[r][c] = this.board[r][c];
            }
        }

        clonedBoard.player1Row = this.player1Row;
        clonedBoard.player1Col = this.player1Col;
        clonedBoard.player2Row = this.player2Row;
        clonedBoard.player2Col = this.player2Col;

        return clonedBoard;
    }

    // IsolaBoard.java

    /**
     * Counts the number of tiles that can be removed from the board.
     * This includes TILE, PLAYER1_START, and PLAYER2_START.
     * @return The number of free tiles.
     */
    public int countRemovableTiles() {
        int count = 0;
        for (int r = 0; r < BOARD_ROWS; r++) {
            for (int c = 0; c < BOARD_COLS; c++) {
                if (board[r][c] == TILE || board[r][c] == PLAYER1_START || board[r][c] == PLAYER2_START) {
                    count++;
                }
            }
        }
        // Subtract the two player positions if they are on start fields
        if (board[player1Row][player1Col] == PLAYER1_START) {
            count--;
        }
        if (board[player2Row][player2Col] == PLAYER2_START) {
            count--;
        }
        return count;
    }

}