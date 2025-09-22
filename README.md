# Isola Game

Isola is a strategic board game for two players. The objective is to isolate your opponent so they cannot make any legal moves.

## Features

- Complete implementation of the Insola board game
- Computer player with AI opponent
- Console-based interface
- Graphical Swing-based interface
- Clean, extensible architecture

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher

## Building the Project

To build the project, run:

```bash
mvn clean package
```

This will create a fat JAR file in the `target` directory that includes all dependencies.

## Running the Game

### Console Version

To run the console version of the game:

```bash
java -jar target/Insola-1.0.0.jar
```

### GUI Version

To run the graphical Swing version of the game:

```bash
java -jar target/Insola-1.0.0.jar -gui
```

## How to Play

### Game Setup

- Player 1 (P1) starts in position (5,3) - bottom center
- Player 2 (P2) starts in position (0,4) - top center
- All other positions contain removable tiles

### Game Rules

1. Players take turns moving their piece and removing a tile
2. On each turn, a player must:
   - Move their piece to an adjacent square (including diagonally)
   - Remove one tile from the board (cannot be a starting position or occupied square)
3. A player wins when their opponent cannot make a legal move

### Making Moves in the GUI

1. **Move Phase**: Click on your piece, then click on an adjacent square to move to
2. **Remove Tile Phase**: Click on any tile to remove it from the board
3. The game automatically switches between players after each complete turn

## Project Structure

```
src/
├── de/greenoid/game/isola/
│   ├── board/              # Board-related classes
│   ├── game/               # Main game logic
│   ├── gui/                # GUI implementation
│   │   ├── adapter/        # GUI adapter pattern
│   │   ├── common/         # Common GUI components
│   │   ├── swing/          # Swing implementation
│   │   └── web/            # Web GUI plan
│   └── player/             # Player classes
└── log4j2.xml              # Logging configuration
```

## Dependencies

- Log4j 2.20.0 - Logging framework
- FlatLaf 3.2.5 - Modern Look and Feel for Java Swing
- MigLayout 11.0 - Layout Manager for Java Swing

## License

This project is licensed under the MIT License - see the LICENSE file for details.
