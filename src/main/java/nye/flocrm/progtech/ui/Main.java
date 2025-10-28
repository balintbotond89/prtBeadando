//Beadási határidő: december 5

package nye.flocrm.progtech.ui;
import nye.flocrm.progtech.model.Board;

@SuppressWarnings({"PMD.SystemPrintln"})
public class Main {

    // utility osztály (ne lehessen példányosítani)
    private Main() { }

    public static void main(String[] args) {
        Board board = new Board();
        board.print();

    }
}