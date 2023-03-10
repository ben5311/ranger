package ranger.core;

/**
 * Circular range value for int type.
 */
public class CircularRangeValueInt extends CircularRangeValue<Integer> {

    /**
     * Constructs int circular range value with specified <code>range</code> and <code>increment</code>.
     *
     * @param range Range.
     * @param increment Increment.
     */
    public CircularRangeValueInt(Range<Integer> range, Integer increment) {
        super(range, increment);
    }

    /*
    Copy constructor
     */
    private CircularRangeValueInt(CircularRangeValueInt source) {
        super(source);
    }

    @Override
    protected CircularRangeValueInt clone() {
        return new CircularRangeValueInt(this);
    }

    @Override
    protected Integer zero() {
        return 0;
    }

    @Override
    protected boolean isIncrementGreaterThanRangeSize() {
        return Math.abs(range.getBeginning() - range.getEnd()) < Math.abs(increment);
    }

    @Override
    protected Integer peekNextValue() {
        return val + increment;
    }
}
