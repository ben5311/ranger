package ranger.core;

/**
 * Circular range value for double type.
 */
public class CircularRangeValueDouble extends CircularRangeValue<Double> {

    /**
     * Constructs double circular range value with specified <code>range</code> and <code>increment</code>.
     *
     * @param range Range.
     * @param increment Increment.
     */
    public CircularRangeValueDouble(Range<Double> range, Double increment) {
        super(range, increment);
    }

    /*
    Copy constructor
     */
    private CircularRangeValueDouble(CircularRangeValueDouble source) {
        super(source);
    }

    @Override
    protected CircularRangeValueDouble clone() {
        return new CircularRangeValueDouble(this);
    }

    @Override
    protected Double zero() {
        return 0d;
    }

    @Override
    protected boolean isIncrementGreaterThanRangeSize() {
        return Math.abs(range.getBeginning() - range.getEnd()) < Math.abs(increment);
    }

    @Override
    protected Double peekNextValue() {
        return val + increment;
    }
}
