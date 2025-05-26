import javafx.animation.FadeTransition;
import javafx.application.Application;
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
        Scene welcomeScene = new Scene(welcome, 800, 600);
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
        Scene gameScene = new Scene(root, 800, 600);
        gameScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        // Welcome Animation
        FadeTransition fade = new FadeTransition(Duration.millis(1000), title);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        // Scene Switching
        startButton.setOnAction(e -> primaryStage.setScene(gameScene));
        newGameButton.setOnAction(e -> primaryStage.setScene(welcomeScene));

        primaryStage.setScene(welcomeScene);
        primaryStage.setTitle("Board Game");
        primaryStage.show();

        // Start Game
        GameIO gameIO = new JavaFXGameIO(primaryStage, boardGrid, messageLabel, controlPanel, progressIndicator);
        GameManager gameManager = new GameManager(gameIO);
        new Thread(() -> gameManager.startGame()).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}