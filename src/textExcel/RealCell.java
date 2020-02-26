package textExcel;

public abstract class RealCell implements Cell {
    protected final String rawInput;
    
    protected RealCell(String input) {
        this.rawInput = input;
    }
    
    public abstract double getDoubleValue();
}
