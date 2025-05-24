import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainUI extends Application {
    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        GridPane boardGrid = new GridPane();
        boardGrid.setAlignment(Pos.CENTER);
        Label messageLabel = new Label("== Java program started ==");
        messageLabel.setId("message-label");
        VBox controlPanel = new VBox(10);
        controlPanel.setAlignment(Pos.CENTER_LEFT);
        controlPanel.setPadding(new Insets(10));
        controlPanel.setId("control-panel");
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);
        root.setCenter(boardGrid);
        root.setBottom(messageLabel);
        root.setRight(controlPanel);
        root.setTop(progressIndicator);
        BorderPane.setAlignment(messageLabel, Pos.CENTER);
        BorderPane.setAlignment(progressIndicator, Pos.CENTER);

        Scene scene = new Scene(root, 800, 600);
        try {
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        } catch (NullPointerException e) {
            System.err.println("Warning: styles.css not found in src/main/resources. Using default styling.");
        }
        primaryStage.setScene(scene);
        primaryStage.setTitle("Board Game");
        primaryStage.show();

        GameIO gameIO = new JavaFXGameIO(primaryStage, boardGrid, messageLabel, controlPanel);
        GameManager gameManager = new GameManager(gameIO);
        new Thread(() -> gameManager.startGame()).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}