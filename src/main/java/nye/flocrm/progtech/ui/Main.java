package nye.flocrm.progtech.ui;

/**
 * Hello world!
 *
 */
@SuppressWarnings({"PMD.SystemPrintln"})
public class Main {

    // utility osztály (ne lehessen példányosítani)
    private Main() { }

    public static void main(String[] args) {
        Board board = new Board();
        board.print();

    }
}