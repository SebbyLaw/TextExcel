// Sebastian Law
// 2020.2.24

package textExcel;

// Update this file with your own code.

import java.util.ArrayList;
import java.util.Collections;

public class Spreadsheet implements Grid {
    private final Cell[][] cells = new Cell[20][12];
    
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
        } else if (splitCommand[0].toLowerCase().startsWith("sort") && splitCommand[0].length() == 5) {
            // sort command
            char sortType = Character.toUpperCase(splitCommand[0].charAt(4));
            if (sortType == 'A' || sortType == 'D') {
                String[] range = splitCommand[1].split("-", 2);
                Location start = new SpreadsheetLocation(range[0]);
                Location end = new SpreadsheetLocation(range[1]);
                sort(start, end, sortType == 'A');
            } else return "ERROR: invalid sort type";
        } else {
            // if it is not a command, it must be value fetching or assignment
            if (isValidLocation(splitCommand[0])) {
                Location loc = new SpreadsheetLocation(splitCommand[0]);
                if (splitCommand.length == 1) {
                    // value query and return
                    return getCell(loc).fullCellText();
                } else {
                    // value assignment
                    String assignString = command.split(" = ", 2)[1];
                    Cell cellValue;
                    
                    if (assignString.startsWith("\"") && assignString.endsWith("\"")) {
                        // assign a text cell
                        cellValue = new TextCell(assignString);
                    } else if (assignString.startsWith("( ") && assignString.endsWith(" )")) {
                        // assign a formula cell if valid formula
                        if (isValidFormula(assignString, loc)) {
                            cellValue = new FormulaCell(assignString, this);
                        } else return "ERROR: invalid formula";
                    } else if (assignString.endsWith("%")) {
                        // assign a percent cell
                        cellValue = new PercentCell(assignString);
                    } else if (isValidValue(assignString)) {
                        // assign a value cell
                        cellValue = new ValueCell(assignString);
                    } else {
                        return "ERROR: invalid cell value";
                    }
                    
                    setCell(loc, cellValue);
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
    public boolean isValidLocation(String string) {
        if (string.length() < 2) return false;
        
        char column = string.charAt(0);
        // column should be a letter
        if (!Character.isLetter(column)) return false;
        // column should be within spreadsheet bounds
        if (colAsInt(column) > getCols() - 1) return false;
        
        // the rest of the chars should be numeric
        for (int i = 1; i < string.length(); i++) {
            char n = string.charAt(i);
            if (!Character.isDigit(n)) return false;
        }
        
        int row = Integer.parseInt(string.substring(1));
        // row should be within spreadsheet bounds
        return row <= getRows() && row > 0;
        // true if the last check fails, otherwise false
    }
    
    /**
     * Helper method to check whether or not a string is valid to be assigned to a FormulaCell
     * @param formula the string to check
     * @param assignLocation the location that the formula will be assigned to
     * @return true if {@code string} is a valid formula
     */
    private boolean isValidFormula(String formula, Location assignLocation) {
        // TODO: implement
        return true;
    }
    
    /**
     * Helper method to parse the value of a cell reference or double
     * @param value the string value to parse
     * @return the double value of the reference
     */
    public double parseOperand(String value) {
        if (isValidLocation(value)) {
            Cell cell = getCell(new SpreadsheetLocation(value));
            // assume all casting issues will be dealt with elsewhere
            return ((RealCell) cell).getDoubleValue();
        } else {
            return Double.parseDouble(value);
        }
    }
    
    /**
     * Helper method to check whether or not a string is valid to be assigned to a ValueCell
     * @param string the string to check
     * @return true if {@code string} is a valid value
     */
    private static boolean isValidValue(String string) {
        for (int i = string.charAt(0) == '-' ? 1 : 0, decimalCounter = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (!Character.isDigit(c)) {
                if (c == '.') {
                    // there should only be one decimal point
                    if (decimalCounter++ > 1) return false;
                } else return false;
            }
        }
        // true if checks pass
        return true;
    }
    
    private void sort(Location start, Location end, boolean ascending) {
        ArrayList<Cell> sortedRange = new ArrayList<Cell>(getCellsInRange(start, end)){{
            sort(ascending ? null : Collections.reverseOrder());
        }};
        
        for (int i = start.getRow(), k = 0; i <= end.getRow(); i++) {
            for (int j = start.getCol(); j <= end.getCol(); j++) {
                setCell(new SpreadsheetLocation(j, i), sortedRange.get(k++));
            }
        }
    }
    
    /**
     * @return the number of rows in the spreadsheet
     */
    @Override
    public int getRows() {
        return 20;
    }
    
    /**
     * @return the number of columns in the spreadsheet
     */
    @Override
    public int getCols() {
        return 12;
    }
    
    /**
     * Helper function to convert integer to column char
     * @return the letter of the column in the spreadsheet
     */
    public static char colAsChar(int n) {
        return (char) ('A' + n);
    }
    
    /**
     * Helper function to convert column char to integer
     * @return the uppercase character of the column in the spreadsheet
     */
    public static int colAsInt(char c) {
        return Character.toUpperCase(c) - 'A';
    }
    
    /**
     * Get the Cell at a location in the spreadsheet
     * @param loc the location to query
     * @return the Cell found at the {@code loc}
     */
    @Override
    public Cell getCell(Location loc) {
        return cells[loc.getRow()][loc.getCol()];
    }
    
    /**
     * Helper method to get cells in a range
     * @param start the start of the range, inclusive
     * @param end the end of the range, inclusive
     * @return the cells withing {@code range}
     */
    public ArrayList<Cell> getCellsInRange(Location start, Location end) {
        return new ArrayList<Cell>() {{
            for (Location loc : getRange(start, end)) add(getCell(loc));
        }};
    }
    
    /**
     * Helper method to get cells in a range
     * @param range the range to search
     * @return the cells withing {@code range}
     */
    public ArrayList<Cell> getCellsInRange(String range) {
        String[] rSplit = range.split("-", 2);
        Location start = new SpreadsheetLocation(rSplit[0]);
        Location end = new SpreadsheetLocation(rSplit[1]);
        return getCellsInRange(start, end);
    }
    
    /**
     * Helper method to get the locations of a range
     * @param range the range to search
     * @return the cells withing {@code range}
     */
    public ArrayList<Location> getRange(String range) {
        String[] rSplit = range.split("-", 2);
        Location start = new SpreadsheetLocation(rSplit[0]);
        Location end = new SpreadsheetLocation(rSplit[1]);
        return getRange(start, end);
    }
    
    /**
     * Helper method to get the locations of a range
     * @param start the start of the range, inclusive
     * @param end the end of the range, inclusive
     * @return the cells withing {@code range}
     */
    public ArrayList<Location> getRange(Location start, Location end) {
        return new ArrayList<Location>() {{
            for (int i = start.getCol(); i <= end.getCol(); i++) {
                for (int j = start.getRow(); j <= end.getRow(); j++) {
                    add(new SpreadsheetLocation(i, j));
                }
            }
        }};
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
        StringBuilder grid = new StringBuilder("   |");
        for (char i = 'A'; i < colAsChar(getCols()); i++) {
            grid.append(i).append("         |");
        }
        
        grid.append("\n");
        
        for (int i = 1; i <= getRows(); i++) {
            String num = String.valueOf(i);
            num = (num + "  ").substring(0, 3);
            grid.append(num).append("|");
            
            for (char j = 'A'; j < colAsChar(getCols()); j++) {
                Location loc = new SpreadsheetLocation(j, i);
                grid.append(getCell(loc).abbreviatedCellText()).append("|");
            }
            
            grid.append("\n");
        }
        
        return grid.toString();
    }
}
