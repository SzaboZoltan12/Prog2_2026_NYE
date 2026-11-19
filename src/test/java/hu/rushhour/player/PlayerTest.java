package hu.rushhour.player;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void testHumanName() {
        HumanPlayer player = new HumanPlayer("TestPlayer");
        assertEquals("TestPlayer", player.getName());
    }

    @Test
    void testHumanTarget() {
        HumanPlayer player = new HumanPlayer("TestPlayer");
        assertEquals("A", player.getTargetVehicleId());
    }

    @Test
    void testComputerName() {
        ComputerPlayer player = new ComputerPlayer();
        assertEquals("Computer", player.getName());
    }

    @Test
    void testComputerTarget() {
        ComputerPlayer player = new ComputerPlayer();
        assertEquals("B", player.getTargetVehicleId());
    }

    @Test
    void testEmptyName() {
        assertThrows(IllegalArgumentException.class,
                () -> new HumanPlayer(""));
    }

    @Test
    void testNullName() {
        assertThrows(IllegalArgumentException.class,
                () -> new HumanPlayer(null));
    }

    @Test
    void testBlankName() {
        assertThrows(IllegalArgumentException.class,
                () -> new HumanPlayer("   "));
    }

    @Test
    void testEmptyTarget() {
        assertThrows(IllegalArgumentException.class,
                () -> new Player("TestPlayer", "") {});
    }

    @Test
    void testNullTarget() {
        assertThrows(IllegalArgumentException.class,
                () -> new Player("TestPlayer", null) {});
    }
}