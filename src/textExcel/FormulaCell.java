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
    
    /**
     * @return text for spreadsheet cell display, must be exactly length 10
     */
    @Override
    public String abbreviatedCellText() {
        return (getDoubleValue() + "          ").substring(0, 10);
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
