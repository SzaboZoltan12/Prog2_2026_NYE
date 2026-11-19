package hu.rushhour.player;

/** A játékosok közös adatai. */
public abstract class Player {

  private final String name;
  private final String targetVehicleId;

  /** Ellenőrzi a játékos adatait. */
  protected Player(String name, String targetVehicleId) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException(
          "A játékos neve nem lehet üres.");
    }

    if (targetVehicleId == null
        || targetVehicleId.isBlank()) {
      throw new IllegalArgumentException(
          "A céljármű nem lehet üres.");
    }

    this.name = name;
    this.targetVehicleId = targetVehicleId;
  }

  /** Visszaadja a nevet. */
  public String getName() {
    return name;
  }

  /** Visszaadja a céljármű azonosítóját. */
  public String getTargetVehicleId() {
    return targetVehicleId;
  }
}
