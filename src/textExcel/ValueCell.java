package textExcel;

public class ValueCell extends RealCell {
    private final double fullPrecision;
    
    public ValueCell(String input) {
        super(input);
        this.fullPrecision = Double.parseDouble(input);
    }
    
    @Override
    public double getDoubleValue() {
        return fullPrecision;
    }
    
    @Override
    public String abbreviatedCellText() {
        String abbr;
        if (fullPrecision == (int) fullPrecision) {
            abbr = (int) fullPrecision + ".0        ";
        } else {
            abbr = fullPrecision + "          ";
        }
        return abbr.substring(0, 10);
    }
    
    @Override
    public String fullCellText() {
        return rawInput;
    }
}
