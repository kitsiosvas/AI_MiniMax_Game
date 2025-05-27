package com.boardgame.logic;

public enum MoveResult {
    SUCCESS(null, null),
    OUT_OF_BOUNDS("out of bounds", "at"),
    HIT_OBSTACLE("hits black square", "at"),
    HIT_OPPONENT("blocked by opponent", "at");

    private final String message;
    private final String preposition;

    MoveResult(String message, String preposition) {
        this.message = message;
        this.preposition = preposition;
    }

    public String getMessage() {
        return message;
    }

    public String getPreposition() {
        return preposition;
    }
}