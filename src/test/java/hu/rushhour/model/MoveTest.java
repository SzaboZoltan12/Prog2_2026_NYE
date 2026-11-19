package hu.rushhour.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MoveTest {

    @Test
    void testVehicleId() {
        Move move = new Move("A", 2);
        assertEquals("A", move.getVehicleId());
    }

    @Test
    void testDisplacement() {
        Move move = new Move("A", 2);
        assertEquals(2, move.getDisplacement());
    }

    @Test
    void testNegativeDisplacement() {
        Move move = new Move("B", -2);
        assertEquals(-2, move.getDisplacement());
    }

    @Test
    void testZeroDisplacement() {
        assertThrows(IllegalArgumentException.class,
                () -> new Move("A", 0));
    }

    @Test
    void testEquals() {
        assertEquals(new Move("A", 2),
                     new Move("A", 2));
    }

    @Test
    void testNotEquals() {
        assertNotEquals(new Move("A", 2),
                        new Move("B", 3));
    }

    @Test
    void testHashCode() {
        assertEquals(new Move("A", 2).hashCode(),
                     new Move("A", 2).hashCode());
    }
}