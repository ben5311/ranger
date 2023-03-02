package ranger.core;



import java.util.Date;

/**
 * Randomly generates {@link Date} value within specified range.
 */
public class CircularRangeValueDate extends CircularRangeValue<Date> {

    private static final long DAY_IN_MS = 1000*60*60*24;
    private final int intIncrement;

    /**
     * Constructs date circular range with specified <code>range</code> and <code>increment</code>.
     *
     * @param range Date range.
     */
    public CircularRangeValueDate(Range<Date> range, int increment) {
        super(range, range.isIncreasing() ? new Date(2*DAY_IN_MS) : new Date(0));     //just need the char increment to comply with CircularRangeValue
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
    private CircularRangeValueDate(CircularRangeValueDate source) {
        super(source);
        this.intIncrement = source.intIncrement;
    }

    @Override
    protected CircularRangeValueDate clone() {
        return new CircularRangeValueDate(this);
    }

    @Override
    protected Date zero() {
        return new Date(DAY_IN_MS);
    }   //bypass char increment check in CircularRangeValue

    @Override
    protected boolean isIncrementGreaterThanRangeSize() {
        return Math.abs(range.getEnd().getTime()-range.getBeginning().getTime())/DAY_IN_MS < Math.abs(intIncrement);
    }

    @Override
    protected Date peekNextValue() {
        return new Date(val.getTime() + intIncrement*DAY_IN_MS);
    }
}
