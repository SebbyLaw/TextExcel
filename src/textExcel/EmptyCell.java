// Sebastian Law
// 2020.3.27

package textExcel;

public class EmptyCell extends BaseCell {
    public EmptyCell() {
        super("");
    }
    
    @Override
    public int compareTo(Object o) {
        return -1;
    }
}
