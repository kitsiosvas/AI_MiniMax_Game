package com.boardgame.io;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import com.boardgame.logic.BoardState;
import com.boardgame.logic.Direction;
import com.boardgame.logic.GameLogic;
import com.boardgame.logic.MoveResult;
import com.boardgame.logic.Pair;

public class ConsoleGameIO implements GameIO {
    private final Scanner scanner;

    public ConsoleGameIO(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public int[] promptBoardSize() {
        int rows = 0, columns = 0;
        boolean validInput = false;
        while (!validInput) {
            try {
                displayMessage("Give number of rows and columns: ");
                displayMessage("NOTE! For board size larger than 4x4 " +
                        "the program might take a while to calculate the first move.");
                rows = scanner.nextInt();
                columns = scanner.nextInt();
                if (rows <= 0 || columns <= 0) {
                    displayMessage("Rows and columns must be positive.");
                    continue;
                }
                validInput = true;
            } catch (InputMismatchException e) {
                displayMessage("Invalid input: Please enter integers for rows and columns.");
                scanner.nextLine();
            }
        }
        return new int[]{rows, columns};
    }

    @Override
    public List<int[]> promptBlackSquares(int rows, int columns) {
        List<int[]> blackSquares = new ArrayList<>();
        displayMessage("Do you want to set any black squares on the board? Type \"yes\" for positive or anything else for negative ");
        String setBlackSquares = scanner.next();
        while (setBlackSquares.equals("yes")) {
            try {
                displayMessage("Give i and j of the black square");
                int row = scanner.nextInt();
                int col = scanner.nextInt();
                if (col >= 0 && col < columns && row >= 0 && row < rows) {
                    blackSquares.add(new int[]{row, col});
                } else {
                    displayMessage("Invalid coordinates: Must be within board bounds.");
                }
                displayMessage("Do you want to set another square black?");
                setBlackSquares = scanner.next();
            } catch (InputMismatchException e) {
                displayMessage("Invalid input: Please enter integers for coordinates.");
                scanner.nextLine();
                setBlackSquares = scanner.next();
            }
        }
        return blackSquares;
    }

    @Override
    public int[][] promptPlayerPositions(int rows, int columns) {
        int playerAX = -1, playerAY = -1, playerBX = -1, playerBY = -1;
        boolean invalidInput;
        do {
            try {
                displayMessage("Give i and j of player A (Computer)");
                playerAY = scanner.nextInt();
                playerAX = scanner.nextInt();
                displayMessage("Give i and j of player B (You)");
                playerBY = scanner.nextInt();
                playerBX = scanner.nextInt();
                invalidInput = !(playerAX >= 0 && playerAY >= 0 && playerBX >= 0 && playerBY >= 0 &&
                        playerAY < rows && playerBY < rows && playerAX < columns && playerBX < columns);
                if (invalidInput) {
                    displayMessage("Wrong input for at least one of the players. Try again:");
                } else if (playerAX == playerBX && playerAY == playerBY) {
                    displayMessage("Players cannot occupy the same position. Try again:");
                    invalidInput = true;
                }
            } catch (InputMismatchException e) {
                displayMessage("Invalid input: Please enter integers for coordinates.");
                scanner.nextLine();
                invalidInput = true;
            }
        } while (invalidInput);
        return new int[][]{{playerAY, playerAX}, {playerBY, playerBX}};
    }

    @Override
    public Pair<Direction, Integer> promptPlayerMove() {
        Direction moveDirection = null;
        int moveLength = 0;
        boolean validInput = false;
        while (!validInput) {
            try {
                displayMessage("Give direction and move length of your move: (e.g \"up_right 2\" to go up and then right 2 squares) ");
                String directionInput = scanner.next();
                moveDirection = Direction.fromString(directionInput);
                moveLength = scanner.nextInt();
                if (moveDirection == null || moveLength <= 0 || moveLength > GameLogic.MOVE_LIMIT) {
                    displayMessage("Invalid direction or length. Try again.");
                    continue;
                }
                validInput = true;
            } catch (InputMismatchException e) {
                displayMessage("Invalid input: Please enter a valid direction and integer length.");
                scanner.nextLine();
            }
        }
        return new Pair<>(moveDirection, moveLength);
    }

    @Override
    public boolean promptPlayAgain() {
        displayMessage("Play another game? (yes/no)");
        String response = scanner.next();
        return response.equalsIgnoreCase("yes");
    }

    @Override
    public void displayMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void displayBoard(BoardState state) {
        state.print();
    }

    @Override
    public void displayMoveError(MoveResult result, Direction direction, int length, int failureY, int failureX) {
        System.out.printf("Cannot move %s %d: %s %s (%d,%d). Game ended.%n",
                direction.toString().toLowerCase(), length,
                result.getMessage(), result.getPreposition(), failureY, failureX);
    }
}