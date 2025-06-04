package com.boardgame.logic;

import com.boardgame.io.GameIO;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;


public class GameManager {
    private final GameIO gameIO;
    private int rows;
    private int columns;
    private List<int[]> blackSquares;
    private final GameLogic logic;
    private final AtomicBoolean isGameCancelled;
    private ScreenState currentScreenState;
    private BoardState currentBoardState;

    public enum ScreenState {
        WELCOME, SETUP_BOARD, SETUP_BLACK_SQUARES, SETUP_PLAYERS, PLAYING, GAME_OVER
    }

    public GameManager(GameIO gameIO) {
        this.gameIO = gameIO;
        this.blackSquares = new ArrayList<>();
        this.logic = new GameLogic();
        this.isGameCancelled = new AtomicBoolean(false);
        reset(true);
    }

    public void reset(boolean toWelcome) {
        isGameCancelled.set(false);
        rows = 0;
        columns = 0;
        blackSquares.clear();
        currentScreenState = toWelcome ? ScreenState.WELCOME : ScreenState.SETUP_BOARD;
        currentBoardState = null;
        gameIO.reset();
    }

    public AtomicBoolean getIsGameCancelled() {
        return isGameCancelled;
    }

    public void startNewGame() {
        if (isGameCancelled.get()) return;
        reset(false);
        gameIO.displayMessage("== Java program started ==");
        setupBoardSize();
    }

    private void setupBoardSize() {
        if (isGameCancelled.get()) return;
        currentScreenState = ScreenState.SETUP_BOARD;
        gameIO.promptBoardSize(size -> {
            if (isGameCancelled.get() || size == null) return;
            rows = size[0];
            columns = size[1];
            setupBlackSquares();
        });
    }

    private void setupBlackSquares() {
        if (isGameCancelled.get()) return;
        currentScreenState = ScreenState.SETUP_BLACK_SQUARES;
        gameIO.promptBlackSquares(rows, columns, squares -> {
            if (isGameCancelled.get() || squares == null) return;
            blackSquares = squares;
            setupPlayerPositions();
        });
    }

    private void setupPlayerPositions() {
        if (isGameCancelled.get()) return;
        currentScreenState = ScreenState.SETUP_PLAYERS;
        promptPlayerPositionsAndInitBoard(boardState -> {
            if (isGameCancelled.get() || boardState == null) return;
            currentBoardState = boardState;
            currentScreenState = ScreenState.PLAYING;
            runGameLoop(currentBoardState);
        });
    }

    private void promptPlayerPositionsAndInitBoard(Consumer<BoardState> callback) {
        if (isGameCancelled.get()) {
            callback.accept(null);
            return;
        }
        gameIO.promptPlayerPositions(rows, columns, positions -> {
            if (isGameCancelled.get() || positions == null) {
                callback.accept(null);
                return;
            }
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
            callback.accept(new BoardState(board));
        });
    }

    private void runGameLoop(BoardState boardState) {
        if (boardState == null || isGameCancelled.get()) return;
        int evaluationResult;

        gameIO.displayMessage("STARTING POSITION:");
        gameIO.displayBoard(boardState);

        do {
            if (isGameCancelled.get()) return;

            // AI move
            gameIO.displayMessage("Calculating my move...");
            Pair<Integer, BoardState> result = minimax(boardState, 10, true);
            if (isGameCancelled.get()) return;
            boardState = result.getSecond();
            currentBoardState = boardState; // Update currentBoardState
            gameIO.displayBoard(boardState);
            evaluationResult = logic.evaluate(boardState, 2);
            if (evaluationResult != -100) {
                currentScreenState = ScreenState.GAME_OVER; // Updated to ScreenState
                gameIO.displayGameFinished(evaluationResult).join();
                break;
            }

            // Human move
            Pair<Direction, Integer> move = gameIO.promptPlayerMove();
            if (isGameCancelled.get()) return;
            Direction direction = move.getFirst();
            int length = move.getSecond();
            Pair<MoveResult, int[]> moveResult = logic.isValidMove(
                boardState, 2, direction, length, boardState.getPlayerBX(), boardState.getPlayerBY()
            );
            if (moveResult.getFirst() != MoveResult.SUCCESS) {
                int failureY = moveResult.getSecond()[0];
                int failureX = moveResult.getSecond()[1];
                currentScreenState = ScreenState.GAME_OVER; // Updated to ScreenState
                gameIO.displayGameEndError(moveResult.getFirst(), direction, length, failureY, failureX).join();
                evaluationResult = logic.evaluate(boardState, 1);
                break;
            }
            boardState = logic.makeMove(
                boardState, 2, direction, length, boardState.getPlayerBX(), boardState.getPlayerBY()
            );
            currentBoardState = boardState; // Update currentBoardState
            gameIO.displayBoard(boardState);
            evaluationResult = logic.evaluate(boardState, 1);
            if (evaluationResult != -100) {
                currentScreenState = ScreenState.GAME_OVER; // Updated to ScreenState
                gameIO.displayGameFinished(evaluationResult).join();
                break;
            }
        } while (evaluationResult == -100);
    }

    private Pair<Integer, BoardState> minimax(BoardState state, int depth, boolean isMax) {
        if (isGameCancelled.get()) {
            return new Pair<>(Integer.MIN_VALUE, new BoardState(state.getBoard()));
        }

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
            if (isGameCancelled.get()) {
                return new Pair<>(Integer.MIN_VALUE, new BoardState(state.getBoard()));
            }
            int score = result.getFirst();
            if (isMax) {
                if (score > bestScore) {
                    bestScore = score;
                    bestChildState = new BoardState(child.getBoard());
                }
            } else {
                if (score < bestScore) {
                    bestScore = score;
                    bestChildState = new BoardState(child.getBoard());
                }
            }
        }

        if (bestChildState == null) {
            bestChildState = new BoardState(state.getBoard());
        }

        return new Pair<>(bestScore, bestChildState);
    }
}