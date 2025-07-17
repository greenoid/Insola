package de.greenoid.game.isola;

public class IsolaMove {
    public int moveFromRow, moveFromCol;
    public int moveToRow, moveToCol;
    public int removeTileRow, removeTileCol;

    public IsolaMove(int moveFromRow, int moveFromCol, int moveToRow, int moveToCol, int removeTileRow, int removeTileCol) {
        this.moveFromRow = moveFromRow;
        this.moveFromCol = moveFromCol;
        this.moveToRow = moveToRow;
        this.removeTileRow = removeTileRow;
        this.removeTileCol = removeTileCol;
    }

    @Override
    public String toString() {
        return "Move: (" + moveFromRow + "," + moveFromCol + ") -> (" + moveToRow + "," + moveToCol +
                "), Remove: (" + removeTileRow + "," + removeTileCol + ")";
    }
}