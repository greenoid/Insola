package de.greenoid.game.isola;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IsolaMove {
    private static final Logger log = LogManager.getLogger(IsolaMove.class);

    // Aktuelle Position der Figur
    public final int moveFromRow;
    public final int moveFromCol;

    // Zielposition der Figur
    public final int moveToRow;
    public final int moveToCol;

    // Position des zu entfernenden Steins
    public final int removeTileRow;
    public final int removeTileCol;

    public IsolaMove(int moveFromRow, int moveFromCol, int moveToRow, int moveToCol, int removeTileRow, int removeTileCol) {
        this.moveFromRow = moveFromRow;
        this.moveFromCol = moveFromCol;
        this.moveToRow = moveToRow;
        this.moveToCol = moveToCol;
        this.removeTileRow = removeTileRow;
        this.removeTileCol = removeTileCol;
    }

    @Override
    public String toString() {
        return "Move: (" + moveFromRow + "," + moveFromCol + ") -> (" + moveToRow + "," + moveToCol + "), Remove: (" + removeTileRow + "," + removeTileCol + ")";
    }
}