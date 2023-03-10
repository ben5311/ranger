package ranger.core;

/**
 * Circular range value for float type.
 */
public class CircularRangeValueFloat extends CircularRangeValue<Float> {

    /**
     * Constructs float circular range value with specified <code>range</code> and <code>increment</code>.
     *
     * @param range Range.
     * @param increment Increment.
     */
    public CircularRangeValueFloat(Range<Float> range, Float increment) {
        super(range, increment);
    }

    /*
    Copy constructor
     */
    private CircularRangeValueFloat(CircularRangeValueFloat source) {
        super(source);
    }

    @Override
    protected CircularRangeValueFloat clone() {
        return new CircularRangeValueFloat(this);
    }

    @Override
    protected Float zero() {
        return 0f;
    }

    @Override
    protected boolean isIncrementGreaterThanRangeSize() {
        return Math.abs(range.getBeginning() - range.getEnd()) < Math.abs(increment);
    }

    @Override
    protected Float peekNextValue() {
        return val + increment;
    }
}
