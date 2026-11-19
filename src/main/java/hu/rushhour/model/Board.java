package hu.rushhour.model;

import java.util.ArrayList;
import java.util.List;

public class Board {

  private final int rows;
  private final int columns;
  private final List<Vehicle> vehicles;

  private Exit humanExit;
  private Exit computerExit;
  private String winnerVehicleId;

  public Board(int rows, int columns) {
    if (rows <= 0 || columns <= 0) {
      throw new IllegalArgumentException(
          "Board dimensions must be positive");
    }

    this.rows = rows;
    this.columns = columns;
    this.vehicles = new ArrayList<>();
  }

  public int getRows() {
    return rows;
  }

  public int getColumns() {
    return columns;
  }

  public List<Vehicle> getVehicles() {
    return List.copyOf(vehicles);
  }

  // Jarmu hozzaadasa a tablahoz
  public void addVehicle(Vehicle vehicle) {
    if (vehicle == null) {
      throw new IllegalArgumentException(
          "Vehicle cannot be null");
    }

    if (isGameOver()) {
      throw new IllegalStateException(
          "The game is already finished");
    }

    for (Vehicle existing : vehicles) {
      if (existing.getId().equals(vehicle.getId())) {
        throw new IllegalArgumentException(
            "Vehicle ID already exists");
      }
    }

    if (!isWithinBounds(vehicle)) {
      throw new IllegalArgumentException(
          "Vehicle is outside the board");
    }

    for (Vehicle existing : vehicles) {
      if (overlaps(existing, vehicle)) {
        throw new IllegalArgumentException(
            "Vehicles cannot overlap");
      }
    }

    vehicles.add(vehicle);
  }

  // Ellenorzi, hogy a jarmu a tablan belul van-e
  private boolean isWithinBounds(Vehicle vehicle) {
    int row = vehicle.getRow();
    int column = vehicle.getColumn();

    if (row < 0 || column < 0) {
      return false;
    }

    if (vehicle.getOrientation()
        == Orientation.HORIZONTAL) {
      return row < rows
          && (long) column + vehicle.getLength()
          <= columns;
    }

    return column < columns
        && (long) row + vehicle.getLength()
        <= rows;
  }

  // Ellenorzi, hogy ket jarmu fedi-e egymast
  private boolean overlaps(Vehicle first, Vehicle second) {
    for (int i = 0; i < first.getLength(); i++) {
      int firstRow = first.getRow();
      int firstColumn = first.getColumn();

      if (first.getOrientation()
          == Orientation.HORIZONTAL) {
        firstColumn += i;
      } else {
        firstRow += i;
      }

      for (int j = 0; j < second.getLength(); j++) {
        int secondRow = second.getRow();
        int secondColumn = second.getColumn();

        if (second.getOrientation()
            == Orientation.HORIZONTAL) {
          secondColumn += j;
        } else {
          secondRow += j;
        }

        if (firstRow == secondRow
            && firstColumn == secondColumn) {
          return true;
        }
      }
    }

    return false;
  }

  // Jarmu keresese azonosito alapjan
  public Vehicle findVehicle(String id) {
    if (id == null) {
      return null;
    }

    for (Vehicle vehicle : vehicles) {
      if (vehicle.getId().equals(id)) {
        return vehicle;
      }
    }

    return null;
  }

  // Ellenorzi, hogy egy mozgatas szabalyos-e
  public boolean canMove(Move move) {
    if (move == null || isGameOver()) {
      return false;
    }

    Vehicle vehicle = findVehicle(move.getVehicleId());

    if (vehicle == null) {
      return false;
    }

    long newRow = vehicle.getRow();
    long newColumn = vehicle.getColumn();

    if (vehicle.getOrientation()
        == Orientation.HORIZONTAL) {
      newColumn += move.getDisplacement();
    } else {
      newRow += move.getDisplacement();
    }

    if (newRow < 0 || newColumn < 0
        || newRow > Integer.MAX_VALUE
        || newColumn > Integer.MAX_VALUE) {
      return false;
    }

    Vehicle movedVehicle = vehicle.withPosition(
        (int) newRow, (int) newColumn);

    if (!isWithinBounds(movedVehicle)) {
      return false;
    }

    int direction = Integer.signum(
        move.getDisplacement());

    long distance = Math.abs(
        (long) move.getDisplacement());

    // A teljes mozgatasi utvonal ellenorzese
    for (long step = 1; step <= distance; step++) {
      long stepRow = vehicle.getRow();
      long stepColumn = vehicle.getColumn();

      if (vehicle.getOrientation()
          == Orientation.HORIZONTAL) {
        stepColumn += step * direction;
      } else {
        stepRow += step * direction;
      }

      if (stepRow < 0 || stepColumn < 0
          || stepRow > Integer.MAX_VALUE
          || stepColumn > Integer.MAX_VALUE) {
        return false;
      }

      Vehicle intermediate = vehicle.withPosition(
          (int) stepRow, (int) stepColumn);

      if (!isWithinBounds(intermediate)) {
        return false;
      }

      for (Vehicle other : vehicles) {
        if (!other.getId().equals(vehicle.getId())
            && overlaps(intermediate, other)) {
          return false;
        }
      }
    }

    return true;
  }

  // Jarmu tenyleges mozgatasa
  public void moveVehicle(Move move) {
    if (!canMove(move)) {
      throw new IllegalArgumentException(
          "Invalid move");
    }

    Vehicle vehicle = findVehicle(move.getVehicleId());

    int newRow = vehicle.getRow();
    int newColumn = vehicle.getColumn();

    if (vehicle.getOrientation()
        == Orientation.HORIZONTAL) {
      newColumn += move.getDisplacement();
    } else {
      newRow += move.getDisplacement();
    }

    Vehicle movedVehicle = vehicle.withPosition(
        newRow, newColumn);

    int index = vehicles.indexOf(vehicle);
    vehicles.set(index, movedVehicle);
  }

  // Az A es B jarmu kijaratanak beallitasa
  public void configureExits(int aRow, int bRow) {
    if (aRow < 0 || aRow >= rows
        || bRow < 0 || bRow >= rows) {
      throw new IllegalArgumentException(
          "Exit row is outside the board");
    }

    Vehicle carA = findVehicle("A");
    Vehicle carB = findVehicle("B");

    if (carA == null || carB == null) {
      throw new IllegalStateException(
          "Both hero vehicles must exist");
    }

    if (carA.getOrientation() != Orientation.HORIZONTAL
        || carB.getOrientation() != Orientation.HORIZONTAL) {
      throw new IllegalArgumentException(
          "Hero vehicles must be horizontal");
    }

    if (carA.getRow() != aRow
        || carB.getRow() != bRow) {
      throw new IllegalArgumentException(
          "Hero vehicles must match their exit rows");
    }

    if (isGameOver()) {
      throw new IllegalStateException(
          "The game is already finished");
    }

    humanExit = new Exit("A", ExitSide.RIGHT, aRow);
    computerExit = new Exit("B", ExitSide.LEFT, bRow);
  }

  // Ellenorzi, hogy a celjarmu ki tud-e menni
  public boolean canExit(String vehicleId) {
    if (vehicleId == null || isGameOver()) {
      return false;
    }

    Exit exit;

    if (vehicleId.equals("A")) {
      exit = humanExit;
    } else if (vehicleId.equals("B")) {
      exit = computerExit;
    } else {
      return false;
    }

    if (exit == null) {
      return false;
    }

    Vehicle vehicle = findVehicle(vehicleId);

    if (vehicle == null
        || vehicle.getOrientation()
        != Orientation.HORIZONTAL
        || vehicle.getRow() != exit.row()) {
      return false;
    }

    int startColumn;
    int endColumn;

    if (exit.side() == ExitSide.RIGHT) {
      startColumn = vehicle.getColumn()
          + vehicle.getLength();
      endColumn = columns - 1;
    } else {
      startColumn = 0;
      endColumn = vehicle.getColumn() - 1;
    }

    for (int column = startColumn;
         column <= endColumn;
         column++) {

      for (Vehicle other : vehicles) {
        if (other.getId().equals(vehicleId)) {
          continue;
        }

        for (int i = 0; i < other.getLength(); i++) {
          int occupiedRow = other.getRow();
          int occupiedColumn = other.getColumn();

          if (other.getOrientation()
              == Orientation.HORIZONTAL) {
            occupiedColumn += i;
          } else {
            occupiedRow += i;
          }

          if (occupiedRow == exit.row()
              && occupiedColumn == column) {
            return false;
          }
        }
      }
    }

    return true;
  }

  // A celjarmu kivezetese a tablarol
  public void exitVehicle(String vehicleId) {
    if (!canExit(vehicleId)) {
      throw new IllegalArgumentException(
          "Vehicle cannot exit the board");
    }

    Vehicle vehicle = findVehicle(vehicleId);

    vehicles.remove(vehicle);
    winnerVehicleId = vehicleId;
  }

  // Ellenorzi, hogy befejezodott-e a jatek
  public boolean isGameOver() {
    return winnerVehicleId != null;
  }

  // Visszaadja a gyoztes celjarmu azonositojat
  public String getWinnerVehicleId() {
    return winnerVehicleId;
  }

  // A jatektabla kirajzolasa
  public void printBoard() {
    String[][] grid = new String[rows][columns];

    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < columns; col++) {
        grid[row][col] = ".";
      }
    }

    for (Vehicle vehicle : vehicles) {
      for (int i = 0; i < vehicle.getLength(); i++) {
        int row = vehicle.getRow();
        int col = vehicle.getColumn();

        if (vehicle.getOrientation()
            == Orientation.HORIZONTAL) {
          col += i;
        } else {
          row += i;
        }

        grid[row][col] = vehicle.getId();
      }
    }

    System.out.print("  ");

    for (int col = 0; col < columns; col++) {
      System.out.print(" " + (char) ('a' + col));
    }

    System.out.println();

    for (int row = 0; row < rows; row++) {
      System.out.print((row + 1) + " ");

      for (int col = 0; col < columns; col++) {
        System.out.print(" " + grid[row][col]);
      }

      System.out.println();
    }
  }

  public Exit getHumanExit() {
    return humanExit;
  }

  public Exit getComputerExit() {
    return computerExit;
  }
}
