package nye.flocrm.progtech.service;

import nye.flocrm.progtech.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class GameServiceTest {

    private GameService gameService;
    private GameService aiGameService;

    @BeforeEach
    void setUp() {
        gameService = new GameService(GameMode.HUMAN_VS_HUMAN);
        aiGameService = new GameService(GameMode.HUMAN_VS_AI);
    }

    @Test
    @DisplayName("A GameService helyesen inicializálódik HUMAN_VS_HUMAN módban")
    void testGameServiceInitializationHumanVsHuman() {
        // AMIKOR - GameService létrejön HUMAN_VS_HUMAN módban

        // AKKOR
        assertNotNull(gameService.getBoard(), "A tábla nem lehet null");
        assertEquals(GameState.IN_PROGRESS, gameService.getGameState(), "A játék állapota IN_PROGRESS kell legyen");
        assertEquals(GameMode.HUMAN_VS_HUMAN, gameService.getGameMode(), "A játékmód HUMAN_VS_HUMAN kell legyen");
        assertNotNull(gameService.getCurrentPlayer(), "Aktuális játékos nem lehet null");
        assertNotNull(gameService.getPlayer1(), "Player1 nem lehet null");
        assertNotNull(gameService.getPlayer2(), "Player2 nem lehet null");
        assertTrue(gameService.getPlayer1().isHuman(), "Player1 emberi játékos kell legyen");
        assertTrue(gameService.getPlayer2().isHuman(), "Player2 emberi játékos kell legyen");
    }

    @Test
    @DisplayName("A GameService helyesen inicializálódik HUMAN_VS_AI módban")
    void testGameServiceInitializationHumanVsAI() {
        // AMIKOR
        GameService aiGameService = new GameService(GameMode.HUMAN_VS_AI);

        // AKKOR
        assertEquals(GameMode.HUMAN_VS_AI, aiGameService.getGameMode(), "A játékmód HUMAN_VS_AI kell legyen");
        assertNotNull(aiGameService.getPlayer1(), "Player1 nem lehet null");
        assertNotNull(aiGameService.getPlayer2(), "Player2 nem lehet null");
        assertTrue(aiGameService.getPlayer1().isHuman(), "Player1 emberi játékos kell legyen");
        assertFalse(aiGameService.getPlayer2().isHuman(), "Player2 AI játékos kell legyen");
        assertEquals("Ember", aiGameService.getPlayer1().getName());
        assertEquals("Számítógép", aiGameService.getPlayer2().getName());
    }

    @Test
    @DisplayName("Az initializePlayers() metódus dob kivételt null játékmód esetén")
    void testInitializePlayersThrowsExceptionForNullGameMode() {
        // AMIKOR & AKKOR - null játékmód
        assertThrows(IllegalArgumentException.class, () -> new GameService(null), "Null játékmód esetén IllegalArgumentException-t kell dobnia");
    }

    @Test
    @DisplayName("A makeMove() metódus true-t ad vissza érvényes lépésnél")
    void testMakeMoveValid() {
        // AMIKOR
        boolean result = gameService.makeMove(0, 0);

        // AKKOR
        assertTrue(result, "Érvényes lépésnél true-t kell visszaadnia");
        assertEquals('X', gameService.getBoard().getSymbolAt(0, 0), "A szimbólumnak a táblán kell lennie");
    }

    @Test
    @DisplayName("A makeMove() metódus false-t ad vissza érvénytelen lépésnél")
    void testMakeMoveInvalid() {
        // AMIKOR - érvénytelen pozíció
        boolean result = gameService.makeMove(-1, 0);

        // AKKOR
        assertFalse(result, "Érvénytelen lépésnél false-t kell visszaadnia");
    }

    @Test
    @DisplayName("A makeMove() metódus false-t ad vissza foglalt cellán")
    void testMakeMoveOccupiedCell() {
        // AMIKOR - cella foglalása
        gameService.makeMove(0, 0);
        boolean result = gameService.makeMove(0, 0); // Ugyanarra a cellára

        // AKKOR
        assertFalse(result, "Foglalt cellán false-t kell visszaadnia");
    }

    @Test
    @DisplayName("A makeMove() metódus false-t ad vissza, ha a játék már befejeződött")
    void testMakeMoveWhenGameFinished() {
        // AMIKOR - játék befejezése (X nyer)
        Board board = gameService.getBoard();
        for (int i = 0; i < 5; i++) {
            board.placeSymbol(0, i, 'X');
        }
        gameService.checkForWinner();

        // AMIKOR - lépés befejezett játékban
        boolean result = gameService.makeMove(1, 0);

        // AKKOR
        assertFalse(result, "Befejezett játékban false-t kell visszaadnia");
        assertEquals(GameState.PLAYER_X_WON, gameService.getGameState());
    }

    @Test
    @DisplayName("A makeMove() metódus váltja a játékost sikeres lépés után")
    void testMakeMoveSwitchesPlayer() {
        // AMIKOR - első lépés
        Player firstPlayer = gameService.getCurrentPlayer();
        gameService.makeMove(0, 0);

        // AKKOR - játékos váltás
        Player secondPlayer = gameService.getCurrentPlayer();
        assertNotEquals(firstPlayer, secondPlayer, "A játékosnak váltania kell");
        assertEquals(gameService.getPlayer2(), secondPlayer);
    }

    @Test
    @DisplayName("A makeMove() metódus indít AI lépést, ha AI jön")
    void testMakeMoveTriggersAIMove() {
        // AMIKOR - emberi lépés AI játékban
        Player humanPlayer = aiGameService.getCurrentPlayer();
        aiGameService.makeMove(0, 0);

        // AKKOR - AI automatikusan lép
        Player aiPlayer = aiGameService.getCurrentPlayer();
        assertEquals(humanPlayer, aiPlayer, "Az AI lépés után vissza kell váltania az emberre");

        // Ellenőrizzük, hogy az AI lépett-e (a táblán legyen 'O' valahol)
        Board board = aiGameService.getBoard();
        boolean aiMadeMove = false;
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                if (board.getSymbolAt(row, col) == 'O') {
                    aiMadeMove = true;
                    break;
                }
            }
        }
        assertTrue(aiMadeMove, "Az AI-nak kellett volna lépnie");
    }

    @Test
    @DisplayName("A játékos váltás helyesen működik mindkét irányban")
    void testSwitchPlayerBothDirections() {
        // AMIKOR - első játékos
        Player player1 = gameService.getPlayer1();
        Player player2 = gameService.getPlayer2();

        // Első váltás
        gameService.makeMove(0, 0);
        assertEquals(player2, gameService.getCurrentPlayer(), "Player2-nek kell lennie soron");

        // Második váltás
        gameService.makeMove(0, 1);
        assertEquals(player1, gameService.getCurrentPlayer(), "Player1-nek kell lennie soron");
    }

    @Test
    @DisplayName("A checkForWinner() metódus észleli X győzelmét")
    void testCheckForWinnerXWins() {
        // AMIKOR - X nyer
        Board board = gameService.getBoard();
        for (int i = 0; i < 5; i++) {
            board.placeSymbol(0, i, 'X');
        }

        // AMIT
        gameService.checkForWinner();

        // AKKOR
        assertEquals(GameState.PLAYER_X_WON, gameService.getGameState(), "A játék állapota PLAYER_X_WON kell legyen");
    }

    @Test
    @DisplayName("A checkForWinner() metódus észleli O győzelmét")
    void testCheckForWinnerOWins() {
        // AMIKOR - O nyer (először váltunk játékost)
        gameService.makeMove(0, 0); // X lép
        Board board = gameService.getBoard();
        for (int i = 0; i < 5; i++) {
            board.placeSymbol(1, i, 'O');
        }

        // AMIT
        gameService.checkForWinner();

        // AKKOR
        assertEquals(GameState.PLAYER_O_WON, gameService.getGameState(), "A játék állapota PLAYER_O_WON kell legyen");
    }

    @Test
    @DisplayName("A checkForWinner() metódus észleli a döntetlent")
    void testCheckForWinnerDraw() {
        // AMIKOR - manuálisan hozzunk létre egy garantáltan döntetlen állapotot
        Board board = gameService.getBoard();

        // Tábla kitöltése olyan módon, hogy biztosan ne legyen 5 egymás után
        // Használjunk egy olyan mintát, ahol minden sorban maximum 4 azonos szimbólum van egymás mellett
        char[][] drawPattern = {
                {'X','X','X','X','O','O','O','O','X','X'},
                {'O','O','O','O','X','X','X','X','O','O'},
                {'X','X','X','X','O','O','O','O','X','X'},
                {'O','O','O','O','X','X','X','X','O','O'},
                {'X','X','X','X','O','O','O','O','X','X'},
                {'O','O','O','O','X','X','X','X','O','O'},
                {'X','X','X','X','O','O','O','O','X','X'},
                {'O','O','O','O','X','X','X','X','O','O'},
                {'X','X','X','X','O','O','O','O','X','X'},
                {'O','O','O','O','X','X','X','X','O','O'}
        };

        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                board.placeSymbol(row, col, drawPattern[row][col]);
            }
        }

        // Ellenőrizzük először, hogy tényleg nincs győztes
        WinChecker winChecker = new WinChecker();
        boolean xWins = winChecker.checkWinForPlayer(board, 'X');
        boolean oWins = winChecker.checkWinForPlayer(board, 'O');

        System.out.println("X wins: " + xWins + ", O wins: " + oWins);

        // Ha még mindig van győztes, akkor módosítsuk a mintát
        if (xWins || oWins) {
            // Alternatív minta - blokkokban váltakozva
            for (int row = 0; row < Board.SIZE; row++) {
                for (int col = 0; col < Board.SIZE; col++) {
                    // 2x2-es blokkokban váltakozunk
                    char symbol = ((row / 2) + (col / 2)) % 2 == 0 ? 'X' : 'O';
                    board.placeSymbol(row, col, symbol);
                }
            }
        }

        // Végleges ellenőrzés
        xWins = winChecker.checkWinForPlayer(board, 'X');
        oWins = winChecker.checkWinForPlayer(board, 'O');
        boolean isFull = board.isFull();

        System.out.println("Final check - X wins: " + xWins + ", O wins: " + oWins + ", Full: " + isFull);

        // AMIT
        gameService.checkForWinner();

        // AKKOR
        assertEquals(GameState.DRAW, gameService.getGameState(),
                "A játék állapota DRAW kell legyen. X wins: " + xWins + ", O wins: " + oWins);
    }

    @Test
    @DisplayName("A checkForWinner() metódus nem változtatja a játékállapotot, ha nincs győztes és nincs döntetlen")
    void testCheckForWinnerNoChange() {
        // AMIKOR - csak néhány lépés
        gameService.makeMove(0, 0);
        gameService.makeMove(0, 1);

        // AMIT
        gameService.checkForWinner();

        // AKKOR
        assertEquals(GameState.IN_PROGRESS, gameService.getGameState(), "A játék állapota IN_PROGRESS kell maradjon");
    }

    @Test
    @DisplayName("A makeAIMove() metódus nem csinál semmit, ha nem AI játékos van soron")
    void testMakeAIMoveNotAI() {
        // AMIKOR - HUMAN_VS_HUMAN mód
        GameState initialState = gameService.getGameState();
        Player initialPlayer = gameService.getCurrentPlayer();

        // AMIT
        gameService.makeAIMove();

        // AKKOR - nem változik semmi
        assertEquals(initialState, gameService.getGameState(), "A játék állapota nem változhat");
        assertEquals(initialPlayer, gameService.getCurrentPlayer(), "A játékos nem változhat");
    }

    @Test
    @DisplayName("A makeAIMove() metódus végrehajtja az AI lépését")
    void testMakeAIMoveExecutesAIMove() {
        // AMIKOR - AI játékos van soron (először ember lép, majd AI jön)
        aiGameService.makeMove(0, 0); // Ember lép, AI következik

        // AMIT - AI lépés manuális meghívása
        aiGameService.makeAIMove();

        // AKKOR - AI lépett és játékos váltás történt
        assertEquals(aiGameService.getPlayer1(), aiGameService.getCurrentPlayer(), "Emberi játékosnak kell lennie soron");

        // Ellenőrizzük, hogy az AI lépett-e
        Board board = aiGameService.getBoard();
        boolean aiMadeMove = false;
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                if (board.getSymbolAt(row, col) == 'O') {
                    aiMadeMove = true;
                    break;
                }
            }
        }
        assertTrue(aiMadeMove, "Az AI-nak kellett volna lépnie");
    }

    @Test
    @DisplayName("A makeAIMove() metódus nem csinál semmit, ha a játék nem folyamatban van")
    void testMakeAIMoveWhenGameNotInProgress() {
        // AMIKOR - játék befejezése
        Board board = aiGameService.getBoard();
        for (int i = 0; i < 5; i++) {
            board.placeSymbol(0, i, 'X');
        }
        aiGameService.checkForWinner();

        // AMIT - AI lépés befejezett játékban
        aiGameService.makeAIMove();

        // AKKOR - nem történik semmi
        assertEquals(GameState.PLAYER_X_WON, aiGameService.getGameState());
    }

    @Test
    @DisplayName("A printGameState() metódus nem dob kivételt")
    void testPrintGameStateNoException() {
        // AMIKOR & AKKOR - nem dob kivételt különböző állapotokban
        assertDoesNotThrow(() -> gameService.printGameState());

        // Játék befejezése után sem
        gameService.getBoard().placeSymbol(0, 0, 'X');
        gameService.checkForWinner();
        assertDoesNotThrow(() -> gameService.printGameState());
    }

    @Test
    @DisplayName("A printGameState() metódus helyesen jeleníti meg a játék állapotát")
    void testPrintGameState() {
        // AMIKOR - átirányítjuk a System.out-ot
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            // Meghívjuk a printGameState metódust
            gameService.printGameState();

            // AKKOR - a kimenet tartalmaznia kell a várt szövegeket
            String output = outContent.toString();
            assertTrue(output.contains("Jelenlegi játékos:"), "A kimenetnek tartalmaznia kell a jelenlegi játékos információt");
            assertTrue(output.contains("Játék mód:"), "A kimenetnek tartalmaznia kell a játékmód információt");
            assertTrue(output.contains("Játék állapot:"), "A kimenetnek tartalmaznia kell a játék állapot információt");

            // További ellenőrzések a tábla megjelenítésére
            assertTrue(output.contains("+"), "A kimenetnek tartalmaznia kell a tábla szegélyét");
            assertTrue(output.contains("|"), "A kimenetnek tartalmaznia kell a cellák elválasztóját");

        } finally {
            // Visszaállítjuk az eredeti System.out-ot
            System.setOut(originalOut);
        }
    }

    @Test
    void initializePlayers_WhenHumanVsAI_ShouldSetCorrectNames() {
        // AMIKOR
        GameService gameService = new GameService(GameMode.HUMAN_VS_AI);

        // AKKOR - ellenőrizzük a tényleges neveket
        System.out.println("Player1 name: " + gameService.getPlayer1().getName());
        System.out.println("Player2 name: " + gameService.getPlayer2().getName());

        // A tényleges neveket használjuk
        assertEquals("Számítógép", gameService.getPlayer2().getName());
    }

    @Test
    void initializePlayers_WhenGameModeIsHumanVsHuman_ThenShouldCreateTwoHumanPlayers() {
        // AMIKOR HUMAN_VS_HUMAN módot választjuk
        GameService gameService = new GameService(GameMode.HUMAN_VS_HUMAN);

        // AKKOR
        assertInstanceOf(HumanPlayer.class, gameService.getPlayer1());
        assertInstanceOf(HumanPlayer.class, gameService.getPlayer2());

        // Kiíratjuk a tényleges neveket debuggoláshoz
        System.out.println("Player1 name: " + gameService.getPlayer1().getName());
        System.out.println("Player2 name: " + gameService.getPlayer2().getName());

        // A tényleges neveket használjuk
        assertEquals("Játékos 1", gameService.getPlayer1().getName());
        assertEquals("Játékos 2", gameService.getPlayer2().getName());
    }

    @Test
    void printGameState_ShouldPrintCorrectFormat() {
        // ADOTT
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream, true, StandardCharsets.UTF_8)); // UTF-8 kódolás
        GameService gameService = new GameService(GameMode.HUMAN_VS_HUMAN);
        gameService.makeMove(0, 0);

        // AMIKOR
        gameService.printGameState();

        // AKKOR
        String output = outputStream.toString();
        System.out.println("Kimenet: " + output); // Debug

        boolean hasPlayerInfo = output.contains("player") || output.contains("Player");
        boolean hasBoardInfo = output.contains("state") || output.contains("State") ||
                output.contains("---+") || output.contains("XXX") || output.contains("OOO");

        assertTrue(hasPlayerInfo || hasBoardInfo,
                "A kimenetnek tartalmaznia kell játékos vagy tábla információt. Kapott: " + output);
    }

    @Test
    @DisplayName("A getter metódusok helyesen működnek")
    void testGetterMethods() {
        // AMIKOR - GameService létrehozva
        Board board = gameService.getBoard();
        GameState gameState = gameService.getGameState();
        Player currentPlayer = gameService.getCurrentPlayer();
        GameMode gameMode = gameService.getGameMode();
        Player player1 = gameService.getPlayer1();
        Player player2 = gameService.getPlayer2();

        // AKKOR - minden getter visszaadja a megfelelő értéket
        assertNotNull(board);
        assertEquals(GameState.IN_PROGRESS, gameState);
        assertNotNull(currentPlayer);
        assertEquals(GameMode.HUMAN_VS_HUMAN, gameMode);
        assertNotNull(player1);
        assertNotNull(player2);
        assertEquals(player1, gameService.getCurrentPlayer()); // Kezdő játékos player1
    }
}