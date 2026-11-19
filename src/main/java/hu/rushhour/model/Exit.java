package hu.rushhour.model;

import java.util.Objects;

/** A kijárat adatai. */
public record Exit(String vehicleId, ExitSide side, int row) {

  /** Ellenőrzi a kijárat adatait. */
  public Exit {
    if (vehicleId == null || vehicleId.isBlank()) {
      throw new IllegalArgumentException(
          "A járműazonosító nem lehet üres.");
    }

    Objects.requireNonNull(side, "A kijárat oldalát meg kell adni.");

    if (row < 0) {
      throw new IllegalArgumentException(
          "A kijárat sora nem lehet negatív.");
    }
  }
}
