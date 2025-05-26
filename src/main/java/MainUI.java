import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainUI extends Application {
    private Scene welcomeScene;
    private Scene gameScene;

    @Override
    public void start(Stage primaryStage) {
        // Welcome Screen
        VBox welcome = new VBox(20);
        welcome.setAlignment(Pos.CENTER);
        welcome.setId("welcome-pane");
        Label title = new Label("Board Game");
        title.setId("title");
        Button startButton = new Button("Start Game");
        startButton.setId("action-button");
        welcome.getChildren().addAll(title, startButton);
        welcomeScene = new Scene(welcome, 800, 600);
        welcomeScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        // Game Screen
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        GridPane boardGrid = new GridPane();
        boardGrid.setAlignment(Pos.CENTER);
        VBox controlPanel = new VBox(10);
        controlPanel.setPrefWidth(300);
        controlPanel.setId("control-panel");
        Label messageLabel = new Label("== Java program started ==");
        messageLabel.setId("message-label");
        HBox toolbar = new HBox(10);
        toolbar.setId("toolbar");
        Button newGameButton = new Button("New Game");
        newGameButton.setId("action-button");
        toolbar.getChildren().add(newGameButton);
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);
        root.setTop(toolbar);
        root.setCenter(boardGrid);
        root.setLeft(controlPanel);
        root.setBottom(messageLabel);
        BorderPane.setAlignment(messageLabel, Pos.CENTER);
        gameScene = new Scene(root, 800, 600);
        gameScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        // Welcome Animation
        FadeTransition fade = new FadeTransition(Duration.millis(1000), title);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        // Scene Switching with Maximized State Preservation
        startButton.setOnAction(e -> switchScene(primaryStage, gameScene));
        newGameButton.setOnAction(e -> switchScene(primaryStage, welcomeScene));

        // Initial Scene Setup
        primaryStage.setScene(welcomeScene);
        primaryStage.setTitle("Board Game");
        primaryStage.show();

        // Start Game
        GameIO gameIO = new JavaFXGameIO(primaryStage, boardGrid, messageLabel, controlPanel, progressIndicator);
        GameManager gameManager = new GameManager(gameIO);
        new Thread(() -> gameManager.startGame()).start();
    }

    private void switchScene(Stage stage, Scene newScene) {
        boolean isMaximized = stage.isMaximized();
        double x = stage.getX();
        double y = stage.getY();
        double width = stage.getWidth();
        double height = stage.getHeight();

        stage.setScene(newScene);

        // Restore state after scene switch
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