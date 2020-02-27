package textExcel;

import java.util.ArrayList;
import java.util.Arrays;

public class FormulaCell extends RealCell {
    private final String[] equation;
    private final Spreadsheet spreadsheet;
    
    public FormulaCell(String input, Spreadsheet spreadsheet) {
        super(input);
        equation = input.substring(2, input.length() - 2).split(" ");
        
        this.spreadsheet = spreadsheet;
    }
    
    @Override
    public double getDoubleValue() {
        if (equation[0].equalsIgnoreCase("AVG") || equation[0].equalsIgnoreCase("SUM")) {
            String[] cellRange = equation[1].split("-", 2);
            double total = 0;
            int numCells = 0;
            
            for (char c = Character.toUpperCase(cellRange[0].charAt(0)); c <= Character.toUpperCase(cellRange[1].charAt(0)); c++) {
                for (int i = Integer.parseInt(cellRange[0].substring(1)); i <= Integer.parseInt(cellRange[1].substring(1)); i++) {
                    total += ((RealCell) spreadsheet.getCell(new SpreadsheetLocation(c, i))).getDoubleValue();
                    numCells++;
                }
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
                
                        double firstValue = parseOperand(expression.get(opIndex - 1));
                        double secondValue = parseOperand(expression.get(opIndex + 1));
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
            return parseOperand(expression.get(0));
        }
    }
    
    /**
     * A helper method to parse the value of a cell reference or double
     * @param value the string value to parse
     * @return the double value of the reference
     */
    private double parseOperand(String value) {
        if (spreadsheet.isValidLocation(value)) {
            return ((RealCell) spreadsheet.getCell(new SpreadsheetLocation(value))).getDoubleValue();
        } else {
            return Double.parseDouble(value);
        }
    }
    
    /**
     * @return text for spreadsheet cell display, must be exactly length 10
     */
    @Override
    public String abbreviatedCellText() {
        return (getDoubleValue() + "          ").substring(0, 10);
    }
}
