package com.boardgame.io;
import java.util.List;

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
}