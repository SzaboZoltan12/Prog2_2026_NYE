package hu.rushhour.io;

import hu.rushhour.model.Board;
import hu.rushhour.model.Vehicle;
import hu.rushhour.model.Orientation;
import hu.rushhour.model.Move;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class BoardFileHandlerTest {

    @TempDir
    Path tempDir;

    private final BoardFileHandler handler =
            new BoardFileHandler();

    private Board createBoard() {
        Board board = new Board(6, 6);

        board.addVehicle(new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 3, 1));

        board.addVehicle(new Vehicle(
                "B", 2, Orientation.HORIZONTAL, 1, 3));

        board.configureExits(3, 1);

        return board;
    }

    @Test
    void testSaveBoard() throws IOException {
        Path file = tempDir.resolve("board.txt");

        handler.save(file, createBoard());

        assertTrue(Files.exists(file));
        assertFalse(Files.readAllLines(file).isEmpty());
    }

    @Test
    void testLoadBoard() throws IOException {
        Path file = tempDir.resolve("board.txt");

        handler.save(file, createBoard());

        Board loaded = handler.load(file);

        assertEquals(6, loaded.getRows());
        assertEquals(6, loaded.getColumns());
        assertEquals(2, loaded.getVehicles().size());
        assertNotNull(loaded.findVehicle("A"));
        assertNotNull(loaded.findVehicle("B"));
    }

    @Test
    void testDefaultBoard() throws IOException {
        Board board = handler.loadDefault();

        assertEquals(6, board.getRows());
        assertEquals(12, board.getColumns());
        assertEquals(4, board.getVehicles().size());
    }

    @Test
    void testMissingFile() {
        Path file = tempDir.resolve("missing.txt");

        assertThrows(IOException.class,
                () -> handler.load(file));
    }

    @Test
    void testEmptyFile() throws IOException {
        Path file = tempDir.resolve("empty.txt");

        Files.writeString(file, "");

        assertThrows(IOException.class,
                () -> handler.load(file));
    }

    @Test
    void testInvalidDimensions() throws IOException {
        Path file = tempDir.resolve("invalid.txt");

        Files.writeString(file, """
                abc 6
                3 1
                A 2 HORIZONTAL 3 1
                B 2 HORIZONTAL 1 3
                """);

        assertThrows(IOException.class,
                () -> handler.load(file));
    }

    @Test
    void testInvalidFormat() throws IOException {
        Path file = tempDir.resolve("invalid.txt");

        Files.writeString(file, """
                6 6
                3 1
                A 2 HORIZONTAL
                B 2 HORIZONTAL 1 3
                """);

        assertThrows(IOException.class,
                () -> handler.load(file));
    }

    @Test
    void testSaveWithoutExits() {
        Board board = new Board(6, 6);
        Path file = tempDir.resolve("board.txt");

        assertThrows(IOException.class,
                () -> handler.save(file, board));
    }

    @Test
    void testSaveCompletedBoard() {
        Board board = createBoard();
        board.exitVehicle("A");

        Path file = tempDir.resolve("board.txt");

        assertThrows(IOException.class,
                () -> handler.save(file, board));
    }

    @Test
    void testSaveMovedVehicle() throws IOException {
        Board board = createBoard();

        board.moveVehicle(new Move("A", 1));

        Path file = tempDir.resolve("board.txt");

        handler.save(file, board);

        Board loaded = handler.load(file);

        assertEquals(2, loaded.findVehicle("A").getColumn());
    }
}