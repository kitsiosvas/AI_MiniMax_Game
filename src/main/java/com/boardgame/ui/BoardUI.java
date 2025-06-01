package com.boardgame.ui;

import com.boardgame.logic.BoardState;
import com.boardgame.logic.Direction;
import com.boardgame.logic.Pair;
import com.boardgame.ui.MessageArea;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class BoardUI implements IBoardUI {
    private final GridPane gridPane;
    private final Stage stage;
    private final MessageArea messageArea;
    private final int rows;
    private final int columns;
    private final StackPane[][] cells;
    private final Map<Character, String> stateToStyle;
    private List<int[]> blackSquares;
    private CompletableFuture<List<int[]>> blackSquaresFuture;
    private enum InteractionMode { NONE, PLACING_BLACK_SQUARES, SETTING_PLAYER_A, SETTING_PLAYER_B }

    private InteractionMode currentMode = InteractionMode.NONE;

    public BoardUI(Stage stage, GridPane gridPane, MessageArea messageArea, int rows, int columns) {
        this.stage = stage;
        this.gridPane = gridPane;
        this.messageArea = messageArea;
        this.rows = rows;
        this.columns = columns;
        this.cells = new StackPane[rows][columns];
        this.blackSquares = new ArrayList<>();
        this.stateToStyle = new HashMap<>();
        initializeStyleMap();
        initializeBoard();
    }

    private void initializeStyleMap() {
        stateToStyle.put(' ', ""); // Empty: default white
        stateToStyle.put('*', "black-square");
        stateToStyle.put('A', "player-a");
        stateToStyle.put('B', "player-b");
    }

    private void initializeBoard() {
        gridPane.getChildren().clear();
        double cellSize = Math.min(600.0 / Math.max(rows, columns), 60.0);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                StackPane cell = new StackPane();
                Rectangle rect = new Rectangle(cellSize, cellSize);
                rect.getStyleClass().add("cell");
                cell.getChildren().add(rect);
                cell.setPadding(new Insets(2));
                cells[i][j] = cell;
                gridPane.add(cell, j, i);
            }
        }
        applyStyles();
    }

    private void applyStyles() {
        String css = getClass().getResource("/styles.css").toExternalForm();
        if (css == null) {
            System.err.println("Error: styles.css not found at /styles.css");
            Platform.runLater(() -> messageArea.updateMessage("Error: styles.css not found", MessageArea.MessageType.ERROR));
        } else {
            gridPane.getStylesheets().add(css);
        }
    }

    @Override
    public void renderBoard(BoardState state) {
        Platform.runLater(() -> {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    StackPane cell = cells[i][j];
                    Rectangle rect = (Rectangle) cell.getChildren().get(0);
                    char cellState = state.getBoardCell(i, j);
                    rect.getStyleClass().clear();
                    rect.getStyleClass().add("cell");
                    String styleClass = stateToStyle.getOrDefault(cellState, "");
                    if (!styleClass.isEmpty()) {
                        rect.getStyleClass().add(styleClass);
                    }
                }
            }
        });
    }

    @Override
    public CompletableFuture<List<int[]>> promptBlackSquares() {
        blackSquaresFuture = new CompletableFuture<>();
        Platform.runLater(() -> {
            blackSquares.clear();
            currentMode = InteractionMode.PLACING_BLACK_SQUARES;
            messageArea.updateMessage("Click cells to set black squares (*). Confirm when done.", MessageArea.MessageType.NEUTRAL);
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    int row = i, col = j;
                    StackPane cell = cells[i][j];
                    cell.setOnMouseClicked(null); // Clear existing handlers
                    cell.setOnMouseClicked(e -> {
                        if (currentMode != InteractionMode.PLACING_BLACK_SQUARES) return;
                        Platform.runLater(() -> {
                            int[] pos = new int[]{row, col};
                            Rectangle rect = (Rectangle) cell.getChildren().get(0);
                            if (blackSquares.stream().anyMatch(p -> p[0] == row && p[1] == col)) {
                                blackSquares.removeIf(p -> p[0] == row && p[1] == col);
                                rect.getStyleClass().clear();
                                rect.getStyleClass().add("cell");
                                messageArea.updateMessage("Removed black square at (" + row + ", " + col + ")", MessageArea.MessageType.NEUTRAL);
                            } else {
                                blackSquares.add(pos);
                                rect.getStyleClass().clear();
                                rect.getStyleClass().add("cell");
                                rect.getStyleClass().add("black-square");
                                messageArea.updateMessage("Added black square at (" + row + ", " + col + ")", MessageArea.MessageType.NEUTRAL);
                            }
                        });
                    });
                }
            }
        });
        return blackSquaresFuture;
    }

    public void completeBlackSquaresPrompt() {
        Platform.runLater(() -> {
            if (currentMode == InteractionMode.PLACING_BLACK_SQUARES) {
                currentMode = InteractionMode.NONE;
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < columns; j++) {
                        cells[i][j].setOnMouseClicked(null); // Clear click handlers
                    }
                }
                if (blackSquaresFuture != null && !blackSquaresFuture.isDone()) {
                    blackSquaresFuture.complete(new ArrayList<>(blackSquares));
                }
            }
        });
    }

    @Override
    public CompletableFuture<int[][]> promptPlayerPositions() {
        CompletableFuture<int[][]> future = new CompletableFuture<>();
        Platform.runLater(() -> {
            int[] playerA = {-1, -1};
            int[] playerB = {-1, -1};
            currentMode = InteractionMode.SETTING_PLAYER_A;
            messageArea.updateMessage("Click to set Player A (red).", MessageArea.MessageType.NEUTRAL);

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    int row = i, col = j;
                    StackPane cell = cells[i][j];
                    cell.setOnMouseClicked(null); // Clear existing handlers
                    cell.setOnMouseClicked(e -> {
                        Platform.runLater(() -> {
                            if (currentMode == InteractionMode.SETTING_PLAYER_A) {
                                if (blackSquares.stream().anyMatch(p -> p[0] == row && p[1] == col)) {
                                    messageArea.updateMessage("Cannot place player on black square.", MessageArea.MessageType.ERROR);
                                    return;
                                }
                                if (playerA[0] != -1) {
                                    Rectangle oldRect = (Rectangle) cells[playerA[0]][playerA[1]].getChildren().get(0);
                                    oldRect.getStyleClass().clear();
                                    oldRect.getStyleClass().add("cell");
                                }
                                playerA[0] = row;
                                playerA[1] = col;
                                Rectangle rect = (Rectangle) cell.getChildren().get(0);
                                rect.getStyleClass().clear();
                                rect.getStyleClass().add("cell");
                                rect.getStyleClass().add("player-a");
                                currentMode = InteractionMode.SETTING_PLAYER_B;
                                messageArea.updateMessage("Click to set Player B (blue).", MessageArea.MessageType.NEUTRAL);
                            } else if (currentMode == InteractionMode.SETTING_PLAYER_B) {
                                if (blackSquares.stream().anyMatch(p -> p[0] == row && p[1] == col)) {
                                    messageArea.updateMessage("Cannot place player on black square.", MessageArea.MessageType.ERROR);
                                    return;
                                }
                                if (row == playerA[0] && col == playerA[1]) {
                                    messageArea.updateMessage("Players cannot share position.", MessageArea.MessageType.ERROR);
                                    return;
                                }
                                if (playerB[0] != -1) {
                                    Rectangle oldRect = (Rectangle) cells[playerB[0]][playerB[1]].getChildren().get(0);
                                    oldRect.getStyleClass().clear();
                                    oldRect.getStyleClass().add("cell");
                                }
                                playerB[0] = row;
                                playerB[1] = col;
                                Rectangle rect = (Rectangle) cell.getChildren().get(0);
                                rect.getStyleClass().clear();
                                rect.getStyleClass().add("cell");
                                rect.getStyleClass().add("player-b");
                                currentMode = InteractionMode.NONE;
                                for (int k = 0; k < rows; k++) {
                                    for (int l = 0; l < columns; l++) {
                                        cells[k][l].setOnMouseClicked(null); // Clear click handlers
                                    }
                                }
                                future.complete(new int[][]{{playerA[0], playerA[1]}, {playerB[0], playerB[1]}});
                            }
                        });
                    });
                }
            }
            future.completeOnTimeout(new int[][]{{playerA[0], playerA[1]}, {playerB[0], playerB[1]}}, 60000, java.util.concurrent.TimeUnit.MILLISECONDS);
        });
        return future;
    }

    @Override
    public void highlightCells(List<int[]> positions) {
        // Placeholder for future valid move highlighting
    }

    @Override
    public void clearBoard() {
        Platform.runLater(() -> {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    Rectangle rect = (Rectangle) cells[i][j].getChildren().get(0);
                    rect.getStyleClass().clear();
                    rect.getStyleClass().add("cell");
                    cells[i][j].setOnMouseClicked(null);
                }
            }
            blackSquares.clear();
            currentMode = InteractionMode.NONE;
            if (blackSquaresFuture != null && !blackSquaresFuture.isDone()) {
                blackSquaresFuture.complete(new ArrayList<>());
            }
        });
    }

    @Override
    public CompletableFuture<Pair<Direction, Integer>> promptPlayerMove() {
        // Placeholder for future click-based move input
        return new CompletableFuture<>();
    }
}