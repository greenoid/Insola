# Isola Game GUI Design

## Overview

This document describes the detailed design for the Isola Game GUI, including the abstraction layer and Swing implementation.

## Package Structure

```
src/
└── de/
    └── greenoid/
        └── game/
            └── isola/
                ├── gui/
                │   ├── adapter/
                │   │   ├── GuiAdapter.java
                │   │   └── GuiListener.java
                │   ├── swing/
                │   │   ├── SwingGui.java
                │   │   ├── BoardPanel.java
                │   │   ├── ControlPanel.java
                │   │   └── GameWindow.java
                │   └── common/
                │       ├── GuiController.java
                │       └── GameEvent.java
                ├── board/
                ├── player/
                └── game/
```

## Core Components

### 1. GuiController (src/de/greenoid/game/isola/gui/common/GuiController.java)

This class acts as the intermediary between the core game logic and the GUI.

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
        // Implementation to move player
    }
    
    public boolean removeTile(int row, int col) {
        // Implementation to remove tile
    }
    
    public void startNewGame() {
        // Implementation to start a new game
    }
    
    public void togglePlayerMode() {
        // Implementation to toggle between human and computer player
    }
}
```

### 2. GuiAdapter (src/de/greenoid/game/isola/gui/adapter/GuiAdapter.java)

This abstract class provides a common interface for different GUI frameworks.

```java
package de.greenoid.game.isola.gui.adapter;

import de.greenoid.game.isola.gui.common.GuiController;
import de.greenoid.game.isola.IsolaGameState;

public abstract class GuiAdapter {
    protected GuiController controller;
    
    public GuiAdapter(GuiController controller) {
        this.controller = controller;
    }
    
    public abstract void updateGameState(IsolaGameState state);
    public abstract void showGameWindow();
    public abstract void closeGameWindow();
    public abstract void showMessage(String message);
}
```

### 3. GuiListener (src/de/greenoid/game/isola/gui/adapter/GuiListener.java)

Interface for handling GUI events.

```java
package de.greenoid.game.isola.gui.adapter;

public interface GuiListener {
    void onPlayerMove(int player, int fromRow, int fromCol, int toRow, int toCol);
    void onTileRemove(int row, int col);
    void onNewGame();
    void onExit();
}
```

## Swing Implementation

### 1. SwingGui (src/de/greenoid/game/isola/gui/swing/SwingGui.java)

Main Swing GUI implementation.

```java
package de.greenoid.game.isola.gui.swing;

import de.greenoid.game.isola.gui.adapter.GuiAdapter;
import de.greenoid.game.isola.gui.common.GuiController;
import de.greenoid.game.isola.IsolaGameState;

public class SwingGui extends GuiAdapter {
    private GameWindow gameWindow;
    private BoardPanel boardPanel;
    private ControlPanel controlPanel;
    
    public SwingGui(GuiController controller) {
        super(controller);
        initializeComponents();
    }
    
    private void initializeComponents() {
        boardPanel = new BoardPanel(this);
        controlPanel = new ControlPanel(this);
        gameWindow = new GameWindow(boardPanel, controlPanel);
    }
    
    @Override
    public void updateGameState(IsolaGameState state) {
        boardPanel.updateBoard(state.getBoardState());
        controlPanel.updateStatus(state);
    }
    
    @Override
    public void showGameWindow() {
        gameWindow.setVisible(true);
    }
    
    @Override
    public void closeGameWindow() {
        gameWindow.dispose();
    }
    
    @Override
    public void showMessage(String message) {
        // Show message dialog
    }
    
    // Event handlers
    public void handlePlayerMove(int player, int fromRow, int fromCol, int toRow, int toCol) {
        // Handle player move event
    }
    
    public void handleTileRemove(int row, int col) {
        // Handle tile removal event
    }
    
    public void handleNewGame() {
        // Handle new game event
    }
    
    public void handleExit() {
        // Handle exit event
    }
}
```

### 2. GameWindow (src/de/greenoid/game/isola/gui/swing/GameWindow.java)

Main game window.

```java
package de.greenoid.game.isola.gui.swing;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {
    public GameWindow(BoardPanel boardPanel, ControlPanel controlPanel) {
        setTitle("Isola Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }
}
```

### 3. BoardPanel (src/de/greenoid/game/isola/gui/swing/BoardPanel.java)

Panel for displaying the game board.

```java
package de.greenoid.game.isola.gui.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BoardPanel extends JPanel {
    private static final int BOARD_ROWS = 6;
    private static final int BOARD_COLS = 8;
    private static final int CELL_SIZE = 60;
    
    private int[][] boardState;
    private SwingGui gui;
    
    public BoardPanel(SwingGui gui) {
        this.gui = gui;
        this.boardState = new int[BOARD_ROWS][BOARD_COLS];
        initializeBoard();
    }
    
    private void initializeBoard() {
        setPreferredSize(new Dimension(BOARD_COLS * CELL_SIZE, BOARD_ROWS * CELL_SIZE));
        addMouseListener(new BoardMouseListener());
    }
    
    public void updateBoard(int[][] newBoardState) {
        this.boardState = newBoardState;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
    }
    
    private void drawBoard(Graphics g) {
        // Draw the game board with current state
        for (int row = 0; row < BOARD_ROWS; row++) {
            for (int col = 0; col < BOARD_COLS; col++) {
                drawCell(g, row, col, boardState[row][col]);
            }
        }
    }
    
    private void drawCell(Graphics g, int row, int col, int cellState) {
        int x = col * CELL_SIZE;
        int y = row * CELL_SIZE;
        
        // Draw cell background
        switch (cellState) {
            case 0: // EMPTY
                g.setColor(Color.LIGHT_GRAY);
                break;
            case 1: // TILE
                g.setColor(Color.WHITE);
                break;
            // Add other cases for player positions, etc.
        }
        
        g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
        
        // Draw player positions
        // ... (implementation details)
    }
    
    private class BoardMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            int col = e.getX() / CELL_SIZE;
            int row = e.getY() / CELL_SIZE;
            
            // Notify the GUI about the click
            // gui.handleBoardClick(row, col);
        }
    }
}
```

### 4. ControlPanel (src/de/greenoid/game/isola/gui/swing/ControlPanel.java)

Panel for game controls and status display.

```java
package de.greenoid.game.isola.gui.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControlPanel extends JPanel {
    private JLabel statusLabel;
    private JButton newGameButton;
    private JButton exitButton;
    
    public ControlPanel(SwingGui gui) {
        initializeComponents(gui);
    }
    
    private void initializeComponents(SwingGui gui) {
        setLayout(new FlowLayout());
        
        statusLabel = new JLabel("Game Status: ONGOING");
        add(statusLabel);
        
        newGameButton = new JButton("New Game");
        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.handleNewGame();
            }
        });
        add(newGameButton);
        
        exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.handleExit();
            }
        });
        add(exitButton);
    }
    
    public void updateStatus(IsolaGameState state) {
        statusLabel.setText("Current Player: " + state.getCurrentPlayer() + 
                           " | Phase: " + state.getGamePhase() + 
                           " | Status: " + state.getGameStatus());
    }
}
```

## Integration with Core Game Logic

The GUI will interact with the core game logic through the following flow:

1. **Initialization**: 
   - Create an `IsolaGame` instance
   - Create a `GuiController` with the game instance
   - Create a `SwingGui` with the controller

2. **Game State Updates**:
   - The GUI periodically calls `controller.getGameState()` to get the current state
   - The `SwingGui.updateGameState()` method updates the UI components

3. **User Interactions**:
   - User clicks on the board or controls
   - Swing event handlers call appropriate methods on the `SwingGui` instance
   - These methods call corresponding methods on the `GuiController`
   - The controller updates the game state through the core game logic
   - The GUI updates its display with the new game state

## Cross-Framework Compatibility

To ensure compatibility with other GUI frameworks:

1. **Abstract Common Functionality**: The `GuiAdapter` abstract class defines the common interface
2. **Separate Implementation**: Each framework has its own implementation (e.g., `SwingGui`, `WebGui`)
3. **Event Handling**: Use generic event objects that can be translated to framework-specific events
4. **State Representation**: Use the existing `IsolaGameState` and `BoardState` classes for consistent state representation

## Future Web GUI Implementation

For a web-based implementation, we would create:

1. **WebGui** class extending `GuiAdapter`
2. **HTML/CSS templates** for the game board and controls
3. **JavaScript event handlers** for user interactions
4. **WebSocket or REST API** for communication between frontend and backend (if needed)

The core game logic remains unchanged, and the `GuiController` provides the same interface for both Swing and web implementations.