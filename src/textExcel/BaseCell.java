// Sebastian Law
// 2020.3.27

package textExcel;

/**
 * An abstract base class for textExcel cells
 *
 * Most cells offer the same basic functionality of storing the raw input
 * and storing it in a private field, then returning it in fullCellText.
 */
public abstract class BaseCell implements Cell{
    protected final String rawInput;
    
    public BaseCell(String input) {
        this.rawInput = input;
    }
    
    /**
     * @return text for spreadsheet cell display, must be exactly length 10
     */
    @Override
    public String fullCellText() {
        return rawInput;
    }
    
    /**
     * @return text for individual cell inspection, not truncated or padded
     */
    @Override
    public String abbreviatedCellText() {
        return (rawInput + "          ").substring(0, 10);
    }
}
