package hu.rushhour.io;

import hu.rushhour.model.Board;
import hu.rushhour.model.GameState;
import hu.rushhour.model.Move;
import hu.rushhour.model.Vehicle;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

class XmlGameFileHandlerTest {

  @TempDir
  Path tempDir;

  private final XmlGameFileHandler handler =
      new XmlGameFileHandler();

  private Board createBoard() throws IOException {
    return new BoardFileHandler().loadDefault();
  }

  @Test
  void testSaveGame() throws IOException {

    Path path = tempDir.resolve("game.xml");

    GameState state =
        new GameState("Player", createBoard(), true);

    handler.save(path, state);

    assertTrue(Files.exists(path));
    assertTrue(Files.size(path) > 0);
  }

  @Test
  void testLoadGame() throws IOException {

    Path path = tempDir.resolve("game.xml");

    Board original = createBoard();

    handler.save(path,
        new GameState("Player", original, true));

    GameState loaded = handler.load(path);

    assertEquals("Player", loaded.playerName());
    assertTrue(loaded.humanTurn());

    assertEquals(original.getRows(),
        loaded.board().getRows());

    assertEquals(original.getColumns(),
        loaded.board().getColumns());

    assertEquals(original.getHumanExit(),
        loaded.board().getHumanExit());

    assertEquals(original.getComputerExit(),
        loaded.board().getComputerExit());

    assertEquals(original.getVehicles().size(),
        loaded.board().getVehicles().size());

    for (Vehicle vehicle : original.getVehicles()) {
      assertTrue(
          loaded.board().getVehicles().contains(vehicle));
    }
  }

  @Test
  void testComputerTurn() throws IOException {

    Path path = tempDir.resolve("game.xml");

    handler.save(path,
        new GameState("Player", createBoard(), false));

    GameState loaded = handler.load(path);

    assertFalse(loaded.humanTurn());
  }

  @Test
  void testSaveMovedVehicle() throws IOException {

    Board board = createBoard();

    board.moveVehicle(new Move("X", 2));

    Path path = tempDir.resolve("game.xml");

    handler.save(path,
        new GameState("Player", board, true));

    GameState loaded = handler.load(path);

    for (Vehicle vehicle : board.getVehicles()) {
      assertTrue(
          loaded.board().getVehicles().contains(vehicle));
    }

    assertTrue(loaded.board().canExit("A"));
  }

  @Test
  void testMissingFile() {

    Path path = tempDir.resolve("missing.xml");

    assertThrows(IOException.class,
        () -> handler.load(path));
  }

  @Test
  void testInvalidXml() throws IOException {

    Path path = tempDir.resolve("invalid.xml");

    Files.writeString(path, "Invalid XML");

    assertThrows(IOException.class,
        () -> handler.load(path));
  }

  @Test
  void testInvalidRoot() throws IOException {

    Path path = tempDir.resolve("invalid.xml");

    Files.writeString(path, "<invalid/>");

    assertThrows(IOException.class,
        () -> handler.load(path));
  }

  @Test
  void testInvalidTurn() throws IOException {

    Path path = tempDir.resolve("invalid.xml");

    Files.writeString(path,
        "<game player=\"Player\" humanTurn=\"invalid\"/>");

    assertThrows(IOException.class,
        () -> handler.load(path));
  }

  @Test
  void testEmptyPlayerName() throws IOException {

    Board board = createBoard();

    assertThrows(IllegalArgumentException.class,
        () -> new GameState("", board, true));
  }

  @Test
  void testNullBoard() {

    assertThrows(NullPointerException.class,
        () -> new GameState("Player", null, true));
  }

  @Test
  void testCompletedGame() throws IOException {

    Board board = createBoard();

    board.moveVehicle(new Move("X", 2));

    board.exitVehicle("A");

    assertThrows(IllegalArgumentException.class,
        () -> new GameState("Player", board, true));
  }

  @Test
  void testSaveFailure() throws IOException {

    Path path = tempDir.resolve("missing/game.xml");

    GameState state =
        new GameState("Player", createBoard(), true);

    assertThrows(IOException.class,
        () -> handler.save(path, state));
  }
}