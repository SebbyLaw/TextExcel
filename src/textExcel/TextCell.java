// Sebastian Law
// 2020.3.27

package textExcel;


public class TextCell extends BaseCell {
    private final String noParentheses;
    
    public TextCell(String input) {
        super(input);
        this.noParentheses = input.substring(1, input.length() - 1);
    }
    
    /**
     * @return text for spreadsheet cell display, must be exactly length 10
     */
    @Override
    public String abbreviatedCellText() {
        return (noParentheses + "          ").substring(0, 10);
    }
    
    @Override
    public int compareTo(Object o) {
        if (o instanceof TextCell) return this.rawInput.compareTo(((TextCell) o).rawInput);
        return o instanceof RealCell ? -1 : 0;
    }
}
