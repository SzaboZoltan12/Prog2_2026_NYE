package hu.rushhour.Game;

import hu.rushhour.io.BoardFileHandler;
import hu.rushhour.model.Board;
import hu.rushhour.model.Vehicle;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameTest {

    @TempDir
    Path tempDir;

    private String runGame(
            String input, Random random, Path path) {

        PrintStream original = System.out;

        ByteArrayOutputStream output =
                new ByteArrayOutputStream();

        try {

            System.setOut(new PrintStream(output));

            Game game = new Game(
                    new Scanner(input), random, path);

            game.start();

            return output.toString();

        } finally {
            System.setOut(original);
        }
    }

    private int vehicleIndex(Board board, String id) {

        List<Vehicle> vehicles =
                new ArrayList<>(board.getVehicles());

        for (int i = 0; i < vehicles.size(); i++) {

            if (vehicles.get(i).getId().equals(id)) {
                return i;
            }
        }

        throw new IllegalArgumentException(
                "Ismeretlen jarmu: " + id);
    }

    @Test
    void testHumanWins() throws Exception {

        Random random = mock(Random.class);

        Board board = new BoardFileHandler().loadDefault();

        when(random.nextInt(anyInt()))
                .thenReturn(vehicleIndex(board, "A"), 0);

        String output = runGame(
                "Player\nX 2\nEXIT A\n",
                random,
                tempDir.resolve("missing.txt"));

        assertTrue(output.contains("Gyoztes: Player"));

        verify(random, times(2)).nextInt(anyInt());
    }

    @Test
    void testInvalidCommands() throws Exception {

        Random random = mock(Random.class);

        Board board = new BoardFileHandler().loadDefault();

        when(random.nextInt(anyInt()))
                .thenReturn(vehicleIndex(board, "A"), 0);

        String output = runGame(
                "\nPlayer\n"
                + "ERROR\n"
                + "EXIT B\n"
                + "EXIT A\n"
                + "A abc\n"
                + "A 0\n"
                + "A 100\n"
                + "X 2\n"
                + "EXIT A\n",
                random,
                tempDir.resolve("missing.txt"));

        assertTrue(output.contains("A nev nem lehet ures"));
        assertTrue(output.contains("Hibas parancs"));
        assertTrue(output.contains("Csak az A"));
        assertTrue(output.contains("ut nem szabad"));
        assertTrue(output.contains("egesz szam legyen"));
        assertTrue(output.contains("Ervenytelen lepes"));
        assertTrue(output.contains("nem hajthato vegre"));
        assertTrue(output.contains("Gyoztes: Player"));
    }

    @Test
    void testComputerWins() throws Exception {

        Path path = tempDir.resolve("board.txt");

        Files.writeString(path,
                "6 12\n"
                + "3 1\n"
                + "A 2 HORIZONTAL 3 1\n"
                + "B 2 HORIZONTAL 1 3\n");

        Board board = new BoardFileHandler().load(path);

        Random random = mock(Random.class);

        when(random.nextInt(anyInt()))
                .thenReturn(vehicleIndex(board, "B"))
                .thenAnswer(invocation ->
                        invocation.getArgument(
                                0, Integer.class) - 1);

        String output = runGame(
                "Player\nA 1\n", random, path);

        assertTrue(output.contains("Gyoztes: Computer"));

        verify(random, times(2)).nextInt(anyInt());
    }

    @Test
    void testDefaultBoardLoading() throws Exception {

        Random random = mock(Random.class);

        Board board = new BoardFileHandler().loadDefault();

        when(random.nextInt(anyInt()))
                .thenReturn(vehicleIndex(board, "A"), 0);

        String output = runGame(
                "Player\nX 2\nEXIT A\n",
                random,
                tempDir.resolve("missing.txt"));

        assertTrue(output.contains(
                "Alapertelmezett palya betoltve."));
    }

    @Test
    void testCustomBoardLoading() throws Exception {

        Path path = tempDir.resolve("board.txt");

        Files.writeString(path,
                "6 12\n"
                + "3 1\n"
                + "A 2 HORIZONTAL 3 1\n"
                + "B 2 HORIZONTAL 1 3\n");

        Random random = mock(Random.class);

        String output = runGame(
                "Player\nEXIT A\n", random, path);

        assertTrue(output.contains("Egyedi palya betoltve."));
        assertTrue(output.contains("Gyoztes: Player"));

        verifyNoInteractions(random);
    }
}