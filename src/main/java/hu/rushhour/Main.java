
package hu.rushhour;

import hu.rushhour.io.BoardFileHandler;
import hu.rushhour.model.Board;

import java.io.IOException;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {

        BoardFileHandler fileHandler =
                new BoardFileHandler();

        try {
            // Az alapertelmezett palya beolvasasa
            Board board = fileHandler.loadDefault();

            System.out.println("Palya sikeresen betoltve!");
            board.printBoard();

            // Palya mentese egy kulso TXT fajlba
            Path output = Path.of("saved-board.txt");
            fileHandler.save(output, board);

            System.out.println(
                    "\nPalya elmentve: saved-board.txt");

            // A mentett palya visszaolvasasa
            Board loaded = fileHandler.load(output);

            System.out.println("\nVisszatoltott palya:");
            loaded.printBoard();

        } catch (IOException e) {
            System.out.println(
                    "Fajlkezelesi hiba: " + e.getMessage());
        }
    }
}
