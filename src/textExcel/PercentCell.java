package textExcel;

public class PercentCell extends RealCell {
    private final double fullPrecision;
    
    public PercentCell(String input) {
        super(input);
        this.fullPrecision = Double.parseDouble(input.substring(0, input.length() - 1)) / 100.0;
    }
    
    @Override
    public double getDoubleValue() {
        return fullPrecision;
    }
    
    /**
     * @return text for spreadsheet cell display, must be exactly length 10
     */
    @Override
    public String abbreviatedCellText() {
        return (rawInput.split("\\.")[0] + "%         ").substring(0, 10);
    }
    
    /**
     * @return text for individual cell inspection, not truncated or padded
     */
    @Override
    public String fullCellText() {
        return String.valueOf(fullPrecision);
    }
}
