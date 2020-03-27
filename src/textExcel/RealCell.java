// Sebastian Law
// 2020.3.27

package textExcel;

public abstract class RealCell extends BaseCell {
    protected RealCell(String input) {
        super(input);
    }
    
    public double getDoubleValue() {
        return 0;
    }
    
    @Override
    public int compareTo(Object o) {
        if (o instanceof RealCell) {
            double thisValue = this.getDoubleValue();
            double otherValue = ((RealCell) o).getDoubleValue();
    
            if (thisValue == otherValue) return 0;
            return thisValue > otherValue ? 1 : -1;
        } else return o instanceof TextCell ? 1 : 0;
    }
}
