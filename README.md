JavaFX Board Game: Strategic Two-Player Challenge
This JavaFX-based board game lets you face off against a computer opponent on a customizable grid. Players take turns moving their pieces, aiming to trap their opponent or block their moves. Built with JDK 21 and JavaFX 21.0.7, the game features a sleek graphical interface, robust AI, and in-game feedback via a MessageArea. Designed for modularity, it’s easy to extend with new features.

![image](https://github.com/user-attachments/assets/b5615f54-123b-4469-8bef-0f149a3fddb7)

Features

Customizable Board: Define board size and place black square obstacles interactively.
Two-Player Gameplay: Human (Player B) vs. Computer (Player A) with strategic moves.
Smart AI: Uses a minimax algorithm for challenging computer moves.
Graphical UI: JavaFX interface with a grid display, interactive controls, and animated feedback.
In-Game Feedback: MessageArea shows errors (red), game end (blue), and status (black) without pop-ups.
Error Handling: Clear messages with coordinates (e.g., "Cannot move down 2: out of bounds at (4,3)").
Modular Design: Separates UI (GameScreen, MessageArea), logic (GameLogic), and state (BoardState).

Getting Started
Prerequisites

Java Development Kit (JDK): 21 or later.
Maven: 3.9.6 or later.
IDE: IntelliJ IDEA, Eclipse, or similar (recommended for Maven projects).
A terminal for running Maven commands.

Installation

Clone the Repository:
git clone https://github.com/kitsiosvas/AI_MiniMax_Game.git
cd javafx-board-game


Build the Project: 
mvn clean install


Run the Game:
mvn javafx:run

How to Play

Start the Game:

Launch the app to see the Welcome screen.
Click “Start” to begin a new game.


Setup Phase:

Board Size: Enter rows and columns (e.g., 4x4) in the ControlPanel. Errors (e.g., “Rows and columns must be positive.”) appear in red in MessageArea.
Black Squares: Click grid cells to mark black squares (gray), then click “Confirm”.
Player Positions: Click to set Player A (computer, red) and Player B (you, blue), then “Confirm”. Errors (e.g., “Cannot place player on black square.”) show in red.


Gameplay:

Your Turn: In ControlPanel, select a direction (e.g., “up_right”) and length (1-2) via a slider, then click “Move”. Invalid moves show errors in red.
Computer’s Turn: See “Calculating AI move...” with a spinning ProgressIndicator in MessageArea, then the board updates.
The board displays Player A (red), Player B (blue), black squares (gray), and empty cells (white).
Game ends when a player cannot move, showing a message like “Cannot move down 2: out of bounds at (4,3). Game ended.” in blue.


Play Again:

At game end, MessageArea shows “Game over. Click board to play again, or press Esc for main menu.”
Click the board to restart or press Esc to return to the Welcome screen.


Future Plans

Add ControlPanel buttons for “Play Again” and “Main Menu” to replace click/Esc.
Implement a message history pane to review past MessageArea feedback.
Refactor JavaFXGameIO into smaller classes (e.g., ControlPanelManager) for better modularity.
Add undo move functionality and AI difficulty settings.
Support human vs. human multiplayer mode.

