package com.boardgame.ui;
import javafx.scene.layout.VBox;

public class ControlPanel {
    private final VBox root;

    public ControlPanel() {
        root = new VBox(10);
        root.setPrefWidth(300);
        root.setId("control-panel");
    }

    public VBox getRoot() {
        return root;
    }
}