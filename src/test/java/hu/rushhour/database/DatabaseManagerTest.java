package hu.rushhour.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseManagerTest {

    @TempDir
    Path tempDir;

    private DatabaseManager database;

    @BeforeEach
    void setUp() throws Exception {

        database = new DatabaseManager(
                "jdbc:sqlite:"
                        + tempDir.resolve("test.db"));

        database.initialize();
    }

    @Test
    void testEmptyLeaderboard() throws Exception {

        assertTrue(database.getLeaderboard().isEmpty());
    }

    @Test
    void testRecordWin() throws Exception {

        database.recordGame("Player", true);

        PlayerStats player =
                database.getLeaderboard().getFirst();

        assertEquals("Player", player.name());
        assertEquals(1, player.games());
        assertEquals(1, player.wins());
        assertEquals(0, player.losses());
    }

    @Test
    void testRecordLoss() throws Exception {

        database.recordGame("Player", false);

        PlayerStats player =
                database.getLeaderboard().getFirst();

        assertEquals(1, player.games());
        assertEquals(0, player.wins());
        assertEquals(1, player.losses());
    }

    @Test
    void testMultipleGames() throws Exception {

        database.recordGame("Player", true);
        database.recordGame("Player", true);
        database.recordGame("Player", false);

        PlayerStats player =
                database.getLeaderboard().getFirst();

        assertEquals(3, player.games());
        assertEquals(2, player.wins());
        assertEquals(1, player.losses());
    }

    @Test
    void testLeaderboardOrder() throws Exception {

        database.recordGame("Player1", true);

        database.recordGame("Player2", true);
        database.recordGame("Player2", true);

        List<PlayerStats> players =
                database.getLeaderboard();

        assertEquals("Player2", players.get(0).name());
        assertEquals("Player1", players.get(1).name());
    }

    @Test
    void testWinRate() {

        PlayerStats player =
                new PlayerStats("Player", 10, 7, 3);

        assertEquals(70.0, player.getWinRate());
    }

    @Test
    void testZeroGamesWinRate() {

        PlayerStats player =
                new PlayerStats("Player", 0, 0, 0);

        assertEquals(0.0, player.getWinRate());
    }

    @Test
    void testInvalidName() {

        assertThrows(
                IllegalArgumentException.class,
                () -> database.recordGame("", true)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> database.recordGame(null, true)
        );
    }

    @Test
    void testPrintLeaderboard() throws Exception {

        database.recordGame("Player", true);

        assertDoesNotThrow(
                () -> database.printLeaderboard()
        );
    }

    @Test
    void testPersistence() throws Exception {

        database.recordGame("Player", true);

        DatabaseManager reopened = new DatabaseManager(
                "jdbc:sqlite:"
                        + tempDir.resolve("test.db"));

        reopened.initialize();

        assertEquals(
                1,
                reopened.getLeaderboard().getFirst().wins()
        );
    }
}