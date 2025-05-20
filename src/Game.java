import java.util.ArrayList;
import java.util.List;

public class Game {
    public static int move_limit = 2;
    private final int rows;
    private final int columns;
    private char[][] board;
    private int playerAX, playerAY, playerBX, playerBY;
    private int failureX, failureY;

    public void print() {
        System.out.print("  ");
        for (int j = 0; j < columns; j++) {
            System.out.print(j + " ");
        }
        System.out.println();

        for (int i = 0; i < rows; i++) {
            System.out.print(i + "|");
            for (int j = 0; j < columns; j++) {
                System.out.print(board[i][j] + "|");
            }
            System.out.println();
        }
    }

    public Game(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        board = new char[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                board[i][j] = ' ';
            }
        }
        failureX = -1;
        failureY = -1;
    }

    public Game(char[][] board) {
        this.rows = board.length;
        this.columns = board[0].length;
        this.board = new char[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                this.board[i][j] = board[i][j];
                if (board[i][j] == 'A') {
                    playerAX = j;
                    playerAY = i;
                }
                if (board[i][j] == 'B') {
                    playerBX = j;
                    playerBY = i;
                }
            }
        }
        failureX = -1;
        failureY = -1;
    }

    public int getRows() { return rows; }
    public int getColumns() { return columns; }

    public boolean setBoardCell(int row, int col, char value) {
        board[row][col] = value;
        return true;
    }

    public char getBoardCell(int row, int col) { return board[row][col]; }

    public char[][] getBoard() { return board; }

    public void setBoard(char[][] newBoard) {
        board = new char[newBoard.length][newBoard[0].length];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                board[i][j] = newBoard[i][j];
                if (board[i][j] == 'A') {
                    playerAX = j;
                    playerAY = i;
                }
                if (board[i][j] == 'B') {
                    playerBX = j;
                    playerBY = i;
                }
            }
        }
        failureX = -1;
        failureY = -1;
    }

    public int getPlayerAX() { return playerAX; }
    public int getPlayerAY() { return playerAY; }
    public int getPlayerBX() { return playerBX; }
    public int getPlayerBY() { return playerBY; }

    public int getFailureX() { return failureX; }
    public int getFailureY() { return failureY; }

    void setPlayerAPosition(int x, int y) { playerAX = x; playerAY = y; }
    void setPlayerBPosition(int x, int y) { playerBX = x; playerBY = y; }

    public MoveResult makeMove(Game child, int player, Direction direction, int moveLength, int currentX, int currentY) {
        if (currentY < 0 || currentX < 0 || currentX >= columns || currentY >= rows) {
            child.failureX = currentX;
            child.failureY = currentY;
            return MoveResult.OUT_OF_BOUNDS;
        }
        char playerChar = (player == 1) ? 'A' : 'B';

        if (board[currentY][currentX] != ' ' && board[currentY][currentX] != playerChar) {
            child.failureX = currentX;
            child.failureY = currentY;
            return board[currentY][currentX] == '*' ? MoveResult.HIT_OBSTACLE : MoveResult.HIT_OPPONENT;
        }

        if (moveLength == 0) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    child.setBoardCell(i, j, board[i][j]);
                }
            }
            if (player == 1) {
                child.setPlayerBPosition(this.getPlayerBX(), this.getPlayerBY());
                child.setPlayerAPosition(currentX, currentY);
                child.setBoardCell(currentY, currentX, 'A');
            } else {
                child.setPlayerAPosition(this.getPlayerAX(), this.getPlayerAY());
                child.setPlayerBPosition(currentX, currentY);
                child.setBoardCell(currentY, currentX, 'B');
            }
            child.failureX = -1;
            child.failureY = -1;
            return MoveResult.SUCCESS;
        }

        MoveResult result = makeMove(child, player, direction, moveLength - 1, currentX + direction.getDx(), currentY + direction.getDy());
        if (result == MoveResult.SUCCESS) {
            child.setBoardCell(currentY, currentX, '*');
        }
        return result;
    }

    public List<Game> expand(int player) {
        List<Game> children = new ArrayList<>();
        int currentX, currentY;
        if (player == 1) {
            currentX = playerAX;
            currentY = playerAY;
        } else {
            currentX = playerBX;
            currentY = playerBY;
        }
        for (Direction direction : Direction.values()) {
            for (int i = 1; i <= move_limit; i++) {
                Game child = new Game(this.getRows(), this.getColumns());
                if (makeMove(child, player, direction, i, currentX, currentY) == MoveResult.SUCCESS) {
                    children.add(child);
                }
            }
        }
        return children;
    }

    public int evaluate(int player) {
        if (player == 1)
            if (expand(1).size() == 0)
                return -1;
        if (player == 2)
            if (expand(2).size() == 0)
                return 1;
        return -100;
    }
}