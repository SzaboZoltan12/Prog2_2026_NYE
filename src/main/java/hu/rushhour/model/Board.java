package hu.rushhour.model;

import java.util.ArrayList;
import java.util.List;

/** A pálya és a lépések kezelése. */
public class Board {

  private final int rows;
  private final int columns;
  private final List<Vehicle> vehicles;

  private Exit humanExit;
  private Exit computerExit;
  private String winnerVehicleId;

  /** Létrehozza a pályát és ellenőrzi a méretét. */
  public Board(int rows, int columns) {
    if (rows <= 0 || columns <= 0) {
      throw new IllegalArgumentException(
          "A pálya mérete pozitív kell legyen.");
    }

    this.rows = rows;
    this.columns = columns;
    this.vehicles = new ArrayList<>();
  }

  /** Visszaadja a sorok számát. */
  public int getRows() {
    return rows;
  }

  /** Visszaadja az oszlopok számát. */
  public int getColumns() {
    return columns;
  }

  /** Visszaadja a járműveket. */
  public List<Vehicle> getVehicles() {
    return List.copyOf(vehicles);
  }

  /** Ellenőrzi és hozzáadja a járművet. */
  public void addVehicle(Vehicle vehicle) {
    if (vehicle == null) {
      throw new IllegalArgumentException(
          "A jármű nem lehet null.");
    }

    if (isGameOver()) {
      throw new IllegalStateException(
          "A játék már véget ért.");
    }

    for (Vehicle existing : vehicles) {
      if (existing.getId().equals(vehicle.getId())) {
        throw new IllegalArgumentException(
            "Ez a járműazonosító már létezik.");
      }
    }

    if (!isWithinBounds(vehicle)) {
      throw new IllegalArgumentException(
          "A jármű a pályán kívül van.");
    }

    for (Vehicle existing : vehicles) {
      if (overlaps(existing, vehicle)) {
        throw new IllegalArgumentException(
            "A járművek nem fedhetik egymást.");
      }
    }

    vehicles.add(vehicle);
  }

  /* Ellenőrzi, hogy a jármű a pályán belül van-e. */
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

  /* Ellenőrzi, hogy két jármű ütközik-e. */
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

  /** Megkeresi a járművet az azonosítója alapján. */
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

  /** Ellenőrzi a lépés teljes útját. */
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

    /* A lépés teljes útjának ellenőrzése. */
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

  /** Elmozdítja a járművet. */
  public void moveVehicle(Move move) {
    if (!canMove(move)) {
      throw new IllegalArgumentException(
          "Érvénytelen lépés.");
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

  /** Beállítja a kijáratokat. */
  public void configureExits(int humanRow, int computerRow) {
    if (humanRow < 0 || humanRow >= rows
        || computerRow < 0 || computerRow >= rows) {
      throw new IllegalArgumentException(
          "A kijárat sora a pályán kívül van.");
    }

    Vehicle carA = findVehicle("A");
    Vehicle carB = findVehicle("B");

    if (carA == null || carB == null) {
      throw new IllegalStateException(
          "Mindkét céljárműnek léteznie kell.");
    }

    if (carA.getOrientation() != Orientation.HORIZONTAL
        || carB.getOrientation() != Orientation.HORIZONTAL) {
      throw new IllegalArgumentException(
          "A céljárművek vízszintesek kell legyenek.");
    }

    if (carA.getRow() != humanRow
        || carB.getRow() != computerRow) {
      throw new IllegalArgumentException(
          "A céljárműveknek a kijárat sorában kell lenniük.");
    }

    if (isGameOver()) {
      throw new IllegalStateException(
          "A játék már véget ért.");
    }

    humanExit = new Exit("A", ExitSide.RIGHT, humanRow);
    computerExit = new Exit("B", ExitSide.LEFT, computerRow);
  }

  /** Ellenőrzi, hogy szabad-e a kijárat. */
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

  /** Kivezeti a járművet és beállítja a győztest. */
  public void exitVehicle(String vehicleId) {
    if (!canExit(vehicleId)) {
      throw new IllegalArgumentException(
          "A jármű nem tud kijutni a pályáról.");
    }

    Vehicle vehicle = findVehicle(vehicleId);

    vehicles.remove(vehicle);
    winnerVehicleId = vehicleId;
  }

  /** Ellenőrzi, hogy vége van-e a játéknak. */
  public boolean isGameOver() {
    return winnerVehicleId != null;
  }

  /** Visszaadja a győztes jármű azonosítóját. */
  public String getWinnerVehicleId() {
    return winnerVehicleId;
  }

  /** Kirajzolja a pályát. */
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

  /** A játékos kijárata. */
  public Exit getHumanExit() {
    return humanExit;
  }

  /** A gép kijárata. */
  public Exit getComputerExit() {
    return computerExit;
  }
}
