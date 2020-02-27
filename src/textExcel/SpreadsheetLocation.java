// Sebastian Law
// 2020.2.24

package textExcel;

public class SpreadsheetLocation implements Location {
    private final int column;
    private final int row;
    
    @Override
    public int getRow() {
        return row;
    }

    @Override
    public int getCol() {
        return column;
    }
    
    /**
     * Constructor to directly assign column and row; ex: L20 = 11, 19
     * @param column the column number
     * @param row the row number
     */
    public SpreadsheetLocation(int column, int row) {
        this.column = column;
        this.row = row;
    }
    
    /**
     * Constructor in the form of (char)(int) ex: L20 = 'L', 20
     * @param column the column in char form
     * @param row the column in unmodified integer form
     */
    public SpreadsheetLocation(char column, int row) {
        this(Character.toUpperCase(column) - 'A', row - 1);
    }
    
    /**
     * Constructor in the form of (string) ex: L20 = "L20"
     * @param cellName the cell name
     */
    public SpreadsheetLocation(String cellName) {
        this(cellName.charAt(0), Integer.parseInt(cellName.substring(1)));
    }
}
