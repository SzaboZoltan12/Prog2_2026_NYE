
package hu.rushhour.model;

import java.util.Objects;

public final class Vehicle {

    private final String id;
    private final int length;
    private final Orientation orientation;
    private final int row;
    private final int column;

    public Vehicle(String id, int length,
                   Orientation orientation,
                   int row, int column) {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(
                    "Vehicle ID cannot be empty");
        }

        if (length < 2) {
            throw new IllegalArgumentException(
                    "Vehicle length must be at least 2");
        }

        if (row < 0 || column < 0) {
            throw new IllegalArgumentException(
                    "Coordinates cannot be negative");
        }

        this.id = id;
        this.length = length;
        this.orientation = Objects.requireNonNull(
                orientation, "Orientation is required");
        this.row = row;
        this.column = column;
    }

    public String getId() {
        return id;
    }

    public int getLength() {
        return length;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

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
        return "Vehicle{" +
                "id='" + id + '\'' +
                ", length=" + length +
                ", orientation=" + orientation +
                ", row=" + row +
                ", column=" + column +
                '}';
    }
}
