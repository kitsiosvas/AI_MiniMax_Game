import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class Toolbar {
    private final HBox root;
    private final Button newGameButton;

    public Toolbar() {
        root = new HBox(10);
        root.setId("toolbar");
        newGameButton = new Button("New Game");
        newGameButton.setId("action-button");
        root.getChildren().add(newGameButton);
    }

    public HBox getRoot() {
        return root;
    }

    public Button getNewGameButton() {
        return newGameButton;
    }
}