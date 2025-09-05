# Isola Game GUI Integration Plan

## Overview

This document describes how to integrate the new GUI architecture with the existing Isola game implementation, ensuring that the application can support both the current console-based interface and the new Swing GUI.

## Entry Point Modifications

### Current Main.java

The current `Main.java` file serves as the entry point for the console-based game:

```java
package de.greenoid.game.isola;

public class Main {
    public static void main(String[] args) {
        IsolaGame game = new IsolaGame();
        game.startGame();
    }
}
```

### Proposed Main.java

To support both console and GUI modes, we'll modify the `Main` class to accept command-line arguments:

```java
package de.greenoid.game.isola;

import de.greenoid.game.isola.gui.swing.SwingGui;
import de.greenoid.game.isola.gui.common.GuiController;

public class Main {
    public static void main(String[] args) {
        // Check if GUI mode is requested
        boolean guiMode = false;
        for (String arg : args) {
            if ("-gui".equals(arg)) {
                guiMode = true;
                break;
            }
        }
        
        if (guiMode) {
            // Initialize and start the Swing GUI
            IsolaGame game = new IsolaGame();
            GuiController controller = new GuiController(game);
            SwingGui swingGui = new SwingGui(controller);
            swingGui.showGameWindow();
        } else {
            // Run the console version
            IsolaGame game = new IsolaGame();
            game.startGame();
        }
    }
}
```

## Package Structure

The new GUI implementation will follow this package structure:

```
src/
└── de/
    └── greenoid/
        └── game/
            └── isola/
                ├── board/
                │   └── IsolaBoard.java
                ├── game/
                │   ├── IsolaGame.java
                │   └── Main.java
                ├── gui/
                │   ├── adapter/
                │   │   ├── GuiAdapter.java
                │   │   └── GuiListener.java
                │   ├── common/
                │   │   ├── GuiController.java
                │   │   └── GameEvent.java
                │   └── swing/
                │       ├── SwingGui.java
                │       ├── GameWindow.java
                │       ├── BoardPanel.java
                │       └── ControlPanel.java
                ├── player/
                │   └── ComputerPlayer.java
                └── state/
                    ├── IsolaGameState.java
                    ├── BoardState.java
                    ├── GamePhase.java
                    └── GameStatus.java
```

## Integration Points

### 1. GuiController Integration

The `GuiController` will act as an intermediary between the GUI and the core game logic:

```java
package de.greenoid.game.isola.gui.common;

import de.greenoid.game.isola.IsolaGame;
import de.greenoid.game.isola.IsolaGameState;

public class GuiController {
    private IsolaGame game;
    
    public GuiController(IsolaGame game) {
        this.game = game;
    }
    
    public IsolaGameState getGameState() {
        return game.getGameState();
    }
    
    public boolean movePlayer(int player, int newRow, int newCol) {
        // Implementation will depend on current game phase
        // This will require modifications to IsolaGame to expose move operations
    }
    
    public boolean removeTile(int row, int col) {
        // Implementation will depend on current game phase
        // This will require modifications to IsolaGame to expose tile removal operations
    }
    
    public void startNewGame() {
        // Create a new IsolaGame instance
    }
}
```

### 2. Required Modifications to IsolaGame

To support GUI interaction, we'll need to expose some internal methods of `IsolaGame`:

```java
// In IsolaGame.java, we'll add public methods for GUI interaction:
public boolean movePlayer(int player, int newRow, int newCol) {
    return board.movePlayer(player, newRow, newCol);
}

public boolean removeTile(int row, int col) {
    return board.removeTile(row, col);
}

public void switchToNextPlayer() {
    switchPlayer();
}

public boolean isPlayerIsolated(int player) {
    return board.isPlayerIsolated(player);
}
```

## Communication Flow

### 1. State Updates
1. GUI calls `controller.getGameState()` periodically or when needed
2. `GuiController` retrieves state from `IsolaGame`
3. `IsolaGame.getGameState()` returns current state
4. GUI updates display based on state

### 2. User Actions
1. User interacts with GUI (clicks on board, buttons, etc.)
2. GUI event handlers call appropriate methods on `SwingGui`
3. `SwingGui` calls corresponding methods on `GuiController`
4. `GuiController` calls methods on `IsolaGame` to update game state
5. GUI requests updated state and refreshes display

## Backward Compatibility

The integration maintains full backward compatibility:

1. **Console Mode**: Running without `-gui` argument uses the existing console interface
2. **Existing API**: All existing public methods remain unchanged
3. **No Breaking Changes**: Existing code that depends on the current implementation continues to work

## Deployment

### Running Console Version
```bash
java -jar IsolaGame.jar
```

### Running GUI Version
```bash
java -jar IsolaGame.jar -gui
```

## Testing

### Unit Testing
- Test `GuiController` independently from GUI frameworks
- Test state conversion and event handling logic

### Integration Testing
- Test complete flow from GUI interaction to game state update
- Test both console and GUI modes

### GUI Testing
- Test Swing components rendering and event handling
- Test user interaction scenarios

## Future Enhancements

### 1. Configuration Options
- Add more command-line options for GUI customization
- Support for loading/saving game states

### 2. Multiple GUI Frameworks
- Implement web-based GUI using the same adapter pattern
- Add mobile GUI for Android/iOS

### 3. Enhanced Features
- Add game statistics and history tracking
- Implement different difficulty levels for computer player
- Add multiplayer support over network

## Conclusion

This integration plan allows for a smooth transition to the new GUI architecture while maintaining full compatibility with the existing console-based implementation. The modular design ensures that future enhancements can be added without disrupting existing functionality.