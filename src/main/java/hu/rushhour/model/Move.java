
package hu.rushhour.model;

import java.util.Objects;

public final class Move {

    private final String vehicleId;
    private final int displacement;

    public Move(String vehicleId, int displacement) {
        if (vehicleId == null || vehicleId.isBlank()) {
            throw new IllegalArgumentException(
                    "Vehicle ID cannot be empty");
        }

        if (displacement == 0) {
            throw new IllegalArgumentException(
                    "Displacement cannot be zero");
        }

        this.vehicleId = vehicleId;
        this.displacement = displacement;
    }

    public String getVehicleId() {
        return vehicleId;
    }

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
        return "Move{" +
                "vehicleId='" + vehicleId + '\'' +
                ", displacement=" + displacement +
                '}';
    }
}
