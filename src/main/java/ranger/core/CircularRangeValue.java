package ranger.core;

/**
 * Base circular range value class. Generates values within specified <code>range</code> from the beginning to the end
 * with specified <code>increment</code>.
 *
 * @param <T> Type this value would evaluate to.
 */
public abstract class CircularRangeValue<T extends Comparable<T>> extends Value<T> {

    /**
     * Range.
     */
    protected final Range<T> range;

    /**
     * Increment.
     */
    protected final T increment;

    /**
     * Constructs circular range value with specified <code>range</code> and <code>increment</code>.
     *
     * @param range Range.
     * @param increment Increment.
     * @throws ValueException if any argument is null, range is empty, or if increment is zero or increment does not fit to range type (e. g. increment of -1 with increasing range)
     */
    public CircularRangeValue(Range<T> range, T increment) {
        if (range == null) {
            throw new ValueException("Range cannot be null.");
        }
        if (increment == null || increment.compareTo(zero()) == 0) {
            throw new ValueException("Increment cannot be null nor zero.");
        }
        if (range.isEmpty()) {
            throw new ValueException("Range cannot be empty.");
        }
        if (range.isIncreasing() && increment.compareTo(zero()) < 0) {
            throw new ValueException("If range is increasing, increment must be positive.");
        }
        if (range.isDecreasing() && increment.compareTo(zero()) > 0) {
            throw new ValueException("If range is decreasing, increment must be negative.");
        }
        this.range = range;
        this.increment = increment;
        this.val = range.getBeginning();
        this.evaluated = true;
        if (isIncrementGreaterThanRangeSize()) {
            throw new ValueException("Range size must be greater than increment.");
        }
    }

    /*
    Copy constructor
     */
    protected CircularRangeValue(CircularRangeValue<T> source) {
        super(source);
        this.range = source.range;
        this.increment = source.increment;
    }

    @Override
    protected abstract CircularRangeValue<T> clone();

    @Override
    protected void eval() {
        T nextValue = peekNextValue();
        if (isValueInBounds(nextValue)) {
            val = nextValue;
        } else {
            val = range.getBeginning();
        }
    }

    private boolean isValueInBounds(T value) {
        return (range.isIncreasing() && value.compareTo(range.getEnd()) <= 0)
            || (range.isDecreasing() && value.compareTo(range.getEnd()) >= 0);
    }

    /**
     * Returns {@code 0} value represented within {@code <T>} type.
     *
     * @return {@code 0} value represented within {@code <T>} type.
     */
    protected abstract T zero();

    /**
     * Indicates whether increment value is greater than range size or not.
     *
     * @return True if increment value is greater than range size, otherwise false.
     */
    protected abstract boolean isIncrementGreaterThanRangeSize();

    /**
     * Returns next value without changing state of <code>currentValue</code>.
     *
     * @return Next value without changing state of <code>currentValue</code>.
     */
    protected abstract T peekNextValue();
}
