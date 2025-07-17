package de.greenoid.game.isola;
import java.util.InputMismatchException;
import java.util.Scanner;

public class IsolaGame {

    private IsolaBoard board;
    private int currentPlayer;
    private Scanner scanner;
    private ComputerPlayer computerPlayer;
    private final int COMPUTER_PLAYER_ID = IsolaBoard.PLAYER2;

    public IsolaGame() {
        board = new IsolaBoard();
        currentPlayer = IsolaBoard.PLAYER1;
        scanner = new Scanner(System.in);
        computerPlayer = new ComputerPlayer(2); // Original 4, dauert zu lange, testweise 2
    }

    public void startGame() {
        System.out.println("Willkommen zu Isola!");
        System.out.println("Du spielst als Spieler 1 (P1). Der Computer spielt als Spieler 2 (P2).");
        board.printBoard();

        while (true) {
            System.out.println("\n-------------------------");
            System.out.println("Aktueller Spieler: " + (currentPlayer == IsolaBoard.PLAYER1 ? "Mensch (P1)" : "Computer (P2)"));

            IsolaMove currentMove = null;

            if (currentPlayer == COMPUTER_PLAYER_ID) {
                System.out.println("Computer berechnet seinen Zug...");
                currentMove = computerPlayer.findBestMove(board, currentPlayer);

                if (currentMove == null) {
                    System.out.println("Computer kann keinen Zug mehr machen! Mensch gewinnt!");
                    break;
                }

                board.movePlayer(currentPlayer, currentMove.moveToRow, currentMove.moveToCol);
                board.removeTile(currentMove.removeTileRow, currentMove.removeTileCol);

                System.out.println("Computer zieht: " + currentMove);
                board.printBoard();

            } else {
                boolean moveSuccessful = false;
                while (!moveSuccessful) {
                    System.out.println("Figur bewegen (aktuelle Position: P1 bei (" + board.getPlayer1Position()[0] + "," + board.getPlayer1Position()[1] + "))");
                    System.out.print("Gib die neue Zeile ein: ");
                    int newRow = getUserInput();
                    System.out.print("Gib die neue Spalte ein: ");
                    int newCol = getUserInput();

                    moveSuccessful = board.movePlayer(currentPlayer, newRow, newCol);
                    if (!moveSuccessful) {
                        System.out.println("Ungültiger Zug, bitte erneut versuchen.");
                    }
                }
                board.printBoard();

                boolean removeSuccessful = false;
                while (!removeSuccessful) {
                    System.out.println("Spielstein entfernen:");
                    System.out.print("Gib die Zeile des zu entfernenden Steins ein: ");
                    int removeRow = getUserInput();
                    System.out.print("Gib die Spalte des zu entfernenden Steins ein: ");
                    int removeCol = getUserInput();

                    removeSuccessful = board.removeTile(removeRow, removeCol);
                    if (!removeSuccessful) {
                        System.out.println("Ungültige Auswahl, bitte erneut versuchen.");
                    }
                }
                board.printBoard();
            }

            int otherPlayer = (currentPlayer == IsolaBoard.PLAYER1) ? IsolaBoard.PLAYER2 : IsolaBoard.PLAYER1;
            if (board.isPlayerIsolated(otherPlayer)) {
                System.out.println("\n-------------------------");
                System.out.println("Spieler " + (otherPlayer == IsolaBoard.PLAYER1 ? "1" : "2") + " ist isoliert!");
                System.out.println("Spieler " + (currentPlayer == IsolaBoard.PLAYER1 ? "1 (Mensch)" : "2 (Computer)") + " GEWINNT!");
                break;
            }

            switchPlayer();
        }
        scanner.close();
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == IsolaBoard.PLAYER1) ? IsolaBoard.PLAYER2 : IsolaBoard.PLAYER1;
    }

    private int getUserInput() {
        while (true) {
            try {
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Ungültige Eingabe. Bitte gib eine ganze Zahl ein.");
                scanner.next();
                System.out.print("Erneut versuchen: ");
            }
        }
    }

    public static void main(String[] args) {
        IsolaGame game = new IsolaGame();
        game.startGame();
    }
}