package com.boardgame.io;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import com.boardgame.ui.MessageArea;
import com.boardgame.logic.BoardState;
import com.boardgame.logic.Direction;
import com.boardgame.logic.MoveResult;
import com.boardgame.logic.Pair;

public interface GameIO {
    int[] promptBoardSize();
    List<int[]> promptBlackSquares(int rows, int columns);
    int[][] promptPlayerPositions(int rows, int columns);
    Pair<Direction, Integer> promptPlayerMove();
    boolean promptPlayAgain();
    void displayMessage(String message);
    void displayBoard(BoardState state);
    void displayMoveError(MoveResult result, Direction direction, int length, int failureY, int failureX);
    CompletableFuture<Void> displayGameEndMessage(String message, MessageArea.MessageType type);
    CompletableFuture<Void> displayGameFinished(int evaluationResult);
    CompletableFuture<Void> displayGameEndError(MoveResult result, Direction direction, int length, int failureY, int failureX);
}