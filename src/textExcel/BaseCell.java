package textExcel;

public abstract class BaseCell implements Cell{
    protected final String rawInput;
    
    public BaseCell(String input) {
        this.rawInput = input;
    }
    
    @Override
    public String fullCellText() {
        return rawInput;
    }
    
    @Override
    public String abbreviatedCellText() {
        return (rawInput + "          ").substring(0, 10);
    }
}
