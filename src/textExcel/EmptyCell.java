package textExcel;

public class EmptyCell implements Cell {
    /**
     * @return text for spreadsheet cell display, must be exactly length 10
     */
    @Override
    public String abbreviatedCellText() {
        return "";
    }
    
    /**
     * @return text for individual cell inspection, not truncated or padded
     */
    @Override
    public String fullCellText() {
        return "";
    }
}
