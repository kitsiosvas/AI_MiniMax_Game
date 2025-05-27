import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class GameScreen {
    private final BorderPane root;
    private final GridPane boardGrid;
    private final VBox controlPanel;
    private final Label messageLabel;
    private final ProgressIndicator progressIndicator;
    private final Button newGameButton;

    public GameScreen() {
        root = new BorderPane();
        root.setPadding(new Insets(10));

        boardGrid = new GridPane();
        boardGrid.setAlignment(Pos.CENTER);

        controlPanel = new VBox(10);
        controlPanel.setPrefWidth(300);
        controlPanel.setId("control-panel");

        messageLabel = new Label("== Java program started ==");
        messageLabel.setId("message-label");

        HBox toolbar = new HBox(10);
        toolbar.setId("toolbar");
        newGameButton = new Button("New Game");
        newGameButton.setId("action-button");
        toolbar.getChildren().add(newGameButton);

        progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);

        root.setTop(toolbar);
        root.setCenter(boardGrid);
        root.setLeft(controlPanel);
        root.setBottom(messageLabel);
        BorderPane.setAlignment(messageLabel, Pos.CENTER);
    }

    public BorderPane getRoot() {
        return root;
    }

    public GridPane getBoardGrid() {
        return boardGrid;
    }

    public VBox getControlPanel() {
        return controlPanel;
    }

    public Label getMessageLabel() {
        return messageLabel;
    }

    public ProgressIndicator getProgressIndicator() {
        return progressIndicator;
    }

    public Button getNewGameButton() {
        return newGameButton;
    }
}