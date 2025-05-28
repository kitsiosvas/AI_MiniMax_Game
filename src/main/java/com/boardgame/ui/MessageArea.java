package com.boardgame.ui;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public class MessageArea {
    private final HBox root;
    private final Label messageLabel;
    private final ProgressIndicator progressIndicator;

    // Enum for message types
    public enum MessageType {
        NEUTRAL, ERROR, GAME_END
    }

    public MessageArea() {
        root = new HBox(10);
        root.setAlignment(Pos.CENTER);

        messageLabel = new Label("== Java program started ==");
        messageLabel.setId("message-label");
        messageLabel.getStyleClass().add("message-neutral"); // Default style

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

    // Update message with type-specific styling and animation
    public void updateMessage(String message, MessageType type) {
        System.out.println("Updating MessageArea: " + message + " (Type: " + type + ")"); // Debug
        Platform.runLater(() -> {
            messageLabel.getStyleClass().removeAll("message-neutral", "message-error", "message-game-end");
            switch (type) {
                case ERROR:
                    messageLabel.getStyleClass().add("message-error");
                    break;
                case GAME_END:
                    messageLabel.getStyleClass().add("message-game-end");
                    break;
                default:
                    messageLabel.getStyleClass().add("message-neutral");
            }
            messageLabel.setText(message);

            // Control ProgressIndicator visibility
            boolean isCalculating = message.toLowerCase().contains("calculating");
            progressIndicator.setVisible(isCalculating);
            System.out.println("ProgressIndicator set to visible: " + isCalculating); // Debug

            // Apply fade animation
            FadeTransition fade = new FadeTransition(Duration.millis(300), messageLabel);
            fade.setFromValue(0.5);
            fade.setToValue(1);
            fade.play();
        });
    }
}