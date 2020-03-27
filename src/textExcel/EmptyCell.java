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
