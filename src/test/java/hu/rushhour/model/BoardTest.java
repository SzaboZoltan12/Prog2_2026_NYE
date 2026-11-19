package hu.rushhour.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

  private Board board;

  @BeforeEach
  void setUp() {
    board = new Board(6, 6);

    board.addVehicle(new Vehicle(
        "A", 2, Orientation.HORIZONTAL, 3, 1));

    board.addVehicle(new Vehicle(
        "B", 2, Orientation.HORIZONTAL, 1, 3));

    board.addVehicle(new Vehicle(
        "X", 2, Orientation.VERTICAL, 2, 4));

    board.configureExits(3, 1);
  }

  @Test
  void testBoardSize() {
    assertEquals(6, board.getRows());
    assertEquals(6, board.getColumns());
  }

  @Test
  void testVehicleCount() {
    assertEquals(3, board.getVehicles().size());
  }

  @Test
  void testFindVehicle() {
    assertNotNull(board.findVehicle("A"));
    assertNull(board.findVehicle("Z"));
  }

  @Test
  void testValidMove() {
    assertTrue(board.canMove(new Move("A", 1)));
  }

  @Test
  void testCollision() {
    assertFalse(board.canMove(new Move("A", 2)));
  }

  @Test
  void testOutOfBounds() {
    assertFalse(board.canMove(new Move("A", -5)));
    assertFalse(board.canMove(new Move("A", 10)));
  }

  @Test
  void testMoveVehicle() {
    board.moveVehicle(new Move("A", 1));

    assertEquals(2, board.findVehicle("A").getColumn());
  }

  @Test
  void testInvalidMove() {
    assertThrows(IllegalArgumentException.class,
        () -> board.moveVehicle(new Move("A", 2)));
  }

  @Test
  void testBlockedExit() {
    assertFalse(board.canExit("A"));
  }

  @Test
  void testFreeExit() {
    board.moveVehicle(new Move("X", 2));

    assertTrue(board.canExit("A"));
  }

  @Test
  void testWinner() {
    board.moveVehicle(new Move("X", 2));
    board.exitVehicle("A");

    assertTrue(board.isGameOver());
    assertEquals("A", board.getWinnerVehicleId());
    assertNull(board.findVehicle("A"));
  }

  @Test
  void testInvalidBoard() {
    assertThrows(IllegalArgumentException.class,
        () -> new Board(0, 6));
  }

  @Test
  void testDuplicateVehicle() {
    assertThrows(IllegalArgumentException.class,
        () -> board.addVehicle(new Vehicle(
            "A", 2, Orientation.HORIZONTAL, 0, 0)));
  }

  @Test
  void testOverlappingVehicle() {
    assertThrows(IllegalArgumentException.class,
        () -> board.addVehicle(new Vehicle(
            "C", 2, Orientation.HORIZONTAL, 3, 1)));
  }

  @Test
  void testComputerExit() {
    assertTrue(board.canExit("B"));

    board.exitVehicle("B");

    assertEquals("B", board.getWinnerVehicleId());
    assertTrue(board.isGameOver());
  }
}