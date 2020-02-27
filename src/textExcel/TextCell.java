package textExcel;


public class TextCell implements Cell {
    private final String text;
    private final String rawText;
    
    public TextCell(String text) {
        this.text = text;
        this.rawText = text.substring(1, text.length() - 1);
    }
    
    /**
     * @return text for spreadsheet cell display, must be exactly length 10
     */
    @Override
    public String abbreviatedCellText() {
        return (rawText + "          ").substring(0, 10);
    }
    
    /**
     * @return text for individual cell inspection, not truncated or padded
     */
    @Override
    public String fullCellText() {
        return text;
    }
    
    @Override
    public int compareTo(Object o) {
        if (o instanceof TextCell) return this.text.compareTo(((TextCell) o).text);
        return 0;
    }
}
