// Sebastian Law
// 2020.3.27

package textExcel;

import java.util.ArrayList;
import java.util.Arrays;

public class FormulaCell extends RealCell {
    private final String[] equation;
    private final Spreadsheet spreadsheet;
    private Location location;
    
    public FormulaCell(String input, Spreadsheet spreadsheet, Location location) {
        super(input);
        equation = input.substring(2, input.length() - 2).split(" ");
        
        this.spreadsheet = spreadsheet;
        this.location = location;
    }
    
    /**
     * @return the equation stored in this formula cell
     */
    public String[] getEquation() {
        return this.equation;
    }
    
    /**
     * @return this cell's location on the spreadsheet
     */
    public Location getLocation() {
        return this.location;
    }
    
    /**
     * Set the cell's location in the event the cell is moved from it's original location
     * @param location the new location
     */
    public void setLocation(Location location) {
        this.location = location;
    }
    
    /**
     * @return text for spreadsheet cell display, must be exactly length 10
     */
    @Override
    public String abbreviatedCellText() {
        if (spreadsheet.isValidFormula(this, location))return (getDoubleValue() + "          ").substring(0, 10);
        return "#ERROR    ";
    }
    
    @Override
    public double getDoubleValue() {
        if (equation[0].equalsIgnoreCase("AVG") || equation[0].equalsIgnoreCase("SUM")) {
            ArrayList<Cell> cellRange = spreadsheet.getCellsInRange(equation[1]);
            double total = 0;
            int numCells = cellRange.size();
            
            for (Cell cell : cellRange) {
                // assume all casting issues will be dealt with elsewhere
                total += ((RealCell) cell).getDoubleValue();
            }
            
            return equation[0].equalsIgnoreCase("AVG") ? total / numCells : total;
            
        } else {
            // if not a command, it is an arithmetic expression
            ArrayList<String> expression = new ArrayList<>(Arrays.asList(equation));
            String[][] operators = {{"*", "/"}, {"+", "-"}};
            
            for (String[] operatorPriority : operators) {
                
                int opIndex = 1;
                while (expression.contains(operatorPriority[0]) || expression.contains(operatorPriority[1])) {
                    
                    String operator = expression.get(opIndex);
                    if (operator.equals(operatorPriority[0]) || operator.equals(operatorPriority[1])) {
                        
                        double firstValue = spreadsheet.parseOperand(expression.get(opIndex - 1));
                        double secondValue = spreadsheet.parseOperand(expression.get(opIndex + 1));
                        
                        double answer;
    
                        if ("*".equals(operator)) {
                            answer = firstValue * secondValue;
                        } else if ("/".equals(operator)) {
                            answer = firstValue / secondValue;
                        } else if ("+".equals(operator)) {
                            answer = firstValue + secondValue;
                        } else {
                            answer = firstValue - secondValue;
                        }
                        
                        expression.set(opIndex - 1, String.valueOf(answer));
                        // shift everything back two indices
                        expression.remove(opIndex);
                        expression.remove(opIndex);
                
                    } else opIndex += 2;  // check the next operator index
                }
            }
            return spreadsheet.parseOperand(expression.get(0));
        }
    }
}
