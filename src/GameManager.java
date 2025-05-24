import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class GameManager {
    private final Scanner scanner;
    private int rows;
    private int columns;
    private List<int[]> blackSquares; // Stores [row, col] for black squares
    private final GameLogic logic;

    public GameManager(Scanner scanner) {
        this.scanner = scanner;
        this.blackSquares = new ArrayList<>();
        this.logic = new GameLogic();
    }

    public void startGame() {
        System.out.println("== Java program started ==");
        BoardState state = setupGame(); // Use BoardState from setupGame for first game
        do {
            runGameLoop(state);
            if (!promptPlayAgain()) {
                break;
            }
            state = promptPlayerPositionsAndInitBoard(); // New state for subsequent games
        } while (true);
    }

    private BoardState setupGame() {
        boolean validInput = false;
        while (!validInput) {
            try {
                System.out.println("Give number of rows and columns: ");
                System.out.println("NOTE! For board size larger than 4x4 " +
                        "the program might take a while to calculate the first move.");
                rows = scanner.nextInt();
                columns = scanner.nextInt();
                if (rows <= 0 || columns <= 0) {
                    System.out.println("Rows and columns must be positive.");
                    continue;
                }
                validInput = true;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input: Please enter integers for rows and columns.");
                scanner.nextLine();
            }
        }

        // Initialize blackSquares list
        blackSquares.clear();
        // Prompt for black squares
        System.out.println("Do you want to set any black squares on the board? Type \"yes\" for positive or anything else for negative ");
        String setBlackSquares = scanner.next();
        while (setBlackSquares.equals("yes")) {
            try {
                System.out.println("Give i and j of the black square");
                int row = scanner.nextInt();
                int col = scanner.nextInt();
                if (col >= 0 && col < columns && row >= 0 && row < rows) {
                    blackSquares.add(new int[]{row, col});
                } else {
                    System.out.println("Invalid coordinates: Must be within board bounds.");
                }
                System.out.println("Do you want to set another square black?");
                setBlackSquares = scanner.next();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input: Please enter integers for coordinates.");
                scanner.nextLine();
                setBlackSquares = scanner.next();
            }
        }

        return promptPlayerPositionsAndInitBoard();
    }

    private BoardState promptPlayerPositionsAndInitBoard() {
        // Create new board with stored size
        char[][] board = new char[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                board[i][j] = ' ';
            }
        }
        // Apply stored black squares
        for (int[] pos : blackSquares) {
            board[pos[0]][pos[1]] = '*';
        }

        // Prompt for player positions
        int playerAX = -1, playerAY = -1, playerBX = -1, playerBY = -1;
        boolean invalidInput;
        do {
            try {
                System.out.println("Give i and j of player A (Computer)");
                playerAY = scanner.nextInt();
                playerAX = scanner.nextInt();
                System.out.println("Give i and j of player B (You)");
                playerBY = scanner.nextInt();
                playerBX = scanner.nextInt();
                invalidInput = !(playerAX >= 0 && playerAY >= 0 && playerBX >= 0 && playerBY >= 0 &&
                        playerAY < rows && playerBY < rows && playerAX < columns && playerBX < columns);
                if (invalidInput) {
                    System.out.println("Wrong input for at least one of the players. Try again:");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input: Please enter integers for coordinates.");
                scanner.nextLine();
                invalidInput = true;
            }
        } while (invalidInput);

        // Set player positions
        board[playerAY][playerAX] = 'A';
        board[playerBY][playerBX] = 'B';

        return new BoardState(board);
    }

    private void runGameLoop(BoardState currentState) {
        BoardState bestState = new BoardState(rows, columns);
        int evaluationResult;

        System.out.println("STARTING POSITION:");
        currentState.print();

        do {
            // AI move
            System.out.println("Calculating my move...");
            int minimaxScore = minimax(currentState, 10, true, bestState);
            currentState = bestState;
            currentState.print();
            evaluationResult = logic.evaluate(currentState, 2);
            if (evaluationResult != -100) {
                break;
            }

            // Human move
            BoardState tempState = new BoardState(currentState.getBoard());
            MoveResult moveResult = processPlayerMove(currentState, tempState);
            if (moveResult != MoveResult.SUCCESS) {
                int failureX = tempState.getFailureX();
                int failureY = tempState.getFailureY();
                System.out.printf("Cannot move %s %d: %s %s (%d,%d). Game ended.%n",
                        tempState.getMoveDirection().toString().toLowerCase(),
                        tempState.getMoveLength(),
                        moveResult.getMessage(),
                        moveResult.getPreposition(),
                        failureY,
                        failureX);
                evaluationResult = logic.evaluate(currentState, 1);
                break;
            }
            currentState = tempState;
            currentState.print();
            evaluationResult = logic.evaluate(currentState, 1);
        } while (evaluationResult == -100);

        // Print game result
        if (evaluationResult == 1) {
            System.out.println("I win!");
        } else if (evaluationResult == 0) {
            System.out.println("Tie!");
        } else if (evaluationResult == -1) {
            System.out.println("You win!");
        }
    }

    private MoveResult processPlayerMove(BoardState currentState, BoardState tempState) {
        Direction moveDirection = null;
        int moveLength = 0;
        boolean validInput = false;

        while (!validInput) {
            try {
                System.out.println("Give direction and move length of your move: (e.g \"up_right 2\" to go up and then right 2 squares) ");
                String directionInput = scanner.next();
                moveDirection = Direction.fromString(directionInput);
                moveLength = scanner.nextInt();
                if (moveDirection == null || moveLength <= 0 || moveLength > GameLogic.MOVE_LIMIT) {
                    System.out.println("Invalid direction or length. Try again.");
                    continue;
                }
                validInput = true;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input: Please enter a valid direction and integer length.");
                scanner.nextLine();
            }
        }

        // Store move details in tempState for error reporting
        tempState.setMoveDirection(moveDirection);
        tempState.setMoveLength(moveLength);

        return logic.makeMove(currentState, tempState, 2, moveDirection, moveLength,
                currentState.getPlayerBX(), currentState.getPlayerBY());
    }

    private boolean promptPlayAgain() {
        System.out.println("Play another game? (yes/no)");
        String response = scanner.next();
        return response.equalsIgnoreCase("yes");
    }

    private int minimax(BoardState state, int depth, boolean isMax, BoardState bestState) {
        int evaluationResult = logic.evaluate(state, isMax ? 1 : 2);

        if (depth == 0 || evaluationResult != -100) {
            bestState.setBoard(state.getBoard());
            return evaluationResult;
        }

        int maxScore, tempScore;
        BoardState maxState = new BoardState(state.getRows(), state.getColumns());
        BoardState tempState = new BoardState(state.getRows(), state.getColumns());

        List<BoardState> children = logic.expand(state, isMax ? 1 : 2);

        maxScore = minimax(children.get(0), depth - 1, !isMax, maxState);
        maxState = children.get(0);

        for (int i = 1; i < children.size(); i++) {
            tempScore = minimax(children.get(i), depth - 1, !isMax, tempState);
            if ((tempScore > maxScore) == isMax) {
                maxScore = tempScore;
                maxState = children.get(i);
            }
        }
        bestState.setBoard(maxState.getBoard());
        return maxScore;
    }
}