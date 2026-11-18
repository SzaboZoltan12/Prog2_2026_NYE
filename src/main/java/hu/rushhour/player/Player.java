
package hu.rushhour.player;

public abstract class Player {

    private final String name;
    private final String targetVehicleId;

    protected Player(String name, String targetVehicleId) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Player name cannot be empty");
        }

        if (targetVehicleId == null
                || targetVehicleId.isBlank()) {
            throw new IllegalArgumentException(
                    "Target vehicle cannot be empty");
        }

        this.name = name;
        this.targetVehicleId = targetVehicleId;
    }

    public String getName() {
        return name;
    }

    public String getTargetVehicleId() {
        return targetVehicleId;
    }
}
