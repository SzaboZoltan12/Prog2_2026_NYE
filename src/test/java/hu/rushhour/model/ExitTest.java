package hu.rushhour.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExitTest {

  @Test
  void testValidExit() {
    Exit exit = new Exit("A", ExitSide.RIGHT, 3);

    assertEquals("A", exit.vehicleId());
    assertEquals(ExitSide.RIGHT, exit.side());
    assertEquals(3, exit.row());
  }

  @Test
  void testEmptyVehicleId() {
    assertThrows(IllegalArgumentException.class,
        () -> new Exit("", ExitSide.RIGHT, 3));
  }

  @Test
  void testNullVehicleId() {
    assertThrows(IllegalArgumentException.class,
        () -> new Exit(null, ExitSide.RIGHT, 3));
  }

  @Test
  void testNullSide() {
    assertThrows(NullPointerException.class,
        () -> new Exit("A", null, 3));
  }

  @Test
  void testNegativeRow() {
    assertThrows(IllegalArgumentException.class,
        () -> new Exit("A", ExitSide.RIGHT, -1));
  }

  @Test
  void testEqualExits() {
    Exit first = new Exit("A", ExitSide.RIGHT, 3);
    Exit second = new Exit("A", ExitSide.RIGHT, 3);

    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
  }
}