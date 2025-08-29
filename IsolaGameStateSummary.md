# Isola Game State Summary

## Objective
Design a data structure that encapsulates the state of the Isola game for UI consumption, allowing Swing or other GUI frameworks to easily retrieve and display game information.

## Key Requirements
1. Identify which player has to move
2. Determine if the player needs to move their position or remove a tile
3. Check if the game has ended and who won
4. Provide all necessary information for UI display

## Designed Solution

### Data Structure: IsolaGameState
A class that encapsulates all necessary game state information:

- `currentPlayer`: Which player needs to make a move (PLAYER1 or PLAYER2)
- `gamePhase`: Current phase (MOVE_PLAYER or REMOVE_TILE)
- `boardState`: Current board state with player positions
- `gameStatus`: Game status (ONGOING, PLAYER1_WON, PLAYER2_WON)

### Supporting Enums
- `GamePhase`: MOVE_PLAYER, REMOVE_TILE
- `GameStatus`: ONGOING, PLAYER1_WON, PLAYER2_WON

### Public Method
- `getGameState()`: Returns an IsolaGameState object representing the current state

## Implementation Plan

1. Create GamePhase and GameStatus enums
2. Create IsolaGameState class
3. Modify IsolaGame to track game phase and status
4. Add getGameState() method to IsolaGame
5. Update game logic to maintain phase tracking

## UI Integration Example

```java
IsolaGameState state = isolaGame.getGameState();

// Check if game is still ongoing
if (state.getGameStatus() == GameStatus.ONGOING) {
    // Display current player
    int currentPlayer = state.getCurrentPlayer();
    
    // Display appropriate UI based on game phase
    switch (state.getGamePhase()) {
        case MOVE_PLAYER:
            // Enable player movement controls
            break;
        case REMOVE_TILE:
            // Enable tile removal controls
            break;
    }
    
    // Display board state
    BoardState boardState = state.getBoardState();
}
```

## Benefits

1. **Complete Information**: Provides all necessary data for UI implementation
2. **Clean Interface**: Single method call to get complete game state
3. **Extensible**: Easy to add new state information if needed
4. **Framework Agnostic**: Works with any UI framework