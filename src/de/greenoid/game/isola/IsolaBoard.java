package de.greenoid.game.isola;
public class IsolaBoard {

    // Konstanten für den Zustand der Felder
    public static final int EMPTY = 0; // Feld ist leer
    public static final int TILE = 1;  // Feld enthält einen Spielstein
    public static final int PLAYER1_START = 2; // Startfeld von Spieler 1
    public static final int PLAYER2_START = 3; // Startfeld von Spieler 2

    // Die folgenden Konstanten sind für die Identifikation des Spielers
    public static final int PLAYER1 = 4;
    public static final int PLAYER2 = 5;


    // Sichtbarkeit für Klonen anpassen (Package-Private)
    int[][] board;
    int player1Row, player1Col; // Aktuelle Position von Spieler 1
    int player2Row, player2Col; // Aktuelle Position von Spieler 2

    private final int BOARD_ROWS = 6;
    private final int BOARD_COLS = 8;

    /**
     * Konstruktor zur Initialisierung des Spielbretts.
     */
    public IsolaBoard() {
        board = new int[BOARD_ROWS][BOARD_COLS];
        initializeBoard();
    }

    /**
     * Initialisiert das Spielbrett gemäß den Isola-Regeln.
     */
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

    /**
     * Versucht, eine Spielfigur zu bewegen.
     */
    public boolean movePlayer(int player, int newRow, int newCol) {
        if (newRow < 0 || newRow >= BOARD_ROWS || newCol < 0 || newCol >= BOARD_COLS) {
            //System.out.println("Zug ungültig: Außerhalb des Bretts.");
            return false;
        }

        if ((newRow == player1Row && newCol == player1Col && player != PLAYER1) ||
                (newRow == player2Row && newCol == player2Col && player != PLAYER2)) {
            //System.out.println("Zug ungültig: Zielfeld ist von anderer Figur besetzt.");
            return false;
        }

        if (board[newRow][newCol] == EMPTY &&
                board[newRow][newCol] != PLAYER1_START &&
                board[newRow][newCol] != PLAYER2_START) {
            //System.out.println("Zug ungültig: Zielfeld ist leer und kein Startfeld.");
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
            //System.out.println("Ungültiger Spieler.");
            return false;
        }

        int rowDiff = Math.abs(newRow - currentRow);
        int colDiff = Math.abs(newCol - currentCol);

        if (rowDiff > 1 || colDiff > 1 || (rowDiff == 0 && colDiff == 0)) {
            //System.out.println("Zug ungültig: Figur kann nur ein Feld weit ziehen.");
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

    /**
     * Entfernt einen Spielstein vom Brett.
     */
    public boolean removeTile(int row, int col) {
        if (row < 0 || row >= BOARD_ROWS || col < 0 || col >= BOARD_COLS) {
            //System.out.println("Entfernen ungültig: Außerhalb des Bretts.");
            return false;
        }

        if ((row == player1Row && col == player1Col) || (row == player2Row && col == player2Col)) {
            //System.out.println("Entfernen ungültig: Feld ist von einer Spielfigur besetzt.");
            return false;
        }

        if (board[row][col] == TILE) {
            board[row][col] = EMPTY;
            return true;
        } else {
            //System.out.println("Entfernen ungültig: Feld enthält keinen Spielstein.");
            return false;
        }
    }

    /**
     * Überprüft, ob ein Spieler isoliert ist.
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
            return false;
        }

        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;

                int newRow = currentRow + dr;
                int newCol = currentCol + dc;

                if (newRow >= 0 && newRow < BOARD_ROWS && newCol >= 0 && newCol < BOARD_COLS) {
                    if ((board[newRow][newCol] == TILE || board[newRow][newCol] == PLAYER1_START || board[newRow][newCol] == PLAYER2_START) &&
                            !((newRow == player1Row && newCol == player1Col && player != PLAYER1) ||
                                    (newRow == player2Row && newCol == player2Col && player != PLAYER2))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Gibt die aktuelle Position von Spieler 1 zurück.
     */
    public int[] getPlayer1Position() {
        return new int[]{player1Row, player1Col};
    }

    /**
     * Gibt die aktuelle Position von Spieler 2 zurück.
     */
    public int[] getPlayer2Position() {
        return new int[]{player2Row, player2Col};
    }

    /**
     * Gibt eine Textrepräsentation des Spielbretts aus.
     */
    public void printBoard() {
        System.out.println("-------------------------");
        for (int r = 0; r < BOARD_ROWS; r++) {
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
            System.out.println();
        }
        System.out.println("-------------------------");
    }

    /**
     * Erstellt eine tiefe Kopie dieses IsolaBoard-Objekts.
     */
    public IsolaBoard clone() {
        IsolaBoard clonedBoard = new IsolaBoard();

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
}