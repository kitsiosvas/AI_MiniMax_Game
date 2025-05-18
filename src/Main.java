import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.InputMismatchException;

public class Main {

    public static int minimax(Game s, int depth, boolean isMax, Game best)
    {
        int k = s.evaluate(isMax ? 1 : 2);

        if (depth == 0 || k != -100)
        {
            best.setBoard(s.getBoard());
            return k;
        }

        int max, temp;
        Game maxState = new Game(s.getRows(), s.getColumns()), tempState = new Game(s.getRows(), s.getColumns());

        List<Game> children = s.expand(isMax ? 1 : 2);

        max = minimax(children.get(0), depth - 1, !isMax, maxState);
        maxState = children.get(0);

        for (int i = 1; i < children.size(); i++)
        {
            temp = minimax(children.get(i), depth - 1, !isMax, tempState);
            if ((temp > max) == isMax)
            {
                max = temp;
                maxState = children.get(i);
            }
        }
        best.setBoard(maxState.getBoard());
        return max;
    }

    public static void main(String[] args)
    {
        int rows, columns;
        int x1 = -1, y1 = -1, x2 = -1, y2 = -1;
        char[][] a;

        try (Scanner sc = new Scanner(System.in)) {
            System.out.println("== Java program started ==");

            try {
                System.out.println("Give number of rows and columns: ");
                System.out.println("NOTE! For board size larger than 4x4 " +
                        "the program might take a while to calculate the first move.");
                rows = sc.nextInt();
                columns = sc.nextInt();
                if (rows <= 0 || columns <= 0) {
                    System.out.println("Rows and columns must be positive.");
                    return;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input: Please enter integers for rows and columns.");
                return;
            }

            a = new char[rows][columns];

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++)
                    a[i][j] = ' ';
            }

            System.out.println("Do you want to set any black squares on the board? Type \"yes\" for positive or anything else for negative ");
            String setBlack = sc.next();
            while (setBlack.equals("yes"))
            {
                try {
                    System.out.println("Give i and j of the black square");
                    int y = sc.nextInt();
                    int x = sc.nextInt();
                    if (x >= 0 && x < columns && y >= 0 && y < rows) {
                        a[y][x] = '*';
                    } else {
                        System.out.println("Invalid coordinates: Must be within board bounds.");
                    }
                    System.out.println("Do you want to set another square black?");
                    setBlack = sc.next();
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input: Please enter integers for coordinates.");
                    sc.nextLine();
                    setBlack = sc.next();
                }
            }

            boolean condition;
            do {
                try {
                    System.out.println("Give i and j of player A (Computer)");
                    y1 = sc.nextInt();
                    x1 = sc.nextInt();

                    System.out.println("Give i and j of player B (You)");
                    y2 = sc.nextInt();
                    x2 = sc.nextInt();

                    condition = !(x1 >= 0 && y1 >= 0 && x2 >= 0 && y2 >= 0 && 
                                 y1 < rows && y2 < rows && x1 < columns && x2 < columns);
                    if (condition) {
                        System.out.println("Wrong input for at least one of the players. Try again:");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input: Please enter integers for coordinates.");
                    sc.nextLine();
                    condition = true;
                }
            } while (condition);

            if (x1 < 0 || y1 < 0 || x2 < 0 || y2 < 0 || 
                x1 >= columns || y1 >= rows || x2 >= columns || y2 >= rows) {
                System.out.println("Error: Player coordinates not properly set.");
                return;
            }

            a[y1][x1] = 'A';
            a[y2][x2] = 'B';

            Game start = new Game(a), best = new Game(rows, columns);
            int v, exp, len;
            Direction dir;

            System.out.println("STARTING POSITION:");
            start.print();

            do {
                System.out.println("Calculating my move...");
                exp = minimax(start, 10, true, best);
                start = best;
                start.print();
                v = start.evaluate(2);
                if (v == -100)
                {
                    do {
                        try {
                            System.out.println("Give direction and move length of your move: (e.g \"up_right 2\" to go up and then right 2 squares) ");
                            String dirInput = sc.next();
                            dir = Direction.fromString(dirInput);
                            len = sc.nextInt();
                            if (dir == null || len <= 0 || len > Game.move_limit) {
                                System.out.println("Invalid direction or length. Try again.");
                                continue;
                            }
                            break;
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input: Please enter a valid direction and integer length.");
                            sc.nextLine();
                        }
                    } while (true);

                    if (!start.move(start, 2, dir, len, start.getB_x(), start.getB_y()))
                    {
                        System.out.println("Invalid move. Game ended.");
                        break;
                    }

                    v = start.evaluate(1);
                    start.print();
                }
            } while (v == -100);

            if (v == 1)
                System.out.println("I win!");
            else if (v == 0)
                System.out.println("Tie!");
            else if (v == -1)
                System.out.println("You win!");
        }
    }
}