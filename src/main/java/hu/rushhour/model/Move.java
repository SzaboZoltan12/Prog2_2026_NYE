package hu.rushhour.model;

import java.util.Objects;

/** Egy jármű lépésének adatai. */
public final class Move {

  private final String vehicleId;
  private final int displacement;

  /** Ellenőrzi a lépés adatait. */
  public Move(String vehicleId, int displacement) {
    if (vehicleId == null || vehicleId.isBlank()) {
      throw new IllegalArgumentException(
          "A járműazonosító nem lehet üres.");
    }

    if (displacement == 0) {
      throw new IllegalArgumentException(
          "Az elmozdulás nem lehet nulla.");
    }

    this.vehicleId = vehicleId;
    this.displacement = displacement;
  }

  /** Visszaadja a jármű azonosítóját. */
  public String getVehicleId() {
    return vehicleId;
  }

  /** Visszaadja az elmozdulást. */
  public int getDisplacement() {
    return displacement;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }

    if (!(object instanceof Move move)) {
      return false;
    }

    return displacement == move.displacement
        && vehicleId.equals(move.vehicleId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(vehicleId, displacement);
  }

  @Override
  public String toString() {
    return "Move{"
        + "vehicleId='" + vehicleId + '\''
        + ", displacement=" + displacement
        + '}';
  }
}
