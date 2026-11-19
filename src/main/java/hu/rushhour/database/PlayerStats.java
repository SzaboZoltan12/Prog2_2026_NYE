package hu.rushhour.database;

/** A játékos eredményeit tárolja. */
public record PlayerStats(
    String name,
    int games,
    int wins,
    int losses
) {

  /** Kiszámolja a győzelmek százalékát. */
  public double getWinRate() {
    if (games == 0) {
      return 0;
    }

    return wins * 100.0 / games;
  }
}
