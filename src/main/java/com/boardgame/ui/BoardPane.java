package com.boardgame.ui;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;

public class BoardPane {
    private final GridPane root;

    public BoardPane() {
        root = new GridPane();
        root.setAlignment(Pos.CENTER);
    }

    public GridPane getRoot() {
        return root;
    }
}