package hu.rushhour.model;

import java.util.Objects;

public record Exit(String vehicleId, ExitSide side, int row) {

  public Exit {
    if (vehicleId == null || vehicleId.isBlank()) {
      throw new IllegalArgumentException(
          "Vehicle ID cannot be empty");
    }

    Objects.requireNonNull(side, "Exit side is required");

    if (row < 0) {
      throw new IllegalArgumentException(
          "Exit row cannot be negative");
    }
  }
}
