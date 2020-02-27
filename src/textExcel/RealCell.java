package textExcel;

public abstract class RealCell implements Cell, Comparable<RealCell> {
    protected final String rawInput;
    
    protected RealCell(String input) {
        this.rawInput = input;
    }
    
    public double getDoubleValue() {
        return 0;
    }
    
    /**
     * @return text for individual cell inspection, not truncated or padded
     */
    @Override
    public String fullCellText() {
        return rawInput;
    }
    
    @Override
    public int compareTo(RealCell o) {
        double thisValue = this.getDoubleValue();
        double otherValue = o.getDoubleValue();
        
        if (thisValue == otherValue) return 0;
        return thisValue > otherValue ? 1 : -1;
    }
}
