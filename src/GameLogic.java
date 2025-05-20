import java.util.ArrayList;
import java.util.List;

public class GameLogic {
    public static final int MOVE_LIMIT = 2;

    public MoveResult makeMove(BoardState current, BoardState child, int player, Direction direction, int moveLength, int currentX, int currentY) {
        if (currentY < 0 || currentX < 0 || currentX >= current.getColumns() || currentY >= current.getRows()) {
            child.setFailurePosition(currentX, currentY);
            return MoveResult.OUT_OF_BOUNDS;
        }
        char playerChar = (player == 1) ? 'A' : 'B';

        if (current.getBoardCell(currentY, currentX) != ' ' && current.getBoardCell(currentY, currentX) != playerChar) {
            child.setFailurePosition(currentX, currentY);
            return current.getBoardCell(currentY, currentX) == '*' ? MoveResult.HIT_OBSTACLE : MoveResult.HIT_OPPONENT;
        }

        if (moveLength == 0) {
            for (int i = 0; i < current.getRows(); i++) {
                for (int j = 0; j < current.getColumns(); j++) {
                    child.setBoardCell(i, j, current.getBoardCell(i, j));
                }
            }
            if (player == 1) {
                child.setPlayerBPosition(current.getPlayerBX(), current.getPlayerBY());
                child.setPlayerAPosition(currentX, currentY);
                child.setBoardCell(currentY, currentX, 'A');
            } else {
                child.setPlayerAPosition(current.getPlayerAX(), current.getPlayerAY());
                child.setPlayerBPosition(currentX, currentY);
                child.setBoardCell(currentY, currentX, 'B');
            }
            child.setFailurePosition(currentX, currentY);
            return MoveResult.SUCCESS;
        }

        MoveResult result = makeMove(current, child, player, direction, moveLength - 1, currentX + direction.getDx(), currentY + direction.getDy());
        if (result == MoveResult.SUCCESS) {
            child.setBoardCell(currentY, currentX, '*');
        }
        return result;
    }

    public List<BoardState> expand(BoardState state, int player) {
        List<BoardState> children = new ArrayList<>();
        int currentX, currentY;
        if (player == 1) {
            currentX = state.getPlayerAX();
            currentY = state.getPlayerAY();
        } else {
            currentX = state.getPlayerBX();
            currentY = state.getPlayerBY();
        }
        for (Direction direction : Direction.values()) {
            for (int i = 1; i <= MOVE_LIMIT; i++) {
                BoardState child = new BoardState(state.getRows(), state.getColumns());
                if (makeMove(state, child, player, direction, i, currentX, currentY) == MoveResult.SUCCESS) {
                    children.add(child);
                }
            }
        }
        return children;
    }

    public int evaluate(BoardState state, int player) {
        if (player == 1)
            if (expand(state, 1).size() == 0)
                return -1;
        if (player == 2)
            if (expand(state, 2).size() == 0)
                return 1;
        return -100;
    }
}