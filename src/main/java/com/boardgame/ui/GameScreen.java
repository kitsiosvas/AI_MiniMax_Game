package com.boardgame.ui;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class GameScreen {
    private final BorderPane root;
    private final Toolbar toolbar;
    private final BoardPane boardPane;
    private final ControlPanel controlPanel;
    private final MessageArea messageArea;

    public GameScreen() {
        root = new BorderPane();
        root.setPadding(new Insets(10));

        toolbar = new Toolbar();
        boardPane = new BoardPane();
        controlPanel = new ControlPanel();
        messageArea = new MessageArea();

        root.setTop(toolbar.getRoot());
        root.setCenter(boardPane.getRoot());
        root.setLeft(controlPanel.getRoot());
        root.setBottom(messageArea.getRoot());
    }

    public BorderPane getRoot() {
        return root;
    }

    public GridPane getBoardGrid() {
        return boardPane.getRoot();
    }

    public VBox getControlPanel() {
        return controlPanel.getRoot();
    }

    public Label getMessageLabel() {
        return messageArea.getMessageLabel();
    }

    public ProgressIndicator getProgressIndicator() {
        return messageArea.getProgressIndicator();
    }

    public Button getNewGameButton() {
        return toolbar.getNewGameButton();
    }
}