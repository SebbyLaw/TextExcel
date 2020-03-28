// Sebastian Law
// 2020.3.27

package textExcel;

import java.util.ArrayList;
import java.util.Collections;

public class Spreadsheet implements Grid {
    private ArrayList<String> history = new ArrayList<>();
    private boolean recordingHistory = false;
    private int historyMaxSize = 1;
    
    private final Cell[][] cells;
    private final int numRows;
    private final int numCols;
    
    public Spreadsheet(int rows, int columns) {
        this.numRows = rows;
        this.numCols = columns;
        cells = new Cell[rows][columns];
        emptyAllCells();
    }
    
    public Spreadsheet() {
        this(20, 12);
    }
    
    /**
     * @return the number of rows in the spreadsheet
     */
    @Override
    public int getRows() {
        return this.numRows;
    }
    
    /**
     * @return the number of columns in the spreadsheet
     */
    @Override
    public int getCols() {
        return this.numCols;
    }
    
    /**
     * Helper method that sets all cells as empty cells if they are not already empty
     */
    private void emptyAllCells() {
        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getCols(); j++) {
                // check if it is already empty first, so we don't unnecessarily create new objects
                if (!(cells[i][j] instanceof EmptyCell)) cells[i][j] = new EmptyCell();
            }
        }
    }
    
    @Override
    public String processCommand(String command) {
        if (command.trim().isEmpty()) return "";  // don't even touch input if nothing
        String[] splitCommand = command.split(" ");
        
        // recording command history
        if (recordingHistory && !splitCommand[0].equalsIgnoreCase("history")) {
            history.add(0, command);
            if (history.size() > historyMaxSize) history.remove(history.size() - 1);
        }
        
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
                this.sort(splitCommand[1], sortType == 'A');
            } else return "ERROR: invalid sort type";
        } else if (splitCommand[0].equalsIgnoreCase("history")) {
            // history command
            if (splitCommand[1].equalsIgnoreCase("start")) {
                historyMaxSize = Integer.parseInt(splitCommand[2]);
                recordingHistory = true;
            } else if (splitCommand[1].equalsIgnoreCase("clear")) {
                for (int i = 0; i < Integer.parseInt(splitCommand[2]); i++) {
                    if (!history.isEmpty()) history.remove(history.size() - 1);
                }
            } else if (splitCommand[1].equalsIgnoreCase("stop")) {
                history.clear();
                recordingHistory = false;
            } else return String.join("\n", history);  // history display command
            return "";
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
                        if (isValidFormulaAssignment(assignString)) {
                            cellValue = new FormulaCell(assignString, this, loc);
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
            } else return "ERROR: invalid cell location or command";
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
        
        char column = Character.toUpperCase(string.charAt(0));
        // column should be a letter
        if (!Character.isLetter(column)) return false;
        // column should be within spreadsheet bounds
        if (column - 'A' > getCols() - 1) return false;
        
        // the rest of the chars should be numeric
        for (char c : string.substring(1).toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        
        int row = Integer.parseInt(string.substring(1));
        // row should be within spreadsheet bounds
        return row <= getRows() && row > 0;
    }
    
    /**
     * Helper method to check whether or not a string is valid to be assigned to a FormulaCell
     * @param formula the string to check
     * @return true if {@code formula} is a valid formula
     */
    public boolean isValidFormulaAssignment(String formula) {
        String[] equation = formula.substring(2, formula.length() - 2).split(" ");
        
        if (equation[0].equalsIgnoreCase("avg") || equation[0].equalsIgnoreCase("sum")) {
            // if the formula contains a function, just make sure the range is proper
            String[] range = equation[1].split("-", 2);
            return isValidLocation(range[0]) && isValidLocation(range[1]);
        }
        
        for (int i = 0; i < equation.length; i++) {
            String term = equation[i];
            
            if (i % 2 == 0) {
                // even terms must be numerical or cells
                if (!(isValidLocation(term) || isValidValue(term))) return false;
            } else {
                // odd terms must be operators
                char op = term.charAt(0);
                if (!(term.length() == 1 && (op == '*' || op == '/' || op == '+' || op == '-'))) return false;
            }
        }
        
        return true;
    }
    
    /**
     * Helper method to check whether or not a formula cell contains a valid formula
     * @param formulaCell the formula cell to check
     * @param locations the location the formula cell exists at
     * @return true if {@code formulaCell} contains a valid formula
     */
    public boolean isValidFormula(FormulaCell formulaCell, Location... locations) {
        String[] equation = formulaCell.getEquation();
    
        if (equation[0].equalsIgnoreCase("avg") || equation[0].equalsIgnoreCase("sum")) {
            // if this is a range formula. all cells in range must be valid
            for (Cell cell : getCellsInRange(equation[1])) {
                if (!(cell instanceof RealCell)) return false;
                if (cell instanceof FormulaCell) {
                    if (!isValidFormula((FormulaCell) cell, locations)) return false;
                }
            }
        } else {
            // if this is a regular formula
            for (int i = 0; i < equation.length; i++) {
                String term = equation[i];
                if (i % 2 == 0) {
                    // checking operands
                    double termValue;
                    
                    if (isValidLocation(term)) {
                        // this must be a cell reference
                        Cell cell = getCell(term);
                        if (cell instanceof RealCell) {
                            if (cell instanceof FormulaCell) {
                                Location[] newLocations = new Location[locations.length + 1];
                                int j = 0;
                                
                                FormulaCell reference = (FormulaCell) cell;
                                Location referenceLocation = reference.getLocation();
                                for (Location location : locations) {
                                    if (referenceLocation.getCol() == location.getCol() && referenceLocation.getRow() == location.getRow()) {
                                        // if it is in the same location as any other formula cell, it is self-referencing
                                        return false;
                                    }
                                    newLocations[j++] = location;
                                }
                                
                                newLocations[j] = referenceLocation;
                                // each formula cell must be valid as well
                                if (!isValidFormula(reference, newLocations)) return false;
                            }
                            
                            termValue = ((RealCell) cell).getDoubleValue();
                        } else return false;  // references must be instances of RealCell
                    } else termValue = Double.parseDouble(term);
                    // check division by zero
                    if (i - 1 > 0 && termValue == 0.0 && equation[i - 1].charAt(0) == '/') return false;
                } else {
                    // checking operators
                    if (term.length() > 1) return false;
                    char c = term.charAt(0);
                    if (c != '*' && c != '/' && c != '+' && c != '-') return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Helper method to parse the value of a cell reference or double
     * @param value the string value to parse
     * @return the double value of the reference
     */
    public double parseOperand(String value) {
        if (isValidLocation(value)) {
            Cell cell = getCell(value);
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
    
    /**
     * Sorts a range of cells on this spreadsheet
     * @param range the range of cells to sort
     * @param ascending whether or not to sort by ascending order
     */
    private void sort(String range, boolean ascending) {
        ArrayList<Cell> sortedRange = new ArrayList<Cell>(getCellsInRange(range)){{
            this.sort(ascending ? null : Collections.reverseOrder());
        }};
        
        int count = 0;
        for (Location loc : getLocationsInRange(range)) {
            Cell cell = sortedRange.get(count++);
            this.setCell(loc, cell);
            if (cell instanceof FormulaCell) ((FormulaCell) cell).setLocation(loc);
        }
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
     * Get the Cell at a location in the spreadsheet
     * @param loc the location to query
     * @return the Cell found at the {@code loc}
     */
    public Cell getCell(String loc) {
        return getCell(new SpreadsheetLocation(loc));
    }
    
    /**
     * @param loc the location in the spreadsheet to set
     * @param cell the cell to set at the {@code location}
     */
    private void setCell(Location loc, Cell cell) {
        cells[loc.getRow()][loc.getCol()] = cell;
    }
    
    /**
     * Helper method to get cells in a range
     * @param range the range to search
     * @return the cells withing {@code range}
     */
    public ArrayList<Cell> getCellsInRange(String range) {
        return new ArrayList<Cell>() {{
            for (Location loc : getLocationsInRange(range)) add(getCell(loc));
        }};
    }
    
    /**
     * Helper method to get the locations of a range
     * @param range the range to search
     * @return the cells withing {@code range}
     */
    public static ArrayList<Location> getLocationsInRange(String range) {
        String[] rSplit = range.split("-", 2);
        Location start = new SpreadsheetLocation(rSplit[0]);
        Location end = new SpreadsheetLocation(rSplit[1]);
        return new ArrayList<Location>() {{
            for (int row = start.getRow(); row <= end.getRow(); row++) {
                for (int col = start.getCol(); col <= end.getCol(); col++) {
                    add(new SpreadsheetLocation(col, row));
                }
            }
        }};
    }
    
    /**
     * @return the current state of the spreadsheet in grid text form for display
     */
    @Override
    public String getGridText() {
        StringBuilder gridText = new StringBuilder("   |");
        // draw the columns header
        for (char col = 'A'; col < (char) getCols() + 'A'; col++) gridText.append(col).append("         |");
        gridText.append("\n");
        
        for (int row = 1; row <= getRows(); row++) {
            String num = (row + "  ").substring(0, 3);
            gridText.append(num).append("|");
            for (char col = 'A'; col < (char) getCols() + 'A'; col++) {
                gridText.append(getCell(new SpreadsheetLocation(col, row)).abbreviatedCellText()).append("|");
            }
            gridText.append("\n");
        }
        return gridText.toString();
    }
}
