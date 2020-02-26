// Sebastian Law
// 2020.2.24

package textExcel;

// Update this file with your own code.

public class Spreadsheet implements Grid {
    Cell[][] cells = new Cell[20][12];
    
    public Spreadsheet() {
        emptyAllCells();
    }
    
    /**
     * Helper method that sets all cells as empty cells if they are not already empty
     */
    private void emptyAllCells() {
        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getCols(); j++) {
                // instead of creating new objects willy-nilly
                // we only do it if they are not empty, thus saving processing speed
                if (!(cells[i][j] instanceof EmptyCell)) cells[i][j] = new EmptyCell();
            }
        }
    }
    
    @Override
    public String processCommand(String command) {
        if (command.trim().isEmpty()) return "";  // don't even touch input if nothing
        
        String[] splitCommand = command.split(" ");
        
        // commands if-else chain monster
        if (splitCommand[0].equalsIgnoreCase("clear")) {
            // clear command
            if (splitCommand.length == 1) {
                emptyAllCells();
            } else {
                if (isValidLocation(splitCommand[1])) {
                    Location loc = new SpreadsheetLocation(splitCommand[1]);
                    // object creation overhead is more expensive than checking if the cell is already empty
                    if (!(getCell(loc) instanceof EmptyCell)) setCell(loc, new EmptyCell());
                    // this only creates a new object if the cell is not already empty
                } else return "ERROR: invalid cell location to clear";
            }
        } else {
            // if it is not a command, it must be value fetching or assignment
            if (isValidLocation(splitCommand[0])) {
                Location loc = new SpreadsheetLocation(splitCommand[0]);
                if (splitCommand.length == 1) {
                    return getCell(loc).fullCellText();
                } else {
                    String assignValue = command.split(" = ", 2)[1];
                    setCell(loc, new TextCell(assignValue));
                }
            } else return "ERROR: invalid cell location";
        }
        
        return getGridText();
    }
    
    /**
     * Helper method to check whether or not a string is a valid spreadsheet location
     * Will return false even if in correct format if the spreadsheet is not large enough to support the location
     * @param string the string to check
     * @return true if {@code string} is a valid location on the spreadsheet
     */
    private boolean isValidLocation(String string) {
        if (string.length() < 2) return false;
        
        char column = string.charAt(0);
        // column should be within spreadsheet bounds
        if (colAsInt(column) > getCols()) return false;
        
        // the rest of the chars should be numeric
        for (int i = 1; i < string.length(); i++) {
            char n = string.charAt(i);
            if (!Character.isDigit(n)) return false;
        }
        
        // row should be within spreadsheet bounds
        return Integer.parseInt(string.substring(1)) <= getRows();
        // true if the last check fails, otherwise false
    }
    
    @Override
    public int getRows() {
        return 20;
    }
    
    @Override
    public int getCols() {
        return 12;
    }
    
    /**
     * Helper function to convert integer to column char
     * @return the letter of the column in the spreadsheet
     */
    private static char colAsChar(int n) {
        return (char) ('A' + n);
    }
    
    /**
     * Helper function to convert column char to integer
     * @return the uppercase character of the column in the spreadsheet
     */
    private static int colAsInt(char c) {
        return Character.toUpperCase(c) - 'A';
    }
    
    @Override
    public Cell getCell(Location loc) {
        return cells[loc.getRow()][loc.getCol()];
    }
    
    /**
     * @param loc the location in the spreadsheet to set
     * @param cell the cell to set at the {@code location}
     */
    private void setCell(Location loc, Cell cell) {
        cells[loc.getRow()][loc.getCol()] = cell;
    }
    
    @Override
    public String getGridText() {
        String grid = "   |";
        for (int i = 'A'; i < colAsChar(getCols()); i++) {
            grid += (char) i + "         |";
        }
        
        grid += "\n";
        
        for (int i = 1; i <= getRows(); i++) {
            String num = String.valueOf(i);
            num = (num + "  ").substring(0, 3);
            grid += num + "|";
            
            for (int j = 'A'; j < colAsChar(getCols()); j++) {
                Location loc = new SpreadsheetLocation((char) j + String.valueOf(i));
                grid += getCell(loc).abbreviatedCellText() + "|";
            }
            
            grid += "\n";
        }
        
        return grid;
    }
}
