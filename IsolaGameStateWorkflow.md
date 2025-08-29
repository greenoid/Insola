# Isola Game State Workflow

## Mermaid Diagram

```mermaid
graph TD
    A[Game Start] --> B[Initialize Board]
    B --> C[Set Current Player = PLAYER1]
    C --> D[Set Game Phase = MOVE_PLAYER]
    D --> E[Set Game Status = ONGOING]
    E --> F[Game Loop]
    
    F --> G[Get Game State for UI]
    G --> H{Is Game Ongoing?}
    H -->|Yes| I{Current Player Type}
    H -->|No| J[Display Winner]
    
    I -->|Human| K[Player Moves Position]
    K --> L{Move Valid?}
    L -->|No| K
    L -->|Yes| M[Update Board]
    M --> N[Set Game Phase = REMOVE_TILE]
    N --> O[Player Removes Tile]
    O --> P{Tile Removal Valid?}
    P -->|No| O
    P -->|Yes| Q[Update Board]
    Q --> R[Set Game Phase = MOVE_PLAYER]
    R --> S[Check Opponent Isolation]
    S --> T{Is Opponent Isolated?}
    T -->|Yes| U[Set Game Status = Current Player Wins]
    T -->|No| V[Switch Player]
    V --> F
    
    I -->|Computer| W[Computer Calculates Move]
    W --> X[Execute Move and Tile Removal]
    X --> S
    
    U --> F
    J --> Z[End Game]
```

## State Transition Explanation

1. **Game Initialization**:
   - Board is set up with players in starting positions
   - Current player is set to PLAYER1
   - Game phase is set to MOVE_PLAYER
   - Game status is set to ONGOING

2. **Human Player Turn**:
   - UI gets game state and sees it's human player's turn
   - UI enables player movement controls
   - Player moves their piece to a new position
   - Game phase transitions to REMOVE_TILE
   - UI enables tile removal controls
   - Player removes a tile
   - Game phase transitions back to MOVE_PLAYER
   - Check if opponent is isolated (game end condition)
   - If game not ended, switch to next player

3. **Computer Player Turn**:
   - UI gets game state and sees it's computer player's turn
   - Computer calculates and executes both move and tile removal
   - Check if opponent is isolated (game end condition)
   - If game not ended, switch to next player

4. **Game End**:
   - When a player is isolated, game status is updated to reflect winner
   - UI gets game state and displays victory message