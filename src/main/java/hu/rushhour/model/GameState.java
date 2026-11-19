package hu.rushhour.model;

import java.util.Objects;

/** A mentendő játék adatai. */
public record GameState(
    String playerName,
    Board board,
    boolean humanTurn) {

  /** Ellenőrzi a mentendő játékot. */
  public GameState {
    if (playerName == null || playerName.isBlank()) {
      throw new IllegalArgumentException(
          "A játékos neve nem lehet üres.");
    }

    Objects.requireNonNull(board, "A pályát meg kell adni.");

    if (board.isGameOver()) {
      throw new IllegalArgumentException(
          "A játék már véget ért.");
    }
  }
}
