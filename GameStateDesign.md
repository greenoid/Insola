# Isola Game State Design for UI Integration

## Overview
This document describes the design of a data structure that encapsulates the state of an Isola game for UI consumption. The goal is to provide a clean interface that allows UI components (Swing, web-based, etc.) to easily retrieve and display the current game state.

## Current Game Flow Analysis
Based on the analysis of `IsolaGame.java`, the game follows this flow:
1. Display current player
2. If human player:
   - Move player to a new position
   - Remove a tile from the board
3. If computer player:
   - Computer calculates and executes both move and tile removal
4. Check if opponent is isolated (game end condition)
5. Switch to next player

## Required Game State Information
The UI needs to know:
- Which player has to move next
- Whether the player needs to move their position or remove a tile
- Current board state with player positions
- Whether the game has ended
- Who won if the game is finished

## Proposed Data Structure: `IsolaGameState`

### Implementation Details

```java
public class IsolaGameState {
    // Current player who needs to make a move
    private int currentPlayer;
    
    // Game phase: MOVE_PLAYER or REMOVE_TILE
    private GamePhase gamePhase;
    
    // Current board state
    private BoardState boardState;
    
    // Game status: ONGOING, PLAYER1_WON, PLAYER2_WON
    private GameStatus gameStatus;
    
    // Constructor
    public IsolaGameState(int currentPlayer, GamePhase gamePhase, BoardState boardState, GameStatus gameStatus) {
        this.currentPlayer = currentPlayer;
        this.gamePhase = gamePhase;
        this.boardState = boardState;
        this.gameStatus = gameStatus;
    }
    
    // Getter methods
    public int getCurrentPlayer() {
        return currentPlayer;
    }
    
    public GamePhase getGamePhase() {
        return gamePhase;
    }
    
    public BoardState getBoardState() {
        return boardState;
    }
    
    public GameStatus getGameStatus() {
        return gameStatus;
    }
}
```

### Supporting Enums

```java
public enum GamePhase {
    MOVE_PLAYER,    // Player needs to move their position
    REMOVE_TILE     // Player needs to remove a tile
}

public enum GameStatus {
    ONGOING,        // Game is still in progress
    PLAYER1_WON,    // Player 1 has won
    PLAYER2_WON,    // Player 2 has won
    DRAW            // Game ended in a draw (unlikely in Isola)
}
```

## Modifications to IsolaGame Class

To support the new game state functionality, the `IsolaGame` class needs to be modified to track the current game phase and provide a method to retrieve the game state.

### Add these fields to IsolaGame:

```java
// Track the current game phase
private GamePhase currentGamePhase;

// Track game status
private GameStatus gameStatus;
```

### Add this method to IsolaGame:

```java
/**
 * Returns the current state of the game for UI consumption
 * @return IsolaGameState object representing the current game state
 */
public IsolaGameState getGameState() {
    // Create a BoardState object from the current board
    BoardState currentBoardState = new BoardState(
        board.BOARD_ROWS,
        board.BOARD_COLS,
        board.getBoard(),
        board.getPlayer1Row(),
        board.getPlayer1Col(),
        board.getPlayer2Row(),
        board.getPlayer2Col()
    );
    
    return new IsolaGameState(currentPlayer, currentGamePhase, currentBoardState, gameStatus);
}
```

### Update game logic to track phase

In the human player section of `startGame()`:
```java
// After successful move but before tile removal
currentGamePhase = GamePhase.REMOVE_TILE;

// After successful tile removal
currentGamePhase = GamePhase.MOVE_PLAYER;
```

### Update game end condition detection

In the game loop where isolation is checked:
```java
if (board.isPlayerIsolated(otherPlayer)) {
    // Set game status based on who won
    gameStatus = (currentPlayer == IsolaBoard.PLAYER1) ? GameStatus.PLAYER1_WON : GameStatus.PLAYER2_WON;
    // ... rest of existing code
}
```

## Integration with Existing Code

The existing `BoardState` class already provides much of the board information needed. We'll extend this to include the additional game state information.

## Usage Example for UI

```java
// In UI code
IsolaGameState state = isolaGame.getGameState();

switch (state.getGameStatus()) {
    case ONGOING:
        // Display normal game state
        displayBoard(state.getBoardState());
        displayCurrentPlayer(state.getCurrentPlayer());
        displayGamePhase(state.getGamePhase());
        break;
    case PLAYER1_WON:
        // Display Player 1 victory
        displayVictoryMessage("Player 1 wins!");
        break;
    case PLAYER2_WON:
        // Display Player 2 victory
        displayVictoryMessage("Player 2 wins!");
        break;
}
```

## Benefits of This Design

1. **Encapsulation**: All game state information is contained in a single object
2. **UI Agnostic**: Can be used by Swing, web, or any other UI framework
3. **Extensible**: Easy to add new state information if needed
4. **Clear Separation**: Separates game logic from UI concerns
5. **Immutable State**: The game state object can be made immutable for safety

## Implementation Steps for Code Mode

When implementing this design in code mode, follow these steps:

1. Create the `GamePhase` and `GameStatus` enums
2. Create the `IsolaGameState` class
3. Modify the `IsolaGame` class to:
   - Add fields for tracking game phase and status
   - Add the `getGameState()` method
   - Update the game logic to track phase changes
   - Update game end condition detection

## Next Steps

1. Switch to Code mode to implement the Java classes
2. Test with a simple UI implementation
3. Verify that all game state information is correctly exposed