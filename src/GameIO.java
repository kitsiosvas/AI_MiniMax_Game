import java.util.List;

public interface GameIO {
    int[] promptBoardSize();
    List<int[]> promptBlackSquares(int rows, int columns);
    int[][] promptPlayerPositions(int rows, int columns);
    Pair<Direction, Integer> promptPlayerMove();
    boolean promptPlayAgain();
    void displayMessage(String message);
    void displayBoard(BoardState state);
    void displayMoveError(MoveResult result, Direction direction, int length, int failureY, int failureX);
}