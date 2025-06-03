package com.boardgame.ui;

import com.boardgame.io.JavaFXGameIO;
import com.boardgame.logic.GameManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainUI extends Application {
    private Scene welcomeScene;
    private Scene gameScene;
    private GameService gameService;
    private JavaFXGameIO gameIO;
    private GameManager gameManager;

    @Override
    public void start(Stage primaryStage) {
        // Welcome Screen
        WelcomeScreen welcomeScreen = new WelcomeScreen();
        welcomeScene = new Scene(welcomeScreen.getRoot(), 800, 600);
        String css = getClass().getResource("/styles.css").toExternalForm();
        if (css == null) {
            System.err.println("Error: styles.css not found at /styles.css");
        } else {
            welcomeScene.getStylesheets().add(css);
            System.out.println("Loaded styles.css for welcomeScene");
        }

        // Game Screen
        GameScreen gameScreen = new GameScreen();
        gameScene = new Scene(gameScreen.getRoot(), 800, 600);
        if (css == null) {
            System.err.println("Error: styles.css not found at /styles.css");
        } else {
            gameScene.getStylesheets().add(css);
            System.out.println("Loaded styles.css for gameScene");
        }

        // Initialize GameIO with callbacks
        gameIO = new JavaFXGameIO(
            primaryStage,
            gameScreen.getBoardGrid(),
            gameScreen.getMessageArea(),
            gameScreen.getControlPanel(),
            () -> switchScene(primaryStage, welcomeScene));

        // Scene Switching and Game Control
        welcomeScreen.getstartGameButton().setOnAction(e -> {
            startNewGame(primaryStage, gameScreen);
            switchScene(primaryStage, gameScene);
        });

        gameScreen.getMainMenuButton().setOnAction(e -> {
            stopCurrentGame();
            gameIO.reset();
            switchScene(primaryStage, welcomeScene);
        });

        gameScreen.getNewGameButton().setOnAction(e -> {
            stopCurrentGame();
            gameIO.reset();
            startNewGame(primaryStage, gameScreen);
        });

        // Initial Scene Setup
        primaryStage.setScene(welcomeScene);
        primaryStage.setTitle("Board Game");
        primaryStage.show();
    }

    private void startNewGame(Stage stage, GameScreen gameScreen) {
        stopCurrentGame();
        // Disable buttons to prevent rapid clicks during setup
        Platform.runLater(() -> {
            gameScreen.getNewGameButton().setDisable(true);
            gameScreen.getMainMenuButton().setDisable(true);
            System.out.println("Buttons disabled at " + System.currentTimeMillis());
        });

        gameIO = new JavaFXGameIO(
            stage,
            gameScreen.getBoardGrid(),
            gameScreen.getMessageArea(),
            gameScreen.getControlPanel(),
            () -> switchScene(stage, welcomeScene));
        gameManager = new GameManager(gameIO);
        gameService = new GameService(gameManager);

        // Set up listeners to manage button states with debugging
        gameService.setOnRunning(e -> {
            System.out.println("GameService running at " + System.currentTimeMillis());
        });

        gameService.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                System.out.println("GameService succeeded at " + System.currentTimeMillis());
                gameScreen.getNewGameButton().setDisable(false);
                gameScreen.getMainMenuButton().setDisable(false);
            });
        });

        gameService.setOnFailed(e -> {
            Platform.runLater(() -> {
                System.out.println("GameService failed at " + System.currentTimeMillis() + ": " + gameService.getException().getMessage());
                gameIO.displayMessage("Game failed: " + gameService.getException().getMessage());
                gameIO.reset();
                gameScreen.getNewGameButton().setDisable(false);
                gameScreen.getMainMenuButton().setDisable(false);
            });
            gameService.getException().printStackTrace();
        });

        gameService.setOnCancelled(e -> {
            Platform.runLater(() -> {
                System.out.println("GameService cancelled at " + System.currentTimeMillis());
                gameIO.reset();
                gameScreen.getNewGameButton().setDisable(false);
                gameScreen.getMainMenuButton().setDisable(false);
            });
        });

        // Fallback: Re-enable buttons if service fails to start
        gameService.setOnScheduled(e -> {
            if (!gameService.isRunning()) {
                Platform.runLater(() -> {
                    System.out.println("GameService not running after scheduling at " + System.currentTimeMillis());
                    gameScreen.getNewGameButton().setDisable(false);
                    gameScreen.getMainMenuButton().setDisable(false);
                });
            }
        });

        gameService.start();
    }

    private void stopCurrentGame() {
        if (gameService != null && gameService.isRunning()) {
            gameService.cancel();
            if (gameManager != null) {
                gameManager.getIsGameCancelled().set(true);
            }
        }
        gameService = null;
    }

    private void switchScene(Stage stage, Scene newScene) {
        boolean isMaximized = stage.isMaximized();
        double x = stage.getX();
        double y = stage.getY();
        double width = stage.getWidth();
        double height = stage.getHeight();

        stage.setScene(newScene);

        Platform.runLater(() -> {
            if (isMaximized) {
                stage.setMaximized(true);
            } else {
                stage.setX(x);
                stage.setY(y);
                stage.setWidth(width);
                stage.setHeight(height);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}