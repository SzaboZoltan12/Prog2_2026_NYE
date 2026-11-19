package hu.rushhour.database;

public record PlayerStats(
        String name,
        int games,
        int wins,
        int losses
) {

    public double getWinRate() {
        if (games == 0) {
            return 0;
        }

        return wins * 100.0 / games;
    }
}