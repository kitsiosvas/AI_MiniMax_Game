import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            GameIO gameIO = new ConsoleGameIO(scanner);
            GameManager gameManager = new GameManager(gameIO);
            gameManager.startGame();
        }
    }
}