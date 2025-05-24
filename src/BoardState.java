public class BoardState {
    private final int rows;
    private final int columns;
    private char[][] board;
    private int playerAX, playerAY, playerBX, playerBY;
    private int failureX, failureY;
    private Direction moveDirection; // Added for error reporting
    private int moveLength; // Added for error reporting

    public BoardState(int rows, int columns) {
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
        moveDirection = null;
        moveLength = 0;
    }

    public BoardState(char[][] board) {
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
        moveDirection = null;
        moveLength = 0;
    }

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

    public int getRows() { return rows; }
    public int getColumns() { return columns; }
    public char[][] getBoard() { return board; }
    public char getBoardCell(int row, int col) { return board[row][col]; }
    public int getPlayerAX() { return playerAX; }
    public int getPlayerAY() { return playerAY; }
    public int getPlayerBX() { return playerBX; }
    public int getPlayerBY() { return playerBY; }
    public int getFailureX() { return failureX; }
    public int getFailureY() { return failureY; }
    public Direction getMoveDirection() { return moveDirection; }
    public int getMoveLength() { return moveLength; }

    public boolean setBoardCell(int row, int col, char value) {
        board[row][col] = value;
        return true;
    }

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
        moveDirection = null;
        moveLength = 0;
    }

    public void setPlayerAPosition(int x, int y) { playerAX = x; playerAY = y; }
    public void setPlayerBPosition(int x, int y) { playerBX = x; playerBY = y; }
    public void setFailurePosition(int x, int y) { failureX = x; failureY = y; }
    public void setMoveDirection(Direction direction) { this.moveDirection = direction; }
    public void setMoveLength(int length) { this.moveLength = length; }
}