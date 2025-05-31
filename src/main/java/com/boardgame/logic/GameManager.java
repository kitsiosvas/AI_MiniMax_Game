package com.boardgame.logic;

import com.boardgame.io.GameIO;
import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private final GameIO gameIO;
    private int rows;
    private int columns;
    private List<int[]> blackSquares;
    private final GameLogic logic;

    public GameManager(GameIO gameIO) {
        this.gameIO = gameIO;
        this.blackSquares = new ArrayList<>();
        this.logic = new GameLogic();
    }

    public void startGame() {
        gameIO.displayMessage("== Java program started ==");
        BoardState state = setupGame();
        do {
            runGameLoop(state);
            if (!gameIO.promptPlayAgain()) {
                break;
            }
            state = promptPlayerPositionsAndInitBoard();
        } while (true);
    }

    private BoardState setupGame() {
        int[] size = gameIO.promptBoardSize();
        rows = size[0];
        columns = size[1];
        blackSquares = gameIO.promptBlackSquares(rows, columns);
        return promptPlayerPositionsAndInitBoard();
    }

    private BoardState promptPlayerPositionsAndInitBoard() {
        int[][] positions = gameIO.promptPlayerPositions(rows, columns);
        char[][] board = new char[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                board[i][j] = ' ';
            }
        }
        for (int[] pos : blackSquares) {
            board[pos[0]][pos[1]] = '*';
        }
        board[positions[0][0]][positions[0][1]] = 'A';
        board[positions[1][0]][positions[1][1]] = 'B';
        return new BoardState(board);
    }

    private void runGameLoop(BoardState currentState) {
        BoardState bestState = new BoardState(rows, columns);
        int evaluationResult;

        gameIO.displayMessage("STARTING POSITION:");
        gameIO.displayBoard(currentState);

        do {
            // AI move
            gameIO.displayMessage("Calculating my move...");
            int minimaxScore = minimax(currentState, 10, true, bestState);
            currentState = bestState;
            gameIO.displayBoard(currentState);
            evaluationResult = logic.evaluate(currentState, 2);
            if (evaluationResult != -100) {
                break;
            }

            // Human move
            Pair<Direction, Integer> move = gameIO.promptPlayerMove();
            Direction direction = move.getFirst();
            int length = move.getSecond();
            Pair<MoveResult, int[]> result = logic.isValidMove(
                currentState, 2, direction, length, currentState.getPlayerBX(), currentState.getPlayerBY()
            );
            if (result.getFirst() != MoveResult.SUCCESS) {
                int failureY = result.getSecond()[0];
                int failureX = result.getSecond()[1];
                gameIO.displayMoveError(result.getFirst(), direction, length, failureY, failureX);
                evaluationResult = logic.evaluate(currentState, 1);
                break;
            }
            currentState = logic.makeMove(
                currentState, 2, direction, length, currentState.getPlayerBX(), currentState.getPlayerBY()
            );
            gameIO.displayBoard(currentState);
            evaluationResult = logic.evaluate(currentState, 1);
        } while (evaluationResult == -100);

        // Print game result
        if (evaluationResult == 1) {
            gameIO.displayMessage("I win!");
        } else if (evaluationResult == 0) {
            gameIO.displayMessage("Tie!");
        } else if (evaluationResult == -1) {
            gameIO.displayMessage("You win!");
        }
    }

    private int minimax(BoardState state, int depth, boolean isMax, BoardState bestState) {
        int evaluationResult = logic.evaluate(state, isMax ? 1 : 2);

        if (depth == 0 || evaluationResult != -100) {
            bestState.setBoard(state.getBoard());
            return evaluationResult;
        }

        int maxScore, tempScore;
        List<BoardState> children = logic.expand(state, isMax ? 1 : 2);
        if (children.isEmpty()) {
            bestState.setBoard(state.getBoard());
            return evaluationResult;
        }

        maxScore = minimax(children.get(0), depth - 1, !isMax, bestState);

        for (int i = 1; i < children.size(); i++) {
            BoardState tempState = new BoardState(state.getRows(), state.getColumns());
            tempScore = minimax(children.get(i), depth - 1, !isMax, tempState);
            if ((tempScore > maxScore) == isMax) {
                maxScore = tempScore;
                bestState.setBoard(children.get(i).getBoard());
            }
        }
        return maxScore;
    }
}