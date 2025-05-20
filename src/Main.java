import java.util.List;
import java.util.Scanner;
import java.util.InputMismatchException;

public class Main {

    public static int minimax(Game state, int depth, boolean isMax, Game bestState)
    {
        int evaluationResult = state.evaluate(isMax ? 1 : 2);

        if (depth == 0 || evaluationResult != -100)
        {
            bestState.setBoard(state.getBoard());
            return evaluationResult;
        }

        int maxScore, tempScore;
        Game maxState = new Game(state.getRows(), state.getColumns());
        Game tempState = new Game(state.getRows(), state.getColumns());

        List<Game> children = state.expand(isMax ? 1 : 2);

        maxScore = minimax(children.get(0), depth - 1, !isMax, maxState);
        maxState = children.get(0);

        for (int i = 1; i < children.size(); i++)
        {
            tempScore = minimax(children.get(i), depth - 1, !isMax, tempState);
            if ((tempScore > maxScore) == isMax)
            {
                maxScore = tempScore;
                maxState = children.get(i);
            }
        }
        bestState.setBoard(maxState.getBoard());
        return maxScore;
    }

    public static void main(String[] args)
    {
        int rows, columns;
        int playerAX = -1, playerAY = -1, playerBX = -1, playerBY = -1;
        char[][] board;

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("== Java program started ==");

            try {
                System.out.println("Give number of rows and columns: ");
                System.out.println("NOTE! For board size larger than 4x4 " +
                        "the program might take a while to calculate the first move.");
                rows = scanner.nextInt();
                columns = scanner.nextInt();
                if (rows <= 0 || columns <= 0) {
                    System.out.println("Rows and columns must be positive.");
                    return;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input: Please enter integers for rows and columns.");
                return;
            }

            board = new char[rows][columns];

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++)
                    board[i][j] = ' ';
            }

            System.out.println("Do you want to set any black squares on the board? Type \"yes\" for positive or anything else for negative ");
            String setBlackSquares = scanner.next();
            while (setBlackSquares.equals("yes"))
            {
                try {
                    System.out.println("Give i and j of the black square");
                    int row = scanner.nextInt();
                    int col = scanner.nextInt();
                    if (col >= 0 && col < columns && row >= 0 && row < rows) {
                        board[row][col] = '*';
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

            if (playerAX < 0 || playerAY < 0 || playerBX < 0 || playerBY < 0 || 
                playerAX >= columns || playerAY >= rows || playerBX >= columns || playerBY >= rows) {
                System.out.println("Error: Player coordinates not properly set.");
                return;
            }

            board[playerAY][playerAX] = 'A';
            board[playerBY][playerBX] = 'B';

            Game currentState = new Game(board), bestState = new Game(rows, columns);
            int evaluationResult, minimaxScore, moveLength;
            Direction moveDirection;

            System.out.println("STARTING POSITION:");
            currentState.print();

            do {
                System.out.println("Calculating my move...");
                minimaxScore = minimax(currentState, 10, true, bestState);
                currentState = bestState;
                currentState.print();
                evaluationResult = currentState.evaluate(2);
                if (evaluationResult == -100)
                {
                    do {
                        try {
                            System.out.println("Give direction and move length of your move: (e.g \"up_right 2\" to go up and then right 2 squares) ");
                            String directionInput = scanner.next();
                            moveDirection = Direction.fromString(directionInput);
                            moveLength = scanner.nextInt();
                            if (moveDirection == null || moveLength <= 0 || moveLength > Game.move_limit) {
                                System.out.println("Invalid direction or length. Try again.");
                                continue;
                            }
                            break;
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input: Please enter a valid direction and integer length.");
                            scanner.nextLine();
                        }
                    } while (true);

                    Game tempState = new Game(currentState.getBoard());
                    MoveResult moveResult = currentState.makeMove(tempState, 2, moveDirection, moveLength, currentState.getPlayerBX(), currentState.getPlayerBY());
                    if (moveResult != MoveResult.SUCCESS) {
                        int failureX = tempState.getFailureX();
                        int failureY = tempState.getFailureY();
                        System.out.printf("Cannot move %s %d: %s %s (%d,%d). Game ended.%n",
                            moveDirection.toString().toLowerCase(), moveLength,
                            moveResult.getMessage(), moveResult.getPreposition(), failureY, failureX);
                        break;
                    }
                    currentState = tempState;

                    evaluationResult = currentState.evaluate(1);
                    currentState.print();
                }
            } while (evaluationResult == -100);

            if (evaluationResult == 1)
                System.out.println("I win!");
            else if (evaluationResult == 0)
                System.out.println("Tie!");
            else if (evaluationResult == -1)
                System.out.println("You win!");
        }
    }
}