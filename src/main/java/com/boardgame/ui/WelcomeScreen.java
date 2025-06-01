package com.boardgame.ui;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class WelcomeScreen {
    private final VBox root;
    private final Button startGameButton;

    public WelcomeScreen() {
        root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setId("welcome-pane");

        Label title = new Label("Board Game");
        title.setId("title");

        startGameButton = new Button("Start Game");
        startGameButton.setId("action-button");

        root.getChildren().addAll(title, startGameButton);

        // Welcome Animation
        FadeTransition fade = new FadeTransition(Duration.millis(1000), title);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    public VBox getRoot() {
        return root;
    }

    public Button getstartGameButton() {
        return startGameButton;
    }
}