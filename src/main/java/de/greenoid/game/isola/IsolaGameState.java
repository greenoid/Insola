package de.greenoid.game.isola;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class representing the current state of an Isola game for UI consumption.
 * This class encapsulates all necessary information for a UI to display
 * the current game state and determine what actions are available.
 */
public class IsolaGameState {
    private static final Logger log = LogManager.getLogger(IsolaGameState.class);
    // Current player who needs to make a move
    private int currentPlayer;
    
    // Game phase: MOVE_PLAYER or REMOVE_TILE
    private GamePhase gamePhase;
    
    // Current board state
    private BoardState boardState;
    
    // Game status: ONGOING, PLAYER1_WON, PLAYER2_WON
    private GameStatus gameStatus;
    
    /**
     * Constructor to create an IsolaGameState object.
     * 
     * @param currentPlayer The player who needs to make a move
     * @param gamePhase The current phase of the game (MOVE_PLAYER or REMOVE_TILE)
     * @param boardState The current board state
     * @param gameStatus The current status of the game (ONGOING, PLAYER1_WON, PLAYER2_WON)
     */
    public IsolaGameState(int currentPlayer, GamePhase gamePhase, BoardState boardState, GameStatus gameStatus) {
        this.currentPlayer = currentPlayer;
        this.gamePhase = gamePhase;
        this.boardState = boardState;
        this.gameStatus = gameStatus;
    }
    
    /**
     * Get the current player who needs to make a move.
     * 
     * @return The current player (IsolaBoard.PLAYER1 or IsolaBoard.PLAYER2)
     */
    public int getCurrentPlayer() {
        return currentPlayer;
    }
    
    /**
     * Get the current game phase.
     * 
     * @return The current game phase (MOVE_PLAYER or REMOVE_TILE)
     */
    public GamePhase getGamePhase() {
        return gamePhase;
    }
    
    /**
     * Get the current board state.
     * 
     * @return BoardState object representing the current board state
     */
    public BoardState getBoardState() {
        return boardState;
    }
    
    /**
     * Get the current game status.
     * 
     * @return The current game status (ONGOING, PLAYER1_WON, PLAYER2_WON)
     */
    public GameStatus getGameStatus() {
        return gameStatus;
    }
}