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
        int evaluationResult;

        gameIO.displayMessage("STARTING POSITION:");
        gameIO.displayBoard(currentState);

        do {
            // Store Player B's position before AI move
            int originalBX = currentState.getPlayerBX();
            int originalBY = currentState.getPlayerBY();

            // AI move
            gameIO.displayMessage("Calculating my move...");
            Pair<Integer, BoardState> result = minimax(currentState, 10, true);
            currentState = result.getSecond();

            // Check if Player B's position changed
            if (currentState.getPlayerBX() != originalBX || currentState.getPlayerBY() != originalBY) {
                gameIO.displayMessage("Error: Player B moved unexpectedly to (" + currentState.getPlayerBX() + "," + currentState.getPlayerBY() + ")");
                // Revert to original state
                currentState = new BoardState(currentState.getRows(), currentState.getColumns());
                currentState.setBoard(result.getSecond().getBoard());
                currentState.setPlayerBPosition(originalBX, originalBY);
                currentState.setBoardCell(originalBY, originalBX, 'B');
            }

            gameIO.displayBoard(currentState);
            evaluationResult = logic.evaluate(currentState, 2);
            if (evaluationResult != -100) {
                break;
            }

            // Human move (unchanged)
            Pair<Direction, Integer> move = gameIO.promptPlayerMove();
            Direction direction = move.getFirst();
            int length = move.getSecond();
            Pair<MoveResult, int[]> moveResult = logic.isValidMove(
                currentState, 2, direction, length, currentState.getPlayerBX(), currentState.getPlayerBY()
            );
            if (moveResult.getFirst() != MoveResult.SUCCESS) {
                int failureY = moveResult.getSecond()[0];
                int failureX = moveResult.getSecond()[1];
                gameIO.displayMoveError(moveResult.getFirst(), direction, length, failureY, failureX);
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

    private Pair<Integer, BoardState> minimax(BoardState state, int depth, boolean isMax) {
        int evaluationResult = logic.evaluate(state, isMax ? 1 : 2);

        if (depth == 0 || evaluationResult != -100) {
            return new Pair<>(evaluationResult, new BoardState(state.getBoard()));
        }

        List<BoardState> children = logic.expand(state, isMax ? 1 : 2);
        if (children.isEmpty()) {
            return new Pair<>(evaluationResult, new BoardState(state.getBoard()));
        }

        int bestScore = isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        BoardState bestChildState = null;
        int originalBX = state.getPlayerBX();
        int originalBY = state.getPlayerBY();

        for (BoardState child : children) {
            Pair<Integer, BoardState> result = minimax(child, depth - 1, !isMax);
            int score = result.getFirst();
            if (isMax) {
                if (score > bestScore) {
                    // Only select states where Player B's position is unchanged
                    if (child.getPlayerBX() == originalBX && child.getPlayerBY() == originalBY) {
                        bestScore = score;
                        bestChildState = new BoardState(child.getBoard());
                    }
                }
            } else {
                if (score < bestScore) {
                    bestScore = score;
                    bestChildState = new BoardState(child.getBoard());
                }
            }
        }

        // Fallback to input state if no valid move found
        if (bestChildState == null) {
            bestChildState = new BoardState(state.getBoard());
        }

        return new Pair<>(bestScore, bestChildState);
    }
}