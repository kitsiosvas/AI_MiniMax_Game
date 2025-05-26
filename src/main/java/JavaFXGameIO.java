import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class JavaFXGameIO implements GameIO {
    private final Stage primaryStage;
    private final GridPane boardGrid;
    private final Label messageLabel;
    private final VBox controlPanel;
    private final ProgressIndicator progressIndicator;
    private int rows;
    private int columns;
    private List<int[]> blackSquares;

    // Transition configuration
    private enum TransitionType { FADE, SLIDE, SCALE }
    private final TransitionType transitionType = TransitionType.FADE; // Change to SLIDE or SCALE to try
    private final double transitionDuration = 500; // ms

    public JavaFXGameIO(Stage stage, GridPane boardGrid, Label messageLabel, VBox controlPanel, ProgressIndicator progressIndicator) {
        this.primaryStage = stage;
        this.boardGrid = boardGrid;
        this.messageLabel = messageLabel;
        this.controlPanel = controlPanel;
        this.progressIndicator = progressIndicator;
        this.blackSquares = new ArrayList<>();
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
            applyTransition(tabPane);

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
                    showAlert("Invalid Input", "Please enter valid integers.");
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
            updateBoardDisplay(new char[rows][columns]);
            applyTransition(tabPane);

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    int row = i, col = j;
                    Rectangle cell = (Rectangle) boardGrid.getChildren().get(i * columns + j);
                    cell.setOnMouseClicked(e -> {
                        if (blackSquares.contains(new int[]{row, col})) {
                            blackSquares.removeIf(pos -> pos[0] == row && pos[1] == col);
                            applyFadeEffect(cell, Color.WHITE);
                        } else {
                            blackSquares.add(new int[]{row, col});
                            applyFadeEffect(cell, Color.GRAY);
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
            char[][] tempBoard = new char[rows][columns];
            for (int[] pos : blackSquares) {
                tempBoard[pos[0]][pos[1]] = '*';
            }
            updateBoardDisplay(tempBoard);
            applyTransition(tabPane);

            int[] playerA = {-1, -1};
            int[] playerB = {-1, -1};
            boolean[] selectingPlayerA = {true};

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    int row = i, col = j;
                    Rectangle cell = (Rectangle) boardGrid.getChildren().get(i * columns + j);
                    cell.setOnMouseClicked(e -> {
                        if (tempBoard[row][col] == '*') {
                            showAlert("Invalid Position", "Cannot place player on black square.");
                            return;
                        }
                        if (selectingPlayerA[0]) {
                            if (playerA[0] != -1) {
                                Rectangle oldCell = (Rectangle) boardGrid.getChildren().get(playerA[0] * columns + playerA[1]);
                                applyFadeEffect(oldCell, tempBoard[playerA[0]][playerA[1]] == '*' ? Color.GRAY : Color.WHITE);
                            }
                            playerA[0] = row;
                            playerA[1] = col;
                            applyFadeEffect(cell, Color.RED);
                            selectingPlayerA[0] = false;
                            instruction.setText("Click to set Player B (blue):");
                        } else {
                            if (row == playerA[0] && col == playerA[1]) {
                                showAlert("Invalid Position", "Players cannot share position.");
                                return;
                            }
                            if (playerB[0] != -1) {
                                Rectangle oldCell = (Rectangle) boardGrid.getChildren().get(playerB[0] * columns + playerB[1]);
                                applyFadeEffect(oldCell, tempBoard[playerB[0]][playerB[1]] == '*' ? Color.GRAY : Color.WHITE);
                            }
                            playerB[0] = row;
                            playerB[1] = col;
                            applyFadeEffect(cell, Color.BLUE);
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
            applyTransition(moveContent);

            moveButton.setOnAction(e -> {
                String directionStr = directionCombo.getValue();
                int length = (int) lengthSlider.getValue();
                Direction direction = Direction.fromString(directionStr);
                if (direction == null || length <= 0) {
                    showAlert("Invalid Move", "Please select valid direction and length.");
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
            alert.setTitle("Game Over");
            alert.setHeaderText("Play another game?");
            ButtonType playAgain = new ButtonType("Play Again");
            ButtonType mainMenu = new ButtonType("Main Menu");
            alert.getButtonTypes().setAll(playAgain, mainMenu);
            alert.initOwner(primaryStage);
            alert.initModality(Modality.APPLICATION_MODAL);
            applyTransition(alert.getDialogPane());
            alert.showAndWait().ifPresent(response -> {
                future.complete(response == playAgain);
                if (response == mainMenu) {
                    Platform.runLater(() -> primaryStage.setScene(primaryStage.getScene().getRoot().getScene()));
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
        double cellSize = Math.min(600.0 / Math.max(board.length, board[0].length), 60.0);
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                Rectangle cell = new Rectangle(cellSize, cellSize);
                cell.setArcWidth(10);
                cell.setArcHeight(10);
                switch (board[i][j]) {
                    case '*': applyFadeEffect(cell, Color.GRAY); break;
                    case 'A': applyFadeEffect(cell, Color.RED); break;
                    case 'B': applyFadeEffect(cell, Color.BLUE); break;
                    default: applyFadeEffect(cell, Color.WHITE); break;
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
        applyTransition(alert.getDialogPane());
        alert.showAndWait();
    }

    private void applyTransition(Node node) {
        switch (transitionType) {
            case FADE:
                FadeTransition fade = new FadeTransition(Duration.millis(transitionDuration), node);
                fade.setFromValue(0);
                fade.setToValue(1);
                fade.play();
                break;
            case SLIDE:
                TranslateTransition slide = new TranslateTransition(Duration.millis(transitionDuration), node);
                slide.setFromX(-300);
                slide.setToX(0);
                slide.play();
                break;
            case SCALE:
                ScaleTransition scale = new ScaleTransition(Duration.millis(transitionDuration), node);
                scale.setFromX(0.5);
                scale.setFromY(0.5);
                scale.setToX(1);
                scale.setToY(1);
                scale.play();
                break;
        }
    }

    private void applyFadeEffect(Rectangle cell, Color color) {
        cell.setFill(color);
        FadeTransition fade = new FadeTransition(Duration.millis(300), cell);
        fade.setFromValue(0.5);
        fade.setToValue(1);
        fade.play();
    }
}