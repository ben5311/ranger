package ranger.core;

/**
 * Circular range value for int type.
 */
public class CircularRangeValueChar extends CircularRangeValue<Character> {

    private final int intIncrement;

    /**
     * Constructs int circular range value with specified <code>range</code> and <code>increment</code>.
     *
     * @param range Range.
     * @param increment Increment.
     * @throws ValueException if increment is zero or increment does not fit to range type (e. g. increment of -1 with increasing range)
     */
    public CircularRangeValueChar(Range<Character> range, int increment) {
        super(range, range.isIncreasing() ? '2' : '0');     //just need the char increment to comply with CircularRangeValue
        if (range.isIncreasing() && increment <= 0) { throw new ValueException("If range is increasing, increment must be positive."); }
        if (range.isDecreasing() && increment >= 0) { throw new ValueException("If range is decreasing, increment must be negative."); }
        this.intIncrement = increment;
        if (isIncrementGreaterThanRangeSize()) {
            throw new ValueException("Range size must be greater than increment.");
        }
    }

    /*
    Copy constructor
     */
    private CircularRangeValueChar(CircularRangeValueChar source) {
        super(source);
        this.intIncrement = source.intIncrement;
    }

    @Override
    protected CircularRangeValueChar clone() {
        return new CircularRangeValueChar(this);
    }

    @Override
    protected Character zero() {
        return '1';
    }   //bypass char increment check in CircularRangeValue

    @Override
    protected boolean isIncrementGreaterThanRangeSize() {
        return Math.abs(range.getEnd() - range.getBeginning()) < Math.abs(intIncrement);
    }

    @Override
    protected Character peekNextValue() {
        int nextUnicode = (int) val + intIncrement;
        return (char) nextUnicode;
    }
}
