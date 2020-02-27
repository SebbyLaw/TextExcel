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
    
    public SpreadsheetLocation(int column, int row) {
        this.letter = Spreadsheet.colAsChar(column);
        this.row = row;
    }
    
    public SpreadsheetLocation(char column, int row) {
        this.letter = Character.toUpperCase(column);
        this.row = row - 1;
    }
    
    public SpreadsheetLocation(String cellName) {
        this(cellName.charAt(0), Integer.parseInt(cellName.substring(1)));
    }
}
