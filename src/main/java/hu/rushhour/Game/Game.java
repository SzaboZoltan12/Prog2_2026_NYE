package hu.rushhour.Game;

import hu.rushhour.model.Board;
import hu.rushhour.model.Move;
import hu.rushhour.model.Orientation;
import hu.rushhour.model.Vehicle;
import hu.rushhour.player.ComputerPlayer;
import hu.rushhour.player.HumanPlayer;
import hu.rushhour.io.BoardFileHandler;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Game {

    private final Scanner scanner;
    private final Random random;
    private final Path boardPath;

    private Board board;
    private HumanPlayer human;
    private ComputerPlayer computer;

    public Game() {
        this(new Scanner(System.in), new Random());
    }

    public Game(Scanner scanner, Random random) {
        this(scanner, random, Path.of("board.txt"));
    }

    public Game(Scanner scanner, Random random, Path boardPath) {
        this.scanner = scanner;
        this.random = random;
        this.boardPath = boardPath;
    }

    public void start() {

        System.out.println("=== RUSH HOUR ===");
        System.out.print("Add meg a neved: ");

        String name = scanner.nextLine().trim();

        while (name.isBlank()) {
            System.out.print("A nev nem lehet ures: ");
            name = scanner.nextLine().trim();
        }

        human = new HumanPlayer(name);
        computer = new ComputerPlayer();

        initializeBoard();

        System.out.println("\nUdvozollek, "
                + human.getName() + "!");

        System.out.println("A celod az A jarmu kijuttatasa.");
        System.out.println("A gep celja a B jarmu kijuttatasa.");

        boolean humanTurn = true;

        while (!board.isGameOver()) {

            System.out.println();
            board.printBoard();

            if (humanTurn) {
                humanTurn();
            } else {
                computerTurn();
            }

            humanTurn = !humanTurn;
        }

        System.out.println("\n=== JATEK VEGE ===");

        board.printBoard();

        String winnerId = board.getWinnerVehicleId();

        if (winnerId.equals(human.getTargetVehicleId())) {
            System.out.println("Gyoztes: " + human.getName());
        } else {
            System.out.println("Gyoztes: " + computer.getName());
        }
    }

    private void initializeBoard() {

        BoardFileHandler handler = new BoardFileHandler();

        try {
            board = handler.load(boardPath);

            System.out.println("Egyedi palya betoltve.");

        } catch (IOException e) {

            try {
                board = handler.loadDefault();

                System.out.println(
                        "Alapertelmezett palya betoltve.");

            } catch (IOException ex) {

                throw new IllegalStateException(
                        "A palya nem toltheto be.", ex);
            }
        }
    }

    private void humanTurn() {

        System.out.println("\n"
                + human.getName() + " kovetkezik!");

        while (true) {

            System.out.println("Parancs: JARMU ELMOZDULAS");
            System.out.println("Pelda: X 2");
            System.out.println("Kilepes: EXIT A");
            System.out.print("> ");

            String input = scanner.nextLine().trim();
            String[] parts = input.split("\\s+");

            if (parts.length != 2) {
                System.out.println("Hibas parancs!");
                continue;
            }

            String vehicleId = parts[0].toUpperCase();

            if (vehicleId.equals("EXIT")) {

                String target = parts[1].toUpperCase();

                if (!target.equals("A")) {
                    System.out.println(
                            "Csak az A jarmuvel nyerhetsz!");
                    continue;
                }

                if (board.canExit(target)) {
                    board.exitVehicle(target);
                    return;
                }

                System.out.println(
                        "A kijarathoz vezeto ut nem szabad!");
                continue;
            }

            int displacement;

            try {
                displacement = Integer.parseInt(parts[1]);

            } catch (NumberFormatException e) {

                System.out.println(
                        "Az elmozdulas egesz szam legyen!");
                continue;
            }

            Move move;

            try {
                move = new Move(vehicleId, displacement);

            } catch (IllegalArgumentException e) {

                System.out.println(
                        "Ervenytelen lepes: " + e.getMessage());
                continue;
            }

            if (board.canMove(move)) {
                board.moveVehicle(move);

                System.out.println("Sikeres lepes!");
                return;
            }

            System.out.println(
                    "A lepes nem hajthato vegre!");
        }
    }

    private void computerTurn() {

        System.out.println("\nComputer kovetkezik...");

        List<Vehicle> candidates =
                new ArrayList<>(board.getVehicles());

        while (!candidates.isEmpty()) {

            int index = random.nextInt(candidates.size());

            Vehicle vehicle = candidates.remove(index);

            List<Move> legalMoves = new ArrayList<>();

            int limit = vehicle.getOrientation()
                    == Orientation.HORIZONTAL
                    ? board.getColumns()
                    : board.getRows();

            for (int amount = -limit; amount <= limit; amount++) {

                if (amount == 0) {
                    continue;
                }

                Move move = new Move(vehicle.getId(), amount);

                if (board.canMove(move)) {
                    legalMoves.add(move);
                }
            }

            boolean canWin = vehicle.getId()
                    .equals(computer.getTargetVehicleId())
                    && board.canExit(vehicle.getId());

            int optionCount = legalMoves.size()
                    + (canWin ? 1 : 0);

            if (optionCount == 0) {
                continue;
            }

            int choice = random.nextInt(optionCount);

            if (canWin && choice == legalMoves.size()) {

                board.exitVehicle(vehicle.getId());

                System.out.println(
                        "Computer kijutott a B jarmuvel!");

                return;
            }

            Move selected = legalMoves.get(choice);

            board.moveVehicle(selected);

            System.out.println("Computer lepese: "
                    + selected.getVehicleId() + " "
                    + selected.getDisplacement());

            return;
        }

        System.out.println(
                "A gep nem talalt szabalyos lepest.");
    }
}