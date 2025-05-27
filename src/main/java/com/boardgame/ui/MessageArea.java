package com.boardgame.ui;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;

public class MessageArea {
    private final HBox root;
    private final Label messageLabel;
    private final ProgressIndicator progressIndicator;

    public MessageArea() {
        root = new HBox(10);
        root.setAlignment(Pos.CENTER);

        messageLabel = new Label("== Java program started ==");
        messageLabel.setId("message-label");

        progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);

        root.getChildren().addAll(messageLabel, progressIndicator);
    }

    public HBox getRoot() {
        return root;
    }

    public Label getMessageLabel() {
        return messageLabel;
    }

    public ProgressIndicator getProgressIndicator() {
        return progressIndicator;
    }
}