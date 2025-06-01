package com.boardgame.ui;

import com.boardgame.logic.BoardState;
import com.boardgame.logic.Direction;
import com.boardgame.logic.Pair;
import com.boardgame.ui.MessageArea;
import javafx.scene.layout.GridPane;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IBoardUI {
    void renderBoard(BoardState state);
    CompletableFuture<List<int[]>> promptBlackSquares();
    CompletableFuture<int[][]> promptPlayerPositions();
    void highlightCells(List<int[]> positions);
    void clearBoard();
    CompletableFuture<Pair<Direction, Integer>> promptPlayerMove();
}