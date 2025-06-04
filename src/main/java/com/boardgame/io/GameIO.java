package com.boardgame.io;

import java.util.concurrent.CompletableFuture;
import com.boardgame.logic.BoardState;
import com.boardgame.logic.Direction;
import com.boardgame.logic.MoveResult;
import com.boardgame.logic.Pair;
import com.boardgame.ui.MessageArea;
import java.util.List;
import java.util.function.Consumer;

public interface GameIO {
    void promptBoardSize(Consumer<int[]> callback);
    void promptBlackSquares(int rows, int columns, Consumer<List<int[]>> callback);
    void promptPlayerPositions(int rows, int columns, Consumer<int[][]> callback);
    Pair<Direction, Integer> promptPlayerMove();
    boolean promptPlayAgain();
    void displayMessage(String message);
    void displayBoard(BoardState state);
    void displayMoveError(MoveResult result, Direction direction, int length, int failureY, int failureX);
    CompletableFuture<Void> displayGameEndMessage(String message, MessageArea.MessageType type);
    CompletableFuture<Void> displayGameFinished(int evaluationResult);
    CompletableFuture<Void> displayGameEndError(MoveResult result, Direction direction, int length, int failureY, int failureX);
    void reset();
}