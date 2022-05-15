import java.security.PublicKey;
import java.util.Vector;

public class Game {
    public static int move_limit = 2;
    public static int rows,columns;
    private char[][] board = new char[rows][columns];
    private int A_x, A_y, B_x, B_y;


    public void print(){
        System.out.println("----------------------");
        for(int i=0;i<rows;i++)
        {
            System.out.print('|');
            for (int j=0; j<columns; j++)
            {
                System.out.print(Character.toString(board[i][j]) + '|');
            }
            System.out.println();
        }
        System.out.println("----------------------");
    }

    public Game(){
        for (int i=0;i<rows;i++)
            for (int j=0;j<columns;j++)
                board[i][j] = ' ';
    }
    public Game(char[][] a)
    {
        for (int i=0; i<rows; i++) {
            for (int j = 0; j < columns; j++) {
                board[i][j] = (char) a[i][j];
                if (board[i][j] == 'A')
                {
                    A_x = j; A_y = i;
                }
                if (board[i][j] == 'B')
                {
                    B_x = j; B_y = i;
                }
            }
        }
    }

    public int getRows() { return rows; }
    public void setRows(int r) { rows = r; }
    public int getColumns() { return columns; }
    public void setColumns(int c) { columns = c; }

    public boolean setCell(int i, int j, char value)
    {
        board[i][j] = value;
        return true;
    }

    public char getCell(int i, int j) { return board[i][j]; }

    public char[][] getBoard() { return board; }
    public void setBoard(char a[][])
    {
        for (int i=0; i<rows; i++) {
            for (int j = 0; j < columns; j++) {
                board[i][j] = (char) a[i][j];
                if (board[i][j] == 'A')
                {
                    A_x = j; A_y = i;
                }
                if (board[i][j] == 'B')
                {
                    B_x = j; B_y = i;
                }
            }
        }
    }

    public int getA_x() { return A_x; }
    public int getA_y() { return A_y; }
    public int getB_x() { return B_x; }
    public int getB_y() { return B_y; }
    void setPlayer1_positions(int x, int y) { A_x = x; A_y = y; }
    void setPlayer2_positions(int x, int y) { B_x = x; B_y = y; }


    public boolean goUp(Game child, int player, int squares, int x, int y)
    {
        if (y<0 || x<0 || x>=columns || y>=rows) return false;
        char  c;
        if (player==1) c = 'A';
        else c = 'B';

        if (board[y][x]!=' ' & board[y][x]!=c) {
            return false;
        }

        if (squares==0)
        {
            for (int i=0;i<rows;i++)
            {
                for (int j=0; j<columns;j++)
                {
                    child.setCell(i,j, board[i][j]);
                }
            }
            if (player==1)
            {
                child.setPlayer2_positions(this.getB_x(), this.getB_y());
                 child.setPlayer1_positions(x, y);
                 child.setCell(y, x, 'A');
            }
            else
            {
                child.setPlayer1_positions(this.getA_x(), this.getA_y());
                child.setPlayer2_positions(x, y);
                child.setCell(y, x, 'B');
            }
            return true;
        }

        if (goUp(child, player, squares-1, x, y-1))
        {
            child.setCell(y,x,'*');
            return true;
        }
        return false;
    }
    public boolean goDown(Game child, int player, int squares, int x, int y)
    {
        if (y<0 || x<0 || x>=columns || y>=rows) return false;
        char  c;
        if (player==1) c = 'A';
        else c = 'B';

        if (board[y][x]!=' ' & board[y][x]!=c) {
            return false;
        }

        if (squares==0)
        {
            for (int i=0;i<rows;i++)
            {
                for (int j=0; j<columns;j++)
                {
                    child.setCell(i,j, board[i][j]);
                }
            }
            if (player==1)
            {
                child.setPlayer2_positions(this.getB_x(), this.getB_y());
                child.setPlayer1_positions(x, y);
                child.setCell(y, x, 'A');
            }
            else
            {
                child.setPlayer1_positions(this.getA_x(), this.getA_y());
                child.setPlayer2_positions(x, y);
                child.setCell(y, x, 'B');
            }
            return true;
        }

        if (goDown(child, player, squares-1, x, y+1))
        {
            child.setCell(y,x,'*');
            return true;
        }
        return false;
    }
    public boolean goRight(Game child, int player, int squares, int x, int y)
    {
        if (y<0 || x<0 || x>=columns || y>=rows) return false;
        char  c;
        if (player==1) c = 'A';
        else c = 'B';

        if (board[y][x]!=' ' & board[y][x]!=c) {
            return false;
        }

        if (squares==0)
        {
            for (int i=0;i<rows;i++)
            {
                for (int j=0; j<columns;j++)
                {
                    child.setCell(i,j, board[i][j]);
                }
            }
            if (player==1)
            {
                child.setPlayer2_positions(this.getB_x(), this.getB_y());
                child.setPlayer1_positions(x, y);
                child.setCell(y, x, 'A');
            }
            else
            {
                child.setPlayer1_positions(this.getA_x(), this.getA_y());
                child.setPlayer2_positions(x, y);
                child.setCell(y, x, 'B');
            }
            return true;
        }

        if (goRight(child, player, squares-1, x+1, y))
        {
            child.setCell(y,x,'*');
            return true;
        }
        return false;
    }
    public boolean goLeft(Game child, int player, int squares, int x, int y)
    {
        if (y<0 || x<0 || x>=columns || y>=rows) return false;
        char  c;
        if (player==1) c = 'A';
        else c = 'B';

        if (board[y][x]!=' ' & board[y][x]!=c) {
            return false;
        }

        if (squares==0)
        {
            for (int i=0;i<rows;i++)
            {
                for (int j=0; j<columns;j++)
                {
                    child.setCell(i,j, board[i][j]);
                }
            }
            if (player==1)
            {
                child.setPlayer2_positions(this.getB_x(), this.getB_y());
                child.setPlayer1_positions(x, y);
                child.setCell(y, x, 'A');
            }
            else
            {
                child.setPlayer1_positions(this.getA_x(), this.getA_y());
                child.setPlayer2_positions(x, y);
                child.setCell(y, x, 'B');
            }
            return true;
        }
        
        if (goLeft(child, player, squares-1, x-1, y))
        {
            child.setCell(y,x, '*');
            return true;
        }
        return false;
    }
    public boolean goUp_Right(Game child, int player, int squares, int x, int y)
    {
        if (y<0 || x<0 || x>=columns || y>=rows) return false;
        char  c;
        if (player==1) c = 'A';
        else c = 'B';

        if (board[y][x]!=' ' & board[y][x]!=c) {
            return false;
        }

        if (squares==0)
        {
            for (int i=0;i<rows;i++)
            {
                for (int j=0; j<columns;j++)
                {
                    child.setCell(i,j, board[i][j]);
                }
            }
            if (player==1)
            {
                child.setPlayer2_positions(this.getB_x(), this.getB_y());
                child.setPlayer1_positions(x, y);
                child.setCell(y, x, 'A');
            }
            else
            {
                child.setPlayer1_positions(this.getA_x(), this.getA_y());
                child.setPlayer2_positions(x, y);
                child.setCell(y, x, 'B');
            }
            return true;
        }

        if (goUp_Right(child, player, squares-1, x+1, y-1))
        {
            child.setCell(y,x,'*');
            return true;
        }
        return false;
    }
    public boolean goDown_Right(Game child, int player, int squares, int x, int y)
    {
        if (y<0 || x<0 || x>=columns || y>=rows) return false;
        char  c;
        if (player==1) c = 'A';
        else c = 'B';

        if (board[y][x]!=' ' & board[y][x]!=c) {
            return false;
        }

        if (squares==0)
        {
            for (int i=0;i<rows;i++)
            {
                for (int j=0; j<columns;j++)
                {
                    child.setCell(i,j, board[i][j]);
                }
            }
            if (player==1)
            {
                child.setPlayer2_positions(this.getB_x(), this.getB_y());
                child.setPlayer1_positions(x, y);
                child.setCell(y, x, 'A');
            }
            else
            {
                child.setPlayer1_positions(this.getA_x(), this.getA_y());
                child.setPlayer2_positions(x, y);
                child.setCell(y, x, 'B');
            }
            return true;
        }

        if (goDown_Right(child, player, squares-1, x+1, y+1))
        {
            child.setCell(y,x,'*');
            return true;
        }
        return false;
    }
    public boolean goUp_Left(Game child, int player, int squares, int x, int y)
    {
        if (y<0 || x<0 || x>=columns || y>=rows) return false;
        char  c;
        if (player==1) c = 'A';
        else c = 'B';

        if (board[y][x]!=' ' & board[y][x]!=c) {
            return false;
        }

        if (squares==0)
        {
            for (int i=0;i<rows;i++)
            {
                for (int j=0; j<columns;j++)
                {
                    child.setCell(i,j, board[i][j]);
                }
            }
            if (player==1)
            {
                child.setPlayer2_positions(this.getB_x(), this.getB_y());
                child.setPlayer1_positions(x, y);
                child.setCell(y, x, 'A');
            }
            else
            {
                child.setPlayer1_positions(this.getA_x(), this.getA_y());
                child.setPlayer2_positions(x, y);
                child.setCell(y, x, 'B');
            }
            return true;
        }

        if (goUp_Left(child, player, squares-1, x-1, y-1))
        {
            child.setCell(y,x,'*');
            return true;
        }
        return false;
    }
    public boolean goDown_Left(Game child, int player, int squares, int x, int y)
    {
        if (y<0 || x<0 || x>=columns || y>=rows) return false;
        char  c;
        if (player==1) c = 'A';
        else c = 'B';

        if (board[y][x]!=' ' & board[y][x]!=c) {
            return false;
        }

        if (squares==0)
        {
            for (int i=0;i<rows;i++)
            {
                for (int j=0; j<columns;j++)
                {
                    child.setCell(i,j, board[i][j]);
                }
            }
            if (player==1)
            {
                child.setPlayer2_positions(this.getB_x(), this.getB_y());
                child.setPlayer1_positions(x, y);
                child.setCell(y, x, 'A');
            }
            else
            {
                child.setPlayer1_positions(this.getA_x(), this.getA_y());
                child.setPlayer2_positions(x, y);
                child.setCell(y, x, 'B');
            }
            return true;
        }

        if (goDown_Left(child, player, squares-1, x-1, y+1))
        {
            child.setCell(y,x,'*');
            return true;
        }
        return false;
    }

    public Vector<Game> expand(int player)
    {
        Vector<Game> children = new Vector<>();
        int x,y;
        if (player==1)
        {
            x = A_x; y = A_y;
        }
        else
        {
            x = B_x; y = B_y;
        }
        for (int i=1;i<=move_limit;i++)
        {
            Game child = new Game();
            if (goUp(child, player, i, x, y))
            {
                children.add(child);
            }

            child = new Game();
            if (goDown(child, player, i, x, y))
            {
                children.add(child);
            }

            child = new Game();
            if (goLeft(child, player, i, x, y))
            {
                children.add(child);
            }

            child = new Game();
            if (goRight(child, player, i, x, y))
            {
                children.add(child);
            }
            child = new Game();
            if (goUp_Right(child, player, i, x, y))
            {
                children.add(child);
            }

            child = new Game();
            if (goUp_Left(child, player, i, x, y))
            {
                children.add(child);
            }

            child = new Game();
            if (goDown_Right(child, player, i, x, y))
            {
                children.add(child);
            }

            child = new Game();
            if (goDown_Left(child, player, i, x, y))
            {
                children.add(child);
            }
        }

        return children;
    }

    public int evaluate(int player)
    {
        if (player==1)
            if (expand(1).size()==0)
                return -1;
        if (player==2)
            if (expand(2).size()==0)
                return 1;
        return -100;
        /*
        if( (expand(1).size()==0) & (expand(2).size()==0) )
            return 0;
        if (expand(1).size()==0)
            return -1;
        if (expand(2).size()==0)
            return 1;
        return -100;

         */
    }

}
