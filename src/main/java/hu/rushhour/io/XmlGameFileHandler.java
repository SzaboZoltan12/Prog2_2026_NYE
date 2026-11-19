package hu.rushhour.io;

import hu.rushhour.model.Board;
import hu.rushhour.model.GameState;
import hu.rushhour.model.Orientation;
import hu.rushhour.model.Vehicle;
import java.io.IOException;
import java.nio.file.Path;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/** XML mentés és betöltés. */
public class XmlGameFileHandler {

  /** Elmenti az adatokat a fájlba. */
  public void save(Path path, GameState state)
      throws IOException {

    try {
      Document document = DocumentBuilderFactory
          .newInstance()
          .newDocumentBuilder()
          .newDocument();

      final Board board = state.board();

      Element root = document.createElement("game");
      document.appendChild(root);

      root.setAttribute("player", state.playerName());

      root.setAttribute("humanTurn",
          Boolean.toString(state.humanTurn()));

      root.setAttribute("rows",
          Integer.toString(board.getRows()));

      root.setAttribute("columns",
          Integer.toString(board.getColumns()));

      root.setAttribute("humanExit",
          Integer.toString(board.getHumanExit().row()));

      root.setAttribute("computerExit",
          Integer.toString(board.getComputerExit().row()));

      for (Vehicle vehicle : board.getVehicles()) {

        Element element =
            document.createElement("vehicle");

        element.setAttribute("id", vehicle.getId());

        element.setAttribute("length",
            Integer.toString(vehicle.getLength()));

        element.setAttribute("orientation",
            vehicle.getOrientation().name());

        element.setAttribute("row",
            Integer.toString(vehicle.getRow()));

        element.setAttribute("column",
            Integer.toString(vehicle.getColumn()));

        root.appendChild(element);
      }

      var transformer = TransformerFactory
          .newInstance()
          .newTransformer();

      transformer.setOutputProperty(
          OutputKeys.INDENT, "yes");

      transformer.setOutputProperty(
          OutputKeys.ENCODING, "UTF-8");

      transformer.transform(
          new DOMSource(document),
          new StreamResult(path.toFile()));

    } catch (Exception e) {
      throw new IOException("Az XML mentése sikertelen.", e);
    }
  }

  /** Betölti a fájlt. */
  public GameState load(Path path)
      throws IOException {

    try {
      DocumentBuilderFactory factory =
          DocumentBuilderFactory.newInstance();

      factory.setFeature(
          XMLConstants.FEATURE_SECURE_PROCESSING, true);

      factory.setFeature(
          "http://apache.org/xml/features/disallow-doctype-decl",
          true);

      factory.setAttribute(
          XMLConstants.ACCESS_EXTERNAL_DTD, "");

      factory.setAttribute(
          XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

      Document document = factory
          .newDocumentBuilder()
          .parse(path.toFile());

      Element root = document.getDocumentElement();

      if (!root.getTagName().equals("game")) {
        throw new IOException("Hibás XML játékfájl.");
      }

      final String playerName = root.getAttribute("player");

      String turn = root.getAttribute("humanTurn");

      if (!turn.equals("true") && !turn.equals("false")) {
        throw new IOException("Hibás játékossorrend.");
      }

      boolean humanTurn = Boolean.parseBoolean(turn);

      int rows = Integer.parseInt(
          root.getAttribute("rows"));

      int columns = Integer.parseInt(
          root.getAttribute("columns"));

      Board board = new Board(rows, columns);

      NodeList vehicles =
          root.getElementsByTagName("vehicle");

      for (int i = 0; i < vehicles.getLength(); i++) {

        Element element = (Element) vehicles.item(i);

        Vehicle vehicle = new Vehicle(
            element.getAttribute("id"),
            Integer.parseInt(element.getAttribute("length")),
            Orientation.valueOf(
                element.getAttribute("orientation")),
            Integer.parseInt(element.getAttribute("row")),
            Integer.parseInt(element.getAttribute("column"))
        );

        board.addVehicle(vehicle);
      }

      board.configureExits(
          Integer.parseInt(root.getAttribute("humanExit")),
          Integer.parseInt(root.getAttribute("computerExit"))
      );

      return new GameState(playerName, board, humanTurn);

    } catch (Exception e) {
      throw new IOException("Az XML betöltése sikertelen.", e);
    }
  }
}
