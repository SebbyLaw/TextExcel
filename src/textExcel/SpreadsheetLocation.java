// Sebastian Law
// 2020.2.24

package textExcel;

//Update this file with your own code.

public class SpreadsheetLocation implements Location {
    @Override
    public int getRow() {
        return 0;
    }

    @Override
    public int getCol() {
        return 0;
    }
    
    public SpreadsheetLocation(String cellName) {
    }
}
