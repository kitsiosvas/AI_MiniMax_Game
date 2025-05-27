package com.boardgame.ui;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class WelcomeScreen {
    private final VBox root;
    private final Button startButton;

    public WelcomeScreen() {
        root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setId("welcome-pane");

        Label title = new Label("Board Game");
        title.setId("title");

        startButton = new Button("Start Game");
        startButton.setId("action-button");

        root.getChildren().addAll(title, startButton);

        // Welcome Animation
        FadeTransition fade = new FadeTransition(Duration.millis(1000), title);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    public VBox getRoot() {
        return root;
    }

    public Button getStartButton() {
        return startButton;
    }
}