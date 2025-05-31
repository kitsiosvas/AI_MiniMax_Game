package com.boardgame.logic;

import java.util.ArrayList;
import java.util.List;

public class GameLogic {
    public static final int MOVE_LIMIT = 2;

    public Pair<MoveResult, int[]> isValidMove(BoardState state, int player, Direction direction, int moveLength, int currentX, int currentY) {
        char playerChar = (player == 1) ? 'A' : 'B';
        int x = currentX;
        int y = currentY;

        // Check each step of the move
        for (int i = 0; i < moveLength; i++) {
            x += direction.getDx();
            y += direction.getDy();

            // Check bounds
            if (x < 0 || x >= state.getColumns() || y < 0 || y >= state.getRows()) {
                return new Pair<>(MoveResult.OUT_OF_BOUNDS, new int[]{y, x});
            }

            // Check for obstacle or opponent
            char cell = state.getBoardCell(y, x);
            if (cell != ' ' && cell != playerChar) {
                if (cell == '*') {
                    return new Pair<>(MoveResult.HIT_OBSTACLE, new int[]{y, x});
                } else {
                    return new Pair<>(MoveResult.HIT_OPPONENT, new int[]{y, x});
                }
            }
        }

        return new Pair<>(MoveResult.SUCCESS, new int[]{-1, -1});
    }

    public BoardState makeMove(BoardState current, int player, Direction direction, int moveLength, int currentX, int currentY) {
        // Create a new BoardState with a deep copy
        BoardState newState = new BoardState(current.getRows(), current.getColumns());
        for (int i = 0; i < current.getRows(); i++) {
            for (int j = 0; j < current.getColumns(); j++) {
                newState.setBoardCell(i, j, current.getBoardCell(i, j));
            }
        }

        // Keep opponent's position
        if (player == 1) {
            newState.setPlayerBPosition(current.getPlayerBX(), current.getPlayerBY());
        } else {
            newState.setPlayerAPosition(current.getPlayerAX(), current.getPlayerAY());
        }

        // Set starting and intermediate squares to '*'
        int x = currentX;
        int y = currentY;
        for (int i = 0; i < moveLength; i++) {
            newState.setBoardCell(y, x, '*');
            x += direction.getDx();
            y += direction.getDy();
        }

        // Set destination square to player and update position
        char playerChar = (player == 1) ? 'A' : 'B';
        newState.setBoardCell(y, x, playerChar);
        if (player == 1) {
            newState.setPlayerAPosition(x, y);
        } else {
            newState.setPlayerBPosition(x, y);
        }

        return newState;
    }

    public List<BoardState> expand(BoardState state, int player) {
        List<BoardState> children = new ArrayList<>();
        int currentX = (player == 1) ? state.getPlayerAX() : state.getPlayerBX();
        int currentY = (player == 1) ? state.getPlayerAY() : state.getPlayerBY();

        for (Direction direction : Direction.values()) {
            for (int i = 1; i <= MOVE_LIMIT; i++) {
                Pair<MoveResult, int[]> result = isValidMove(state, player, direction, i, currentX, currentY);
                if (result.getFirst() == MoveResult.SUCCESS) {
                    BoardState newState = makeMove(state, player, direction, i, currentX, currentY);
                    children.add(newState);
                }
            }
        }
        return children;
    }

    public int evaluate(BoardState state, int player) {
        if (player == 1) {
            if (expand(state, 1).size() == 0) {
                return -1;
            }
        } else if (player == 2) {
            if (expand(state, 2).size() == 0) {
                return 1;
            }
        }
        return -100;
    }
}