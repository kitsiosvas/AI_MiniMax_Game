/*
Για το παιχνίδι έχει γίνει η υπόθεση ότι τα πιόνια μπορούν να κάνουν κινήσεις με μέγιστο μήκος 2.
Για να αλλάξει αυτό θα πρέπει να αλλάξει η μεταβλητή move_limit της κλάσης Game.java .


Διαθέσιμες κινήσεις(όπου i είναι το μήκος, δηλαδη πρέπει 0<i<move_limit):
up i
down i
left i
right i
up_right i
up_left i
down_right i
down_left i
 */

import java.util.Scanner;
import java.util.Vector;

public class Main {

    public static int minimax(Game s, int depth, boolean isMax, Game best)
    {
        int k = s.evaluate(isMax?1:2);

        if (depth==0 || k!=-100)
        {
            best.setBoard(s.getBoard());
            return k;
        }

        int max, temp;
        Game maxState = new Game(), tempState = new Game();

        Vector<Game> children = s.expand(isMax?1:2);

        max = minimax(children.elementAt(0), depth-1, !isMax, maxState);
        maxState = children.elementAt(0);

        for (int i=1;i<children.size();i++)
        {
            temp = minimax(children.elementAt(i), depth-1, !isMax, tempState);
            if ((temp>max)==isMax)
            {
                max = temp;
                maxState = children.elementAt(i);
            }
        }
        best.setBoard(maxState.getBoard());
        return max;
    }



    public static void main(String[] args)
    {
        int rows, columns;
        int x1, y1, x2, y2;
        char[][] a;
        Scanner sc = new Scanner(System.in);

        System.out.println("Give number of rows and columns: ");
        System.out.println("NOTE! For board size larger than 4x4 " +
                "the program might take a while to calculate the first move.");
        rows = sc.nextInt(); columns = sc.nextInt();
        Game.rows = rows; Game.columns = columns;
        a = new char[rows][columns];

        for (int i=0;i<rows;i++) {
            for (int j = 0; j < columns; j++)
                a[i][j] = ' ';
        }

        System.out.println("Do you want to set any black squares on the board? Type \"yes\" for positive or anything else for negative ");
        String setBlack;
        int x, y;
        setBlack = sc.next();
        while (setBlack.equals("yes"))
        {
            System.out.println("Give i and j of the black square");
            y = sc.nextInt(); x = sc.nextInt();
            a[y][x] = '*';
            System.out.println("Do you want to set another square black?");
            setBlack = sc.next();
        }
        boolean condition;
        do {
            System.out.println("Give i and j of player A");
            y1 = sc.nextInt(); x1 = sc.nextInt();

            System.out.println("Give i and j of player B");
            y2 = sc.nextInt(); x2 = sc.nextInt();

            condition = !(x1>=0 & y1>=0 & x2>=0 & y2>=0 & y1<rows & y2<rows & x1<columns & x2<columns);
            if (condition)
            {
                System.out.println("Wrong input for at least one of the players. Try again:");
            }
        }while (condition);
        a[y1][x1] = 'A'; a[y2][x2] = 'B';

        Game start = new Game(a), best = new Game();
        int v, exp, len;
        String dir;

        System.out.println("STARTING POSITION:");start.print();


        do {
            System.out.println("Calculating my move...");
            exp = minimax(start, 10, true, best);
            start = best;
            start.print();
            v = start.evaluate(2);
            if (v==-100)
            {
                do {
                    System.out.println("Give direction and move length of your move: (e.g \"up_right 2\" to go up and then right 2 squares) ");
                    dir = sc.next();
                    len = sc.nextInt();
                } while (!( (dir.equals("up") || dir.equals("right") || dir.equals("down") || dir.equals("left")
                        || dir.equals("up_left") || dir.equals("up_right") || dir.equals("down_left") || dir.equals("down_right"))
                        & len>0 & len<=Game.move_limit) );

                if (dir.equals("up"))
                {
                    if (!start.goUp(start, 2, len, start.getB_x(), start.getB_y()))
                    {
                        System.out.println("Invalid move. Game ended.");
                        break;
                    }
                }
                else if (dir.equals("right"))
                {
                    if (!start.goRight(start, 2, len, start.getB_x(), start.getB_y()))
                    {
                        System.out.println("Invalid move. Game ended");
                        break;
                    }
                }
                else if (dir.equals("down"))
                {
                    if (!start.goDown(start, 2, len, start.getB_x(), start.getB_y()))
                    {
                        System.out.println("Invalid move. Game ended");
                        break;
                    }
                }
                else if (dir.equals("left"))
                {
                    if (!start.goLeft(start, 2, len, start.getB_x(), start.getB_y()))
                    {
                        System.out.println("Invalid move. Game ended");
                        break;
                    }
                }
                else if (dir.equals("up_left"))
                {
                    if (!start.goUp_Left(start, 2, len, start.getB_x(), start.getB_y()))
                    {
                        System.out.println("Invalid move. Game ended");
                        break;
                    }
                }
                else if (dir.equals("up_right"))
                {
                    if (!start.goUp_Right(start, 2, len, start.getB_x(), start.getB_y()))
                    {
                        System.out.println("Invalid move. Game ended");
                        break;
                    }
                }
                else if (dir.equals("down_left"))
                {
                    if (!start.goDown_Left(start, 2, len, start.getB_x(), start.getB_y()))
                    {
                        System.out.println("Invalid move. Game ended");
                        break;
                    }
                }
                else if (dir.equals("down_right"))
                {
                    if (!start.goDown_Right(start, 2, len, start.getB_x(), start.getB_y()))
                    {
                        System.out.println("Invalid move. Game ended");
                        break;
                    }
                }
                else System.out.println("Invalid move");
                v = start.evaluate(1);
                start.print();
            }

        }while (v==-100);
        if (v==1)
            System.out.println("I win!");
        else if (v==0)
            System.out.println("Tie!");
        else if (v==-1)
            System.out.println("You win!");
    }
}
