package hu.rushhour.game;

import hu.rushhour.database.DatabaseManager;
import hu.rushhour.database.PlayerStats;
import hu.rushhour.game.Game;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Random;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GameDatabaseTest {

  @TempDir
  Path tempDir;

  private Path createBoard() throws Exception {

    Path path = tempDir.resolve("board.txt");

    Files.writeString(path,
        "6 12\n"
            + "3 1\n"
            + "A 2 HORIZONTAL 3 1\n"
            + "B 2 HORIZONTAL 1 3\n");

    return path;
  }

  @Test
  void testSaveWinner() throws Exception {

    DatabaseManager database = new DatabaseManager(
        "jdbc:sqlite:" + tempDir.resolve("test.db"));

    Game game = new Game(
        new Scanner("Player\nEXIT A\n"),
        new Random(),
        createBoard(),
        database);

    game.start();

    PlayerStats player =
        database.getLeaderboard().getFirst();

    assertEquals("Player", player.name());
    assertEquals(1, player.games());
    assertEquals(1, player.wins());
    assertEquals(0, player.losses());
  }

  @Test
  void testDatabaseInteraction() throws Exception {

    DatabaseManager database =
        mock(DatabaseManager.class);

    Game game = new Game(
        new Scanner("Player\nEXIT A\n"),
        new Random(),
        createBoard(),
        database);

    game.start();

    verify(database).initialize();
    verify(database).recordGame("Player", true);
    verify(database).printLeaderboard();
  }

  @Test
  void testDatabaseError() throws Exception {

    DatabaseManager database =
        mock(DatabaseManager.class);

    doThrow(new SQLException("Database error"))
        .when(database).initialize();

    Game game = new Game(
        new Scanner("Player\nEXIT A\n"),
        new Random(),
        createBoard(),
        database);

    assertDoesNotThrow(game::start);
  }
}
