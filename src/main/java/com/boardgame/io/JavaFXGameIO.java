package com.boardgame.io;

import com.boardgame.ui.BoardUI;
import com.boardgame.ui.MessageArea;
import com.boardgame.logic.BoardState;
import com.boardgame.logic.Direction;
import com.boardgame.logic.GameLogic;
import com.boardgame.logic.MoveResult;
import com.boardgame.logic.Pair;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class JavaFXGameIO implements GameIO {
    private final Stage primaryStage;
    private final GridPane boardGrid;
    private final MessageArea messageArea;
    private final VBox controlPanel;
    private final Runnable switchToWelcomeScene;
    private int rows;
    private int columns;
    private List<int[]> blackSquares;
    private BoardUI boardUI;

    public JavaFXGameIO(Stage stage, GridPane boardGrid, MessageArea messageArea, VBox controlPanel, Runnable switchToWelcomeScene) {
        this.primaryStage = stage;
        this.boardGrid = boardGrid;
        this.messageArea = messageArea;
        this.controlPanel = controlPanel;
        this.switchToWelcomeScene = switchToWelcomeScene;
        this.blackSquares = new ArrayList<>();
    }

    public void reset() {
        rows = 0;
        columns = 0;
        blackSquares.clear();
        Platform.runLater(() -> {
            controlPanel.getChildren().clear();
            messageArea.updateMessage("== Java program started ==", MessageArea.MessageType.NEUTRAL);
            if (boardUI != null) {
                boardUI.cancelPrompts(); // Changed to cancelPrompts to ensure futures are completed
            }
        });
    }

    @Override
    public int[] promptBoardSize() {
        CompletableFuture<int[]> future = new CompletableFuture<>();
        Platform.runLater(() -> {
            controlPanel.getChildren().clear();
            TabPane tabPane = new TabPane();
            tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
            Tab sizeTab = new Tab("Board Size");
            VBox sizeContent = new VBox(10);
            sizeContent.setPadding(new Insets(10));
            TextField rowsField = new TextField();
            rowsField.setPromptText("Rows");
            TextField colsField = new TextField();
            colsField.setPromptText("Columns");
            Button submitButton = new Button("Set Size");
            submitButton.setId("action-button");
            Label noteLabel = new Label("Note: Boards > 4x4 may slow AI.");
            noteLabel.setId("note-label");
            sizeContent.getChildren().addAll(
                new Label("Enter board size:"), rowsField, colsField, noteLabel, submitButton
            );
            sizeTab.setContent(sizeContent);
            tabPane.getTabs().add(sizeTab);
            controlPanel.getChildren().add(tabPane);

            submitButton.setOnAction(e -> {
                try {
                    int rows = Integer.parseInt(rowsField.getText());
                    int cols = Integer.parseInt(colsField.getText());
                    if (rows <= 0 || cols <= 0) {
                        messageArea.updateMessage("Rows and columns must be positive.", MessageArea.MessageType.ERROR);
                    } else {
                        this.rows = rows;
                        this.columns = cols;
                        this.boardUI = new BoardUI(primaryStage, boardGrid, messageArea, rows, cols);
                        System.out.println("BoardUI initialized with size " + rows + "x" + cols);
                        future.complete(new int[]{rows, cols});
                    }
                } catch (NumberFormatException ex) {
                    messageArea.updateMessage("Please enter valid integers.", MessageArea.MessageType.ERROR);
                }
            });
        });
        return future.join();
    }

    @Override
    public List<int[]> promptBlackSquares(int rows, int columns) {
        if (boardUI == null) {
            System.err.println("Error: BoardUI not initialized");
            messageArea.updateMessage("Error: Board not initialized. Please set board size first.", MessageArea.MessageType.ERROR);
            return new ArrayList<>();
        }
        CompletableFuture<List<int[]>> future = boardUI.promptBlackSquares();
        Platform.runLater(() -> {
            controlPanel.getChildren().clear();
            TabPane tabPane = new TabPane();
            tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
            Tab blackTab = new Tab("Black Squares");
            VBox blackContent = new VBox(10);
            blackContent.setPadding(new Insets(10));
            Button confirmButton = new Button("Confirm");
            confirmButton.setId("action-button");
            blackContent.getChildren().addAll(
                new Label("Click cells to set black squares (*), then confirm:"), confirmButton
            );
            blackTab.setContent(blackContent);
            tabPane.getTabs().add(blackTab);
            controlPanel.getChildren().add(tabPane);

            confirmButton.setOnAction(e -> {
                System.out.println("Confirm button clicked for black squares");
                boardUI.completeBlackSquaresPrompt();
            });
        });
        return future.join();
    }

    @Override
    public int[][] promptPlayerPositions(int rows, int columns) {
        if (boardUI == null) {
            System.err.println("Error: BoardUI not initialized");
            messageArea.updateMessage("Error: Board not initialized. Please set board size first.", MessageArea.MessageType.ERROR);
            return new int[][]{{-1, -1}, {-1, -1}};
        }
        CompletableFuture<int[][]> future = boardUI.promptPlayerPositions();
        Platform.runLater(() -> {
            controlPanel.getChildren().clear();
            TabPane tabPane = new TabPane();
            tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
            Tab posTab = new Tab("Player Positions");
            VBox posContent = new VBox(10);
            posContent.setPadding(new Insets(10));
            Button confirmButton = new Button("Confirm");
            confirmButton.setId("action-button");
            Label instruction = new Label("Click to set Player A (red), then Player B (blue):");
            posContent.getChildren().addAll(instruction, confirmButton);
            posTab.setContent(posContent);
            tabPane.getTabs().add(posTab);
            controlPanel.getChildren().add(tabPane);

            confirmButton.setOnAction(e -> {
                System.out.println("Confirm button clicked for player positions");
                int[][] positions = boardUI.promptPlayerPositions().join();
                if (positions[0][0] == -1 || positions[1][0] == -1) {
                    messageArea.updateMessage("Please set positions for both players.", MessageArea.MessageType.ERROR);
                } else {
                    future.complete(positions);
                }
            });
        });
        return future.join();
    }

    @Override
    public Pair<Direction, Integer> promptPlayerMove() {
        CompletableFuture<Pair<Direction, Integer>> future = new CompletableFuture<>();
        Platform.runLater(() -> {
            controlPanel.getChildren().clear();
            VBox moveContent = new VBox(10);
            moveContent.setPadding(new Insets(10));
            ComboBox<String> directionCombo = new ComboBox<>();
            directionCombo.getItems().addAll("up", "down", "left", "right", "up_right", "up_left", "down_right", "down_left");
            directionCombo.setPromptText("Select direction");
            Slider lengthSlider = new Slider(1, GameLogic.MOVE_LIMIT, 1);
            lengthSlider.setShowTickLabels(true);
            lengthSlider.setShowTickMarks(true);
            lengthSlider.setMajorTickUnit(1);
            lengthSlider.setMinorTickCount(0);
            lengthSlider.setSnapToTicks(true);
            Button moveButton = new Button("Move");
            moveButton.setId("action-button");
            moveContent.getChildren().addAll(
                new Label("Select your move:"), directionCombo, lengthSlider, moveButton
            );
            controlPanel.getChildren().add(moveContent);
            messageArea.updateMessage("Your turn to move", MessageArea.MessageType.NEUTRAL);

            moveButton.setOnAction(e -> {
                String directionStr = directionCombo.getValue();
                int length = (int) lengthSlider.getValue();
                Direction direction = Direction.fromString(directionStr);
                if (direction == null || length <= 0) {
                    messageArea.updateMessage("Please select a valid direction and length.", MessageArea.MessageType.ERROR);
                } else {
                    future.complete(new Pair<>(direction, length));
                }
            });
        });
        return future.join();
    }

    @Override
    public boolean promptPlayAgain() {
        Platform.runLater(() -> {
            messageArea.updateMessage("Game over. Use the 'New Game' button to play again.", MessageArea.MessageType.GAME_END);
        });
        return false;
    }

    @Override
    public void displayMessage(String message) {
        Platform.runLater(() -> {
            MessageArea.MessageType type = message.toLowerCase().contains("calculating") ? 
                MessageArea.MessageType.NEUTRAL : MessageArea.MessageType.NEUTRAL;
            messageArea.updateMessage(message, type);
        });
    }

    @Override
    public void displayBoard(BoardState state) {
        Platform.runLater(() -> {
            if (boardUI == null) {
                System.err.println("Error: BoardUI not initialized for displayBoard");
                messageArea.updateMessage("Error: Board not initialized.", MessageArea.MessageType.ERROR);
                return;
            }
            boardUI.renderBoard(state);
        });
    }

    @Override
    public CompletableFuture<Void> displayGameFinished(int evaluationResult) {
        String message = switch (evaluationResult) {
            case 1 -> "I win! Use the 'New Game' button to play again.";
            case 0 -> "Tie! Use the 'New Game' button to play again.";
            case -1 -> "You win! Use the 'New Game' button to play again.";
            default -> "Game ended. Use the 'New Game' button to play again.";
        };
        return displayGameEndMessage(message, MessageArea.MessageType.GAME_END);
    }

    @Override
    public CompletableFuture<Void> displayGameEndError(MoveResult result, Direction direction, int length, int failureY, int failureX) {
        String message = String.format("Cannot move %s %d: %s %s (%d,%d). Game ended.",
            direction.toString().toLowerCase(), length, result.getMessage(),
            result.getPreposition() != null ? result.getPreposition() : "at", failureY, failureX);
        return displayGameEndMessage(message, MessageArea.MessageType.GAME_END);
    }

    @Override
    public void displayMoveError(MoveResult result, Direction direction, int length, int failureY, int failureX) {
        Platform.runLater(() -> {
            String message = String.format("Cannot move %s %d: %s %s (%d,%d).",
                direction.toString().toLowerCase(), length, result.getMessage(),
                result.getPreposition() != null ? result.getPreposition() : "at", failureY, failureX);
            messageArea.updateMessage(message, MessageArea.MessageType.ERROR);
        });
    }

    @Override
    public CompletableFuture<Void> displayGameEndMessage(String message, MessageArea.MessageType type) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        Platform.runLater(() -> {
            messageArea.updateMessage(message, type);
            controlPanel.getChildren().clear();
            future.complete(null);
        });
        return future;
    }
}