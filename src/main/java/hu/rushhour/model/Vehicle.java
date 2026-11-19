package hu.rushhour.model;

import java.util.Objects;

/** A jármű adatai, amelyek nem módosíthatók. */
public final class Vehicle {

  private final String id;
  private final int length;
  private final Orientation orientation;
  private final int row;
  private final int column;

  /** Létrehozza a járművet és ellenőrzi az adatokat. */
  public Vehicle(String id, int length,
                 Orientation orientation,
                 int row, int column) {

    if (id == null || id.isBlank()) {
      throw new IllegalArgumentException(
          "A járműazonosító nem lehet üres.");
    }

    if (length < 2) {
      throw new IllegalArgumentException(
          "A jármű hossza legalább 2 kell legyen.");
    }

    if (row < 0 || column < 0) {
      throw new IllegalArgumentException(
          "A koordináták nem lehetnek negatívak.");
    }

    this.id = id;
    this.length = length;
    this.orientation = Objects.requireNonNull(
        orientation, "Az irányt meg kell adni.");
    this.row = row;
    this.column = column;
  }

  /** Visszaadja az azonosítót. */
  public String getId() {
    return id;
  }

  /** Visszaadja a hosszát. */
  public int getLength() {
    return length;
  }

  /** Visszaadja az irányát. */
  public Orientation getOrientation() {
    return orientation;
  }

  /** Visszaadja a sorát. */
  public int getRow() {
    return row;
  }

  /** Visszaadja az oszlopát. */
  public int getColumn() {
    return column;
  }

  /** Új járművet ad vissza az új helyen. */
  public Vehicle withPosition(int newRow, int newColumn) {
    return new Vehicle(
        id,
        length,
        orientation,
        newRow,
        newColumn
    );
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }

    if (!(object instanceof Vehicle vehicle)) {
      return false;
    }

    return length == vehicle.length
        && row == vehicle.row
        && column == vehicle.column
        && id.equals(vehicle.id)
        && orientation == vehicle.orientation;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id, length, orientation, row, column);
  }

  @Override
  public String toString() {
    return "Vehicle{"
        + "id='" + id + '\''
        + ", length=" + length
        + ", orientation=" + orientation
        + ", row=" + row
        + ", column=" + column
        + '}';
  }
}
