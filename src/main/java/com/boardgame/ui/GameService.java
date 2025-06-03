package com.boardgame.ui;

import com.boardgame.logic.GameManager;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class GameService extends Service<Void> {
    private final GameManager gameManager;

    public GameService(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() {
                gameManager.startNewGame();
                return null;
            }
        };
    }
}