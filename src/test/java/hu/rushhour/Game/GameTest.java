package hu.rushhour.Game;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Random;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameTest {

    private String runGame(String input, Random random) {

        PrintStream original = System.out;
        ByteArrayOutputStream output =
                new ByteArrayOutputStream();

        try {
            System.setOut(new PrintStream(output));

            Game game = new Game(
                    new Scanner(input), random);

            game.start();

            return output.toString();

        } finally {
            System.setOut(original);
        }
    }

    @Test
    void testHumanWins() {

        Random random = mock(Random.class);

        when(random.nextInt(anyInt())).thenReturn(0);

        String output = runGame(
                "Player\nX 2\nEXIT A\n", random);

        assertTrue(output.contains("Gyoztes: Player"));

        verify(random).nextInt(anyInt());
    }

    @Test
    void testInvalidCommands() {

        Random random = mock(Random.class);

        when(random.nextInt(anyInt())).thenReturn(0);

        String output = runGame(
                "\nPlayer\n"
                + "ERROR\n"
                + "EXIT B\n"
                + "EXIT A\n"
                + "A abc\n"
                + "A 0\n"
                + "A 100\n"
                + "X 2\n"
                + "EXIT A\n", random);

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
    void testComputerWins() {

        Random random = mock(Random.class);

        when(random.nextInt(anyInt()))
                .thenAnswer(invocation ->
                        invocation.getArgument(
                                0, Integer.class) - 1);

        String output = runGame(
                "Player\nA 1\nX 2\n", random);

        assertTrue(output.contains("Gyoztes: Computer"));

        verify(random, times(2)).nextInt(anyInt());
    }
}