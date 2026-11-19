package hu.rushhour.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VehicleTest {

    @Test
    void testVehicleProperties() {
        Vehicle vehicle = new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 3, 1);

        assertEquals("A", vehicle.getId());
        assertEquals(2, vehicle.getLength());
        assertEquals(Orientation.HORIZONTAL, vehicle.getOrientation());
        assertEquals(3, vehicle.getRow());
        assertEquals(1, vehicle.getColumn());
    }

    @Test
    void testInvalidLength() {
        assertThrows(IllegalArgumentException.class,
                () -> new Vehicle("A", 1,
                        Orientation.HORIZONTAL, 0, 0));
    }

    @Test
    void testNegativeRow() {
        assertThrows(IllegalArgumentException.class,
                () -> new Vehicle("A", 2,
                        Orientation.HORIZONTAL, -1, 0));
    }

    @Test
    void testNegativeColumn() {
        assertThrows(IllegalArgumentException.class,
                () -> new Vehicle("A", 2,
                        Orientation.HORIZONTAL, 0, -1));
    }

    @Test
    void testWithPosition() {
        Vehicle vehicle = new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 3, 1);

        Vehicle moved = vehicle.withPosition(3, 2);

        assertEquals(3, moved.getRow());
        assertEquals(2, moved.getColumn());
        assertEquals(1, vehicle.getColumn());
        assertNotSame(vehicle, moved);
    }

    @Test
    void testEquals() {
        Vehicle a = new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 0, 0);

        Vehicle b = new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 0, 0);

        assertEquals(a, b);
    }

    @Test
    void testNotEquals() {
        Vehicle a = new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 0, 0);

        Vehicle b = new Vehicle(
                "B", 2, Orientation.VERTICAL, 1, 1);

        assertNotEquals(a, b);
    }

    @Test
    void testHashCode() {
        Vehicle a = new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 0, 0);

        Vehicle b = new Vehicle(
                "A", 2, Orientation.HORIZONTAL, 0, 0);

        assertEquals(a.hashCode(), b.hashCode());
    }
}