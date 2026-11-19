package hu.rushhour.model;

import java.util.Objects;

public record GameState(
    String playerName,
    Board board,
    boolean humanTurn) {

  public GameState {
    if (playerName == null || playerName.isBlank()) {
      throw new IllegalArgumentException(
          "Player name cannot be empty");
    }

    Objects.requireNonNull(board, "Board is required");

    if (board.isGameOver()) {
      throw new IllegalArgumentException(
          "Game is already finished");
    }
  }
}