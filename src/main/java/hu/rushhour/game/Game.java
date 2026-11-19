package hu.rushhour.game;

import hu.rushhour.database.DatabaseManager;
import hu.rushhour.io.BoardFileHandler;
import hu.rushhour.io.XmlGameFileHandler;
import hu.rushhour.model.Board;
import hu.rushhour.model.GameState;
import hu.rushhour.model.Move;
import hu.rushhour.model.Orientation;
import hu.rushhour.model.Vehicle;
import hu.rushhour.player.ComputerPlayer;
import hu.rushhour.player.HumanPlayer;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A játék menetét kezeli. */
public class Game {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(Game.class);

  private final Scanner scanner;
  private final Random random;
  private final Path boardPath;
  private final DatabaseManager database;

  private Board board;
  private HumanPlayer human;
  private ComputerPlayer computer;

  private boolean humanNext = true;
  private boolean running = true;

  /** Beállítja a játékot. */
  public Game() {
    this(new Scanner(System.in),
        new Random(),
        Path.of("board.txt"),
        new DatabaseManager());
  }

  /** Beállítja a játékot. */
  public Game(Scanner scanner, Random random) {
    this(scanner, random, Path.of("board.txt"));
  }

  /** Beállítja a játékot. */
  public Game(Scanner scanner, Random random, Path boardPath) {
    this(scanner, random, boardPath, null);
  }

  /** Beállítja a játékot. */
  public Game(Scanner scanner, Random random,
              Path boardPath, DatabaseManager database) {
    this.scanner = scanner;
    this.random = random;
    this.boardPath = boardPath;
    this.database = database;
  }

  /** Futtatja a játékot a végéig. */
  public void start() {

    running = true;
    humanNext = true;

    LOGGER.info("Játék elindult");

    System.out.println("=== RUSH HOUR ===");
    System.out.print("Add meg a neved: ");

    String name = scanner.nextLine().trim();

    while (name.isBlank()) {
      System.out.print("A nev nem lehet ures: ");
      name = scanner.nextLine().trim();
    }

    human = new HumanPlayer(name);
    computer = new ComputerPlayer();

    LOGGER.info("Játékos csatlakozott: {}", human.getName());

    initializeBoard();

    System.out.println("\nUdvozollek, "
        + human.getName() + "!");

    System.out.println("A celod az A jarmu kijuttatasa.");
    System.out.println("A gep celja a B jarmu kijuttatasa.");

    while (running && !board.isGameOver()) {

      System.out.println();
      board.printBoard();

      if (humanNext) {
        humanTurn();
      } else {
        computerTurn();
        humanNext = true;
      }
    }

    if (!running) {
      System.out.println("Kileptel a jatekbol.");
      LOGGER.info("Játék bezárva");
      return;
    }

    System.out.println("\n=== JATEK VEGE ===");

    board.printBoard();

    String winnerId = board.getWinnerVehicleId();

    boolean humanWon =
        winnerId.equals(human.getTargetVehicleId());

    if (humanWon) {
      System.out.println("Gyoztes: " + human.getName());
      LOGGER.info("Győztes: {}", human.getName());
    } else {
      System.out.println("Gyoztes: " + computer.getName());
      LOGGER.info("Győztes: {}", computer.getName());
    }

    saveResult(humanWon);

    LOGGER.info("Játék vége");
  }

  /* Elmenti az eredményt az adatbázisba. */
  private void saveResult(boolean humanWon) {

    if (database == null) {
      return;
    }

    try {
      database.initialize();

      database.recordGame(human.getName(), humanWon);

      LOGGER.info("Eredmény elmentve: {}", human.getName());

      database.printLeaderboard();

    } catch (SQLException e) {

      LOGGER.error("Adatbázishiba", e);

      System.out.println(
          "Az eredmeny mentese vagy a ranglista betoltese sikertelen.");
    }
  }

  /* Betölti a pályát. Ha nincs saját pálya, az alapot használja. */
  private void initializeBoard() {

    BoardFileHandler handler = new BoardFileHandler();

    try {

      board = handler.load(boardPath);

      System.out.println("Egyedi palya betoltve.");

      LOGGER.info("Egyedi pálya betöltve: {}", boardPath);

    } catch (IOException e) {

      LOGGER.warn("Egyedi pálya nem érhető el: {}", boardPath);

      try {

        board = handler.loadDefault();

        System.out.println("Alapertelmezett palya betoltve.");

        LOGGER.info("Alapértelmezett pálya betöltve.");

      } catch (IOException ex) {

        LOGGER.error("A pálya betöltése sikertelen.", ex);

        throw new IllegalStateException(
            "A palya nem toltheto be.", ex);
      }
    }
  }

  /* Elmenti a pályát TXT fájlba. */
  private void saveBoard() {

    BoardFileHandler handler = new BoardFileHandler();

    Path savePath = boardPath.resolveSibling("saved-board.txt");

    try {

      handler.save(savePath, board);

      System.out.println("A palya sikeresen mentve!");
      System.out.println("Fajl: " + savePath);

      LOGGER.info("Pálya elmentve: {}", savePath);

    } catch (IOException e) {

      System.out.println("A palya mentese sikertelen!");

      LOGGER.error("A pálya mentése sikertelen.", e);
    }
  }

  /* Elmenti a játékot XML fájlba. */
  private void saveGame() {

    XmlGameFileHandler handler = new XmlGameFileHandler();

    Path path = boardPath.resolveSibling("saved-game.xml");

    try {

      GameState state = new GameState(
          human.getName(), board, humanNext);

      handler.save(path, state);

      System.out.println("A jatek sikeresen mentve!");
      System.out.println("Fajl: " + path);

      LOGGER.info("Játék elmentve: {}", path);

    } catch (IOException e) {

      System.out.println("A jatek mentese sikertelen!");

      LOGGER.error("A játék mentése sikertelen.", e);
    }
  }

  /* Visszatölti a mentett játékot. */
  private boolean loadGame() {

    XmlGameFileHandler handler = new XmlGameFileHandler();

    Path path = boardPath.resolveSibling("saved-game.xml");

    try {

      GameState state = handler.load(path);

      HumanPlayer loadedPlayer =
          new HumanPlayer(state.playerName());

      board = state.board();
      human = loadedPlayer;
      humanNext = state.humanTurn();

      System.out.println("A mentett jatek betoltve!");
      System.out.println("Jatekos: " + human.getName());

      LOGGER.info("Játék betöltve: {}", path);

      return true;

    } catch (IOException e) {

      System.out.println("A mentett jatek nem toltheto be!");

      LOGGER.error("A játék betöltése sikertelen.", e);

      return false;
    }
  }

  /* Kezeli a játékos parancsait. */
  private void humanTurn() {

    System.out.println("\n"
        + human.getName() + " kovetkezik!");

    while (true) {

      System.out.println("Parancs: JARMU ELMOZDULAS");
      System.out.println("Pelda: X 2");
      System.out.println("Gyozelem: EXIT A");
      System.out.println("Palya mentese: SAVE TXT");
      System.out.println("Jatek mentese: SAVE XML");
      System.out.println("Jatek betoltese: LOAD XML");
      System.out.println("Kilepes: QUIT");
      System.out.print("> ");

      if (!scanner.hasNextLine()) {
        running = false;
        return;
      }

      String input = scanner.nextLine().trim();

      if (input.equalsIgnoreCase("QUIT")) {
        running = false;
        return;
      }

      String[] parts = input.split("\\s+");

      if (parts.length != 2) {

        System.out.println("Hibas parancs!");

        LOGGER.warn("Hibás parancs: {}", input);

        continue;
      }

      String vehicleId = parts[0].toUpperCase(Locale.ROOT);

      if (vehicleId.equals("SAVE")) {

        if (parts[1].equalsIgnoreCase("TXT")) {
          saveBoard();
        } else if (parts[1].equalsIgnoreCase("XML")) {
          saveGame();
        } else {
          System.out.println("Ismeretlen mentesi formatum!");
        }

        continue;
      }

      if (vehicleId.equals("LOAD")) {

        if (parts[1].equalsIgnoreCase("XML")) {

          if (loadGame()) {
            return;
          }

        } else {
          System.out.println("Ismeretlen betoltesi formatum!");
        }

        continue;
      }

      if (vehicleId.equals("EXIT")) {

        String target = parts[1].toUpperCase(Locale.ROOT);

        if (!target.equals("A")) {

          System.out.println(
              "Csak az A jarmuvel nyerhetsz!");

          LOGGER.warn("Hibás kijárati kísérlet: {}", target);

          continue;
        }

        if (board.canExit(target)) {

          board.exitVehicle(target);

          LOGGER.info("A játékos kijutott az A járművel.");

          return;
        }

        System.out.println(
            "A kijarathoz vezeto ut nem szabad!");

        LOGGER.warn("A játékos kijárata el van zárva.");

        continue;
      }

      int displacement;

      try {

        displacement = Integer.parseInt(parts[1]);

      } catch (NumberFormatException e) {

        System.out.println(
            "Az elmozdulas egesz szam legyen!");

        LOGGER.warn("Hibás elmozdulás: {}", parts[1]);

        continue;
      }

      Move move;

      try {

        move = new Move(vehicleId, displacement);

      } catch (IllegalArgumentException e) {

        System.out.println(
            "Ervenytelen lepes: " + e.getMessage());

        LOGGER.warn("Érvénytelen lépés: {}", e.getMessage());

        continue;
      }

      if (board.canMove(move)) {

        board.moveVehicle(move);

        System.out.println("Sikeres lepes!");

        LOGGER.info("Játékos lépése: {} {}",
            vehicleId, displacement);

        humanNext = false;

        return;
      }

      System.out.println(
          "A lepes nem hajthato vegre!");

      LOGGER.warn("Szabálytalan játékoslépés: {} {}",
          vehicleId, displacement);
    }
  }

  /* A gép véletlenszerű szabályos lépést választ. */
  private void computerTurn() {

    System.out.println("\nComputer kovetkezik...");

    List<Vehicle> candidates =
        new ArrayList<>(board.getVehicles());

    while (!candidates.isEmpty()) {

      int index = random.nextInt(candidates.size());

      Vehicle vehicle = candidates.remove(index);

      List<Move> legalMoves = new ArrayList<>();

      int limit = vehicle.getOrientation()
          == Orientation.HORIZONTAL
          ? board.getColumns()
          : board.getRows();

      for (int amount = -limit; amount <= limit; amount++) {

        if (amount == 0) {
          continue;
        }

        Move move = new Move(vehicle.getId(), amount);

        if (board.canMove(move)) {
          legalMoves.add(move);
        }
      }

      boolean canWin = vehicle.getId()
          .equals(computer.getTargetVehicleId())
          && board.canExit(vehicle.getId());

      int optionCount = legalMoves.size()
          + (canWin ? 1 : 0);

      if (optionCount == 0) {
        continue;
      }

      int choice = random.nextInt(optionCount);

      if (canWin && choice == legalMoves.size()) {

        board.exitVehicle(vehicle.getId());

        System.out.println(
            "Computer kijutott a B jarmuvel!");

        LOGGER.info("A gép kijutott a B járművel.");

        return;
      }

      Move selected = legalMoves.get(choice);

      board.moveVehicle(selected);

      System.out.println("Computer lepese: "
          + selected.getVehicleId() + " "
          + selected.getDisplacement());

      LOGGER.info("A gép lépése: {} {}",
          selected.getVehicleId(),
          selected.getDisplacement());

      return;
    }

    System.out.println(
        "A gep nem talalt szabalyos lepest.");

    LOGGER.warn("A gép nem tud szabályosan lépni.");
  }
}
