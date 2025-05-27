import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainUI extends Application {
    private Scene welcomeScene;
    private Scene gameScene;
    private Thread gameThread;
    private JavaFXGameIO gameIO;

    @Override
    public void start(Stage primaryStage) {
        // Welcome Screen
        WelcomeScreen welcomeScreen = new WelcomeScreen();
        welcomeScene = new Scene(welcomeScreen.getRoot(), 800, 600);
        welcomeScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        // Game Screen
        GameScreen gameScreen = new GameScreen();
        gameScene = new Scene(gameScreen.getRoot(), 800, 600);
        gameScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        // Initialize GameIO
        gameIO = new JavaFXGameIO(
            primaryStage,
            gameScreen.getBoardGrid(),
            gameScreen.getMessageLabel(),
            gameScreen.getControlPanel(),
            gameScreen.getProgressIndicator()
        );

        // Scene Switching and Game Control
        welcomeScreen.getStartButton().setOnAction(e -> {
            startNewGame(primaryStage, gameScreen);
            switchScene(primaryStage, gameScene);
        });

        gameScreen.getNewGameButton().setOnAction(e -> {
            stopCurrentGame();
            gameIO.reset();
            switchScene(primaryStage, welcomeScene);
        });

        // Initial Scene Setup
        primaryStage.setScene(welcomeScene);
        primaryStage.setTitle("Board Game");
        primaryStage.show();
    }

    private void startNewGame(Stage stage, GameScreen gameScreen) {
        stopCurrentGame();
        gameIO = new JavaFXGameIO(
            stage,
            gameScreen.getBoardGrid(),
            gameScreen.getMessageLabel(),
            gameScreen.getControlPanel(),
            gameScreen.getProgressIndicator()
        );
        GameManager gameManager = new GameManager(gameIO);
        gameThread = new Thread(() -> gameManager.startGame());
        gameThread.setDaemon(true); // Ensure thread stops when app closes
        gameThread.start();
    }

    private void stopCurrentGame() {
        if (gameThread != null && gameThread.isAlive()) {
            gameThread.interrupt();
            try {
                gameThread.join(100); // Wait briefly for thread to stop
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        gameThread = null;
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