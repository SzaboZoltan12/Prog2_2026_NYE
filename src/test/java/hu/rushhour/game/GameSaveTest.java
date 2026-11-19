package hu.rushhour.game;

import hu.rushhour.database.DatabaseManager;
import hu.rushhour.io.BoardFileHandler;
import hu.rushhour.io.XmlGameFileHandler;
import hu.rushhour.model.Board;
import hu.rushhour.model.GameState;
import hu.rushhour.model.Move;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.Scanner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameSaveTest {

  @TempDir
  Path tempDir;

  private String runGame(String input) {

    PrintStream original = System.out;

    ByteArrayOutputStream output =
        new ByteArrayOutputStream();

    try {

      System.setOut(new PrintStream(output));

      Game game = new Game(
          new Scanner(input),
          new Random(),
          tempDir.resolve("board.txt"));

      game.start();

      return output.toString();

    } finally {
      System.setOut(original);
    }
  }

  @Test
  void testSaveTxt() throws Exception {

    String output = runGame(
        "Player\nSAVE TXT\nQUIT\n");

    Path path = tempDir.resolve("saved-board.txt");

    assertTrue(Files.exists(path));

    assertTrue(output.contains(
        "A palya sikeresen mentve!"));

    Board board = new BoardFileHandler().load(path);

    assertEquals(6, board.getRows());
    assertEquals(12, board.getColumns());
  }

  @Test
  void testSaveXml() throws Exception {

    String output = runGame(
        "Player\nSAVE XML\nQUIT\n");

    Path path = tempDir.resolve("saved-game.xml");

    assertTrue(Files.exists(path));

    assertTrue(output.contains(
        "A jatek sikeresen mentve!"));

    GameState state =
        new XmlGameFileHandler().load(path);

    assertEquals("Player", state.playerName());
    assertTrue(state.humanTurn());
  }

  @Test
  void testLoadXml() throws Exception {

    Board board =
        new BoardFileHandler().loadDefault();

    board.moveVehicle(new Move("X", 2));

    new XmlGameFileHandler().save(
        tempDir.resolve("saved-game.xml"),
        new GameState("SavedPlayer", board, true));

    String output = runGame(
        "Player\nLOAD XML\nEXIT A\n");

    assertTrue(output.contains(
        "A mentett jatek betoltve!"));

    assertTrue(output.contains(
        "Gyoztes: SavedPlayer"));
  }

  @Test
  void testLoadMissingXml() {

    String output = runGame(
        "Player\nLOAD XML\nQUIT\n");

    assertTrue(output.contains(
        "A mentett jatek nem toltheto be!"));
  }

  @Test
  void testInvalidSaveFormat() {

    String output = runGame(
        "Player\nSAVE PDF\nQUIT\n");

    assertTrue(output.contains(
        "formatum"));
  }

  @Test
  void testInvalidLoadFormat() {

    String output = runGame(
        "Player\nLOAD PDF\nQUIT\n");

    assertTrue(output.contains(
        "Ismeretlen betoltesi formatum!"));
  }

  @Test
  void testSaveTxtFailure() throws Exception {

    Files.createDirectory(
        tempDir.resolve("saved-board.txt"));

    String output = runGame(
        "Player\nSAVE TXT\nQUIT\n");

    assertTrue(output.contains(
        "A palya mentese sikertelen!"));
  }

  @Test
  void testSaveXmlFailure() throws Exception {

    Files.createDirectory(
        tempDir.resolve("saved-game.xml"));

    String output = runGame(
        "Player\nSAVE XML\nQUIT\n");

    assertTrue(output.contains(
        "A jatek mentese sikertelen!"));
  }

  @Test
  void testQuit() {

    String output = runGame(
        "Player\nQUIT\n");

    assertTrue(output.contains(
        "Kileptel a jatekbol."));
  }

  @Test
  void testQuitWithoutDatabaseResult() {

    DatabaseManager database =
        mock(DatabaseManager.class);

    Game game = new Game(
        new Scanner("Player\nQUIT\n"),
        new Random(),
        tempDir.resolve("board.txt"),
        database);

    game.start();

    verifyNoInteractions(database);
  }

  @Test
  void testEndOfInput() {

    String output = runGame("Player\n");

    assertTrue(output.contains(
        "Kileptel a jatekbol."));
  }
}