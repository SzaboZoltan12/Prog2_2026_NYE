package hu.rushhour.io;

import hu.rushhour.model.Board;
import hu.rushhour.model.Orientation;
import hu.rushhour.model.Vehicle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** TXT pálya mentése és betöltése. */
public class BoardFileHandler {

  /** Betölti a fájlt. */
  public Board load(Path path) throws IOException {
    try (BufferedReader reader =
             Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
      return parse(reader);
    }
  }

  /** Betölti az alap pályát. */
  public Board loadDefault() throws IOException {
    InputStream stream = getClass()
        .getResourceAsStream("/default-board.txt");

    if (stream == null) {
      throw new IOException(
          "Hiányzik az alapértelmezett pályafájl.");
    }

    try (Reader reader = new InputStreamReader(
        stream, StandardCharsets.UTF_8)) {
      return parse(new BufferedReader(reader));
    }
  }

  /* Beolvassa és ellenőrzi a pálya adatait. */
  private Board parse(BufferedReader reader)
      throws IOException {

    List<String> lines = new ArrayList<>();
    String line;

    while ((line = reader.readLine()) != null) {
      line = line.trim();

      if (!line.isEmpty() && !line.startsWith("#")) {
        lines.add(line);
      }
    }

    if (lines.size() < 4) {
      throw new IOException(
          "Túl kevés sor van a pályafájlban.");
    }

    try {
      String[] dimensions = split(lines.get(0), 2);

      int rows = Integer.parseInt(dimensions[0]);
      int columns = Integer.parseInt(dimensions[1]);

      Board board = new Board(rows, columns);

      String[] exits = split(lines.get(1), 2);

      int humanRow = Integer.parseInt(exits[0]);
      int computerRow = Integer.parseInt(exits[1]);

      for (int i = 2; i < lines.size(); i++) {
        String[] parts = split(lines.get(i), 5);

        String id = parts[0];
        int length = Integer.parseInt(parts[1]);

        Orientation orientation =
            Orientation.valueOf(parts[2]);

        int row = Integer.parseInt(parts[3]);
        int column = Integer.parseInt(parts[4]);

        Vehicle vehicle = new Vehicle(
            id, length, orientation, row, column);

        board.addVehicle(vehicle);
      }

      board.configureExits(humanRow, computerRow);

      return board;

    } catch (IllegalArgumentException
             | IllegalStateException e) {
      throw new IOException(
          "Hibás pályafájl: " + e.getMessage(), e);
    }
  }

  /* Ellenőrzi, hogy elég adat van-e a sorban. */
  private String[] split(String line, int expected)
      throws IOException {

    String[] parts = line.trim().split("\\s+");

    if (parts.length != expected) {
      throw new IOException(
          "Hibás sor: " + line);
    }

    return parts;
  }

  /** Elmenti az adatokat a fájlba. */
  public void save(Path path, Board board)
      throws IOException {

    if (board.isGameOver()) {
      throw new IOException(
          "Befejezett pálya nem menthető.");
    }

    if (board.getHumanExit() == null
        || board.getComputerExit() == null) {
      throw new IOException(
          "A kijáratok nincsenek beállítva.");
    }

    List<String> lines = new ArrayList<>();

    lines.add(board.getRows() + " "
        + board.getColumns());

    lines.add(board.getHumanExit().row() + " "
        + board.getComputerExit().row());

    for (Vehicle vehicle : board.getVehicles()) {
      lines.add(vehicle.getId() + " "
          + vehicle.getLength() + " "
          + vehicle.getOrientation() + " "
          + vehicle.getRow() + " "
          + vehicle.getColumn());
    }

    Files.write(path, lines, StandardCharsets.UTF_8);
  }
}
