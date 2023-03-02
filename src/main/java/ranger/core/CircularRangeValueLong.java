package ranger.core;

/**
 * Circular range value for long type.
 */
public class CircularRangeValueLong extends CircularRangeValue<Long> {

    /**
     * Constructs long circular range value with specified <code>range</code> and <code>increment</code>.
     *
     * @param range Range.
     * @param increment Increment.
     */
    public CircularRangeValueLong(Range<Long> range, Long increment) {
        super(range, increment);
    }

    /*
    Copy constructor
     */
    private CircularRangeValueLong(CircularRangeValueLong source) {
        super(source);
    }

    @Override
    protected CircularRangeValueLong clone() {
        return new CircularRangeValueLong(this);
    }

    @Override
    protected Long zero() {
        return 0L;
    }

    @Override
    protected boolean isIncrementGreaterThanRangeSize() {
        return Math.abs(range.getBeginning() - range.getEnd()) < Math.abs(increment);
    }

    @Override
    protected Long peekNextValue() {
        return val + increment;
    }
}
