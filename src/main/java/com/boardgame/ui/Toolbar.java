package com.boardgame.ui;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class Toolbar {
    private final HBox root;
    private final Button mainMenuButton;
    private final Button newGameButton;

    public Toolbar() {
        root = new HBox(10);
        root.setId("toolbar");
        mainMenuButton = new Button("Main Menu");
        mainMenuButton.setId("action-button");
        newGameButton = new Button("New Game");
        newGameButton.setId("action-button");
        root.getChildren().addAll(mainMenuButton, newGameButton);
    }

    public HBox getRoot() {
        return root;
    }

    public Button getMainMenuButton() {
        return mainMenuButton;
    }

    public Button getNewGameButton() {
        return newGameButton;
    }
}