// Sebastian Law
// 2020.2.24

package textExcel;

public class SpreadsheetLocation implements Location {
    private final char letter;
    private final int row;
    
    @Override
    public int getRow() {
        return row;
    }

    @Override
    public int getCol() {
        return letter - 'A';
    }
    
    public SpreadsheetLocation(String cellName) {
        this.letter = cellName.charAt(0);
        this.row = Integer.parseInt(cellName.substring(1)) - 1;
    }
}
