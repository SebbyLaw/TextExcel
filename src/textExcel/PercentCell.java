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
    
    @Override
    public String abbreviatedCellText() {
        return (rawInput.split("\\.")[0] + "%         ").substring(0, 10);
    }
    
    @Override
    public String fullCellText() {
        return String.valueOf(getDoubleValue());
    }
}
