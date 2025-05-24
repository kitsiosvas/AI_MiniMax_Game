import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class JavaFXGameIO implements GameIO {
    private final Stage primaryStage;
    private final GridPane boardGrid;
    private final Label messageLabel;
    private final VBox controlPanel;
    private int rows;
    private int columns;
    private List<int[]> blackSquares;

    public JavaFXGameIO(Stage stage, GridPane boardGrid, Label messageLabel, VBox controlPanel) {
        this.primaryStage = stage;
        this.boardGrid = boardGrid;
        this.messageLabel = messageLabel;
        this.controlPanel = controlPanel;
        this.blackSquares = new ArrayList<>();
    }

    @Override
    public int[] promptBoardSize() {
        CompletableFuture<int[]> future = new CompletableFuture<>();
        Platform.runLater(() -> {
            controlPanel.getChildren().clear();
            TextField rowsField = new TextField();
            rowsField.setPromptText("Rows");
            TextField colsField = new TextField();
            colsField.setPromptText("Columns");
            Button submitButton = new Button("Set Board");
            submitButton.setId("action-button");
            Label noteLabel = new Label("Note: For boards larger than 4x4, AI moves may take time.");
            noteLabel.setId("note-label");
            controlPanel.getChildren().addAll(
                new Label("Enter board size:"), rowsField, colsField, noteLabel, submitButton
            );

            submitButton.setOnAction(e -> {
                try {
                    int rows = Integer.parseInt(rowsField.getText());
                    int cols = Integer.parseInt(colsField.getText());
                    if (rows <= 0 || cols <= 0) {
                        showAlert("Invalid Input", "Rows and columns must be positive.");
                    } else {
                        this.rows = rows;
                        this.columns = cols;
                        future.complete(new int[]{rows, cols});
                    }
                } catch (NumberFormatException ex) {
                    showAlert("Invalid Input", "Please enter valid integers for rows and columns.");
                }
            });
        });
        return future.join();
    }

    @Override
    public List<int[]> promptBlackSquares(int rows, int columns) {
        CompletableFuture<List<int[]>> future = new CompletableFuture<>();
        blackSquares.clear();
        Platform.runLater(() -> {
            controlPanel.getChildren().clear();
            updateBoardDisplay(new char[rows][columns]); // Empty board
            Button confirmButton = new Button("Confirm Black Squares");
            confirmButton.setId("action-button");
            controlPanel.getChildren().addAll(
                new Label("Click cells to set black squares (*), then confirm:"), confirmButton
            );

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    int row = i, col = j;
                    Rectangle cell = (Rectangle) boardGrid.getChildren().get(i * columns + j);
                    cell.setOnMouseClicked(e -> {
                        if (blackSquares.contains(new int[]{row, col})) {
                            blackSquares.removeIf(pos -> pos[0] == row && pos[1] == col);
                            cell.setFill(Color.WHITE);
                        } else {
                            blackSquares.add(new int[]{row, col});
                            cell.setFill(Color.GRAY);
                        }
                    });
                }
            }

            confirmButton.setOnAction(e -> future.complete(new ArrayList<>(blackSquares)));
        });
        return future.join();
    }

    @Override
    public int[][] promptPlayerPositions(int rows, int columns) {
        CompletableFuture<int[][]> future = new CompletableFuture<>();
        Platform.runLater(() -> {
            controlPanel.getChildren().clear();
            char[][] tempBoard = new char[rows][columns];
            for (int[] pos : blackSquares) {
                tempBoard[pos[0]][pos[1]] = '*';
            }
            updateBoardDisplay(tempBoard);
            Button confirmButton = new Button("Confirm Positions");
            confirmButton.setId("action-button");
            Label instruction = new Label("Click to set Player A (red), then Player B (blue):");
            controlPanel.getChildren().addAll(instruction, confirmButton);

            int[] playerA = {-1, -1};
            int[] playerB = {-1, -1};
            boolean[] selectingPlayerA = {true};

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    int row = i, col = j;
                    Rectangle cell = (Rectangle) boardGrid.getChildren().get(i * columns + j);
                    cell.setOnMouseClicked(e -> {
                        if (tempBoard[row][col] == '*') {
                            showAlert("Invalid Position", "Cannot place player on a black square.");
                            return;
                        }
                        if (selectingPlayerA[0]) {
                            if (playerA[0] != -1) {
                                Rectangle oldCell = (Rectangle) boardGrid.getChildren().get(playerA[0] * columns + playerA[1]);
                                oldCell.setFill(tempBoard[playerA[0]][playerA[1]] == '*' ? Color.GRAY : Color.WHITE);
                            }
                            playerA[0] = row;
                            playerA[1] = col;
                            cell.setFill(Color.RED);
                            selectingPlayerA[0] = false;
                            instruction.setText("Click to set Player B (blue):");
                        } else {
                            if (row == playerA[0] && col == playerA[1]) {
                                showAlert("Invalid Position", "Players cannot occupy the same position.");
                                return;
                            }
                            if (playerB[0] != -1) {
                                Rectangle oldCell = (Rectangle) boardGrid.getChildren().get(playerB[0] * columns + playerB[1]);
                                oldCell.setFill(tempBoard[playerB[0]][playerB[1]] == '*' ? Color.GRAY : Color.WHITE);
                            }
                            playerB[0] = row;
                            playerB[1] = col;
                            cell.setFill(Color.BLUE);
                        }
                    });
                }
            }

            confirmButton.setOnAction(e -> {
                if (playerA[0] == -1 || playerB[0] == -1) {
                    showAlert("Invalid Input", "Please set positions for both players.");
                } else {
                    future.complete(new int[][]{{playerA[0], playerA[1]}, {playerB[0], playerB[1]}});
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
            controlPanel.getChildren().addAll(
                new Label("Select your move:"), directionCombo, lengthSlider, moveButton
            );

            moveButton.setOnAction(e -> {
                String directionStr = directionCombo.getValue();
                int length = (int) lengthSlider.getValue();
                Direction direction = Direction.fromString(directionStr);
                if (direction == null || length <= 0) {
                    showAlert("Invalid Move", "Please select a valid direction and length.");
                } else {
                    future.complete(new Pair<>(direction, length));
                }
            });
        });
        return future.join();
    }

    @Override
    public boolean promptPlayAgain() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Play Again");
            alert.setHeaderText("Play another game?");
            alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
            alert.initOwner(primaryStage);
            alert.showAndWait().ifPresent(response -> {
                future.complete(response == ButtonType.YES);
                if (response == ButtonType.NO) {
                    primaryStage.close();
                }
            });
        });
        return future.join();
    }

    @Override
    public void displayMessage(String message) {
        Platform.runLater(() -> messageLabel.setText(message));
    }

    @Override
    public void displayBoard(BoardState state) {
        Platform.runLater(() -> updateBoardDisplay(state.getBoard()));
    }

    @Override
    public void displayMoveError(MoveResult result, Direction direction, int length, int failureY, int failureX) {
        Platform.runLater(() -> showAlert("Move Failed",
            String.format("Cannot move %s %d: %s at (%d,%d). Game ended.",
                direction.toString().toLowerCase(), length, result.getMessage(), failureY, failureX)));
    }

    private void updateBoardDisplay(char[][] board) {
        boardGrid.getChildren().clear();
        double cellSize = Math.min(400.0 / Math.max(board.length, board[0].length), 50.0);
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                Rectangle cell = new Rectangle(cellSize, cellSize);
                switch (board[i][j]) {
                    case '*': cell.setFill(Color.GRAY); break;
                    case 'A': cell.setFill(Color.RED); break;
                    case 'B': cell.setFill(Color.BLUE); break;
                    default: cell.setFill(Color.WHITE); break;
                }
                cell.setStroke(Color.BLACK);
                cell.setStrokeWidth(1);
                boardGrid.add(cell, j, i);
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(primaryStage);
        alert.showAndWait();
    }
}