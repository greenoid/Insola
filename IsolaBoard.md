Erläuterungen zur Implementierung:

    IsolaBoard Klasse:

        BOARD_ROWS, BOARD_COLS: Konstanten für die Dimensionen des Spielbretts (6 Zeilen, 8 Spalten).

        board[][]: Ein zweidimensionales int-Array, das den Zustand jedes Feldes repräsentiert.

        Konstanten (EMPTY, TILE, PLAYER1_START, PLAYER2_START, PLAYER1, PLAYER2): Diese int-Konstanten werden verwendet, um den Inhalt eines Feldes im board-Array zu definieren.

            EMPTY: Das Feld ist leer (kein Spielstein, keine Figur).

            TILE: Das Feld enthält einen Spielstein.

            PLAYER1_START, PLAYER2_START: Diese Felder sind die permanenten Startfelder, die immer betreten werden können.

            PLAYER1, PLAYER2: Wichtiger Hinweis: Die tatsächlichen Positionen der Spieler werden in player1Row, player1Col, player2Row, player2Col gespeichert, nicht direkt im board-Array. Die PLAYER1 und PLAYER2 Konstanten sind eher für die Identifikation des Spielers in Methoden gedacht. In printBoard() werden die Spielerpositionen gesondert behandelt.

        player1Row, player1Col, player2Row, player2Col: Speichern die aktuellen Koordinaten der beiden Spielfiguren.

    initializeBoard() Methode:

        Füllt initial alle Felder mit TILEs.

        Setzt dann die Startfelder auf PLAYER1_START und PLAYER2_START. Die genaue Position der Startfelder (hier (5,3) und (0,4)) muss an die tatsächliche Ravensburger-Version angepasst werden.

        Platziert die Spielfiguren auf ihren Startfeldern.

    getCellState(int row, int col):

        Gibt den reinen Zustand des Feldes (ob es ein Stein, leer oder ein Startfeld ist) zurück. Wichtig: Diese Methode sagt nicht aus, ob sich eine Figur auf dem Feld befindet.

    getPlayer1Position(), getPlayer2Position():

        Gibt die aktuellen Koordinaten der Spielfiguren zurück.

    movePlayer(int player, int newRow, int newCol) Methode:

        Überprüft die Gültigkeit eines Zuges:

            Ist das Zielfeld innerhalb des Bretts?

            Ist das Zielfeld bereits von der anderen Spielfigur besetzt? (Die eigene Figur kann nicht auf dem gleichen Feld landen).

            Ist das Zielfeld ein gültiges Feld zum Betreten (enthält einen Stein oder ist ein Startfeld)? Ein leeres Feld, das kein Startfeld ist, kann nicht betreten werden.

            Ist der Zug nur ein Feld weit (horizontal, vertikal oder diagonal)?

        Wenn der Zug gültig ist, werden die Koordinaten des Spielers aktualisiert.

    removeTile(int row, int col) Methode:

        Überprüft die Gültigkeit des Entfernens:

            Ist das Feld innerhalb des Bretts?

            Befindet sich eine Spielfigur auf diesem Feld? (Man kann keinen Stein entfernen, auf dem eine Figur steht.)

            Enthält das Feld tatsächlich einen Spielstein (TILE)?

        Wenn gültig, wird der Zustand des Feldes auf EMPTY gesetzt.

    isPlayerIsolated(int player) Methode:

        Dies ist die Kernmethode zur Bestimmung des Spielendes.

        Sie prüft alle 8 benachbarten Felder (einschließlich diagonal) um die aktuelle Position des Spielers.

        Für jedes Nachbarfeld wird geprüft:

            Liegt es innerhalb des Bretts?

            Ist es ein Feld, das betreten werden darf (enthält einen Stein oder ist ein Startfeld)?

            Ist es nicht von der gegnerischen Figur besetzt?

        Wenn auch nur ein einziger gültiger Zug gefunden wird, ist der Spieler nicht isoliert und die Methode gibt false zurück.

        Wenn alle benachbarten Felder ungültig sind (entweder außerhalb des Bretts, leer, oder von der gegnerischen Figur blockiert), dann ist der Spieler isoliert und die Methode gibt true zurück.

    printBoard() Methode:

        Gibt eine textbasierte Darstellung des Spielbretts aus.

        Sie unterscheidet zwischen leeren Feldern, Feldern mit Steinen, Startfeldern und den aktuellen Positionen der Spieler.

    main Methode (Beispielnutzung):

        Demonstriert, wie die IsolaBoard-Klasse instanziiert und die verschiedenen Methoden aufgerufen werden können, um das Spiel zu simulieren.