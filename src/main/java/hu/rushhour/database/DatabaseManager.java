package hu.rushhour.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Az eredmények és a ranglista kezelése. */
public class DatabaseManager {

  private final String databaseUrl;

  /** Beállítja az adatbázist. */
  public DatabaseManager() {
    this("jdbc:sqlite:rushhour.db");
  }

  /** Beállítja az adatbázist. */
  public DatabaseManager(String databaseUrl) {
    this.databaseUrl = databaseUrl;
  }

  private Connection connect() throws SQLException {
    return DriverManager.getConnection(databaseUrl);
  }

  /** Létrehozza a táblát, ha még nincs. */
  public void initialize() throws SQLException {

    String sql = """
        CREATE TABLE IF NOT EXISTS players (
            name TEXT PRIMARY KEY,
            games INTEGER NOT NULL DEFAULT 0,
            wins INTEGER NOT NULL DEFAULT 0,
            losses INTEGER NOT NULL DEFAULT 0
        )
        """;

    try (Connection connection = connect();
         Statement statement = connection.createStatement()) {

      statement.execute(sql);
    }
  }

  /** Elmenti a játék eredményét. */
  public void recordGame(String name, boolean won)
      throws SQLException {

    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException(
          "A játékos neve nem lehet üres.");
    }

    String sql = """
        INSERT INTO players (name, games, wins, losses)
        VALUES (?, 1, ?, ?)
        ON CONFLICT(name) DO UPDATE SET
            games = games + 1,
            wins = wins + excluded.wins,
            losses = losses + excluded.losses
        """;

    try (Connection connection = connect();
         PreparedStatement statement =
             connection.prepareStatement(sql)) {

      statement.setString(1, name.trim());
      statement.setInt(2, won ? 1 : 0);
      statement.setInt(3, won ? 0 : 1);

      statement.executeUpdate();
    }
  }

  /** Lekéri a játékosokat sorrendben. */
  public List<PlayerStats> getLeaderboard()
      throws SQLException {

    List<PlayerStats> players = new ArrayList<>();

    String sql = """
        SELECT name, games, wins, losses
        FROM players
        ORDER BY wins DESC, losses ASC, name ASC
        """;

    try (Connection connection = connect();
         Statement statement = connection.createStatement();
         ResultSet result = statement.executeQuery(sql)) {

      while (result.next()) {

        players.add(new PlayerStats(
            result.getString("name"),
            result.getInt("games"),
            result.getInt("wins"),
            result.getInt("losses")
        ));
      }
    }

    return players;
  }

  /** Kiírja a ranglistát. */
  public void printLeaderboard() throws SQLException {

    List<PlayerStats> players = getLeaderboard();

    System.out.println("\n=== RANGLISTA ===");

    System.out.printf(
        "%-5s %-20s %8s %8s %8s %10s%n",
        "Hely", "Nev", "Jatek",
        "Gyozelem", "Vereseg", "Arany");

    int position = 1;

    for (PlayerStats player : players) {

      System.out.printf(
          "%-5d %-20s %8d %8d %8d %9.1f%%%n",
          position++,
          player.name(),
          player.games(),
          player.wins(),
          player.losses(),
          player.getWinRate()
      );
    }
  }
}
