package ranger.core;


import ranger.distribution.Distribution;
import ranger.distribution.UniformDistribution;

/**
 * Base range value class. Randomly generates value within specified range.
 *
 * @param <T> Type this value would evaluate to.
 */
public abstract class RangeValue<T extends Comparable<T>> extends Value<T> {

    /**
     * Beginning value of the range.
     */
    protected final T beginning;

    /**
     * End of the range.
     */
    protected final T end;

    /**
     * Indicates whether to create edge cases as first two values or not.
     */
    protected final boolean useEdgeCases;

    /**
     * Distribution to use.
     */
    protected final Distribution distribution;

    protected boolean beginningEdgeCaseUsed = false;
    protected boolean endEdgeCaseUsed = false;

    /**
     * Constructs range value with specified <code>range</code>. <code>useEdgeCases</code> is set to
     * <code>true</code> and <code>distribution</code> is set to {@link UniformDistribution}.
     *
     * @param range Range.
     */
    public RangeValue(Range<T> range) {
        this(range, defaultUseEdgeCases());
    }

    /**
     * Constructs range value with specified <code>range</code> and <code>useEdgeCases</code>.
     * <code>distribution</code> is set to {@link UniformDistribution}.
     *
     * @param range Range.
     * @param useEdgeCases Indicates whether to create edge cases as first two values or not.
     */
    public RangeValue(Range<T> range, boolean useEdgeCases) {
        this(range, useEdgeCases, defaultDistrbution());
    }

    /**
     * Constructs range value with specified <code>range</code>, <code>useEdgeCases</code> and
     * <code>distribution</code>.
     *
     * @param range Range.
     * @param useEdgeCases Indicates whether to create edge cases as first two values or not.
     * @param distribution Distribution to use for value selection.
     * @throws ValueException if any argument is null or range is not increasing
     */
    public RangeValue(Range<T> range, boolean useEdgeCases, Distribution distribution) {
        if (range == null) { throw new ValueException("Range cannot be null."); }
        if (!range.isIncreasing()) { throw new InvalidRangeBoundsException("End of range must be greater than beginning of range."); }
        if (distribution == null) { throw new ValueException("Distribution cannot be null."); }
        this.beginning = range.getBeginning();
        this.end = range.getEnd();
        this.useEdgeCases = useEdgeCases;
        this.distribution = distribution;
    }

    /*
    Copy constructor
     */
    protected RangeValue(RangeValue<T> source) {
        super(source);
        this.beginning = source.beginning;
        this.end = source.end;
        this.useEdgeCases = source.useEdgeCases;
        this.distribution = source.distribution.clone();
        this.beginningEdgeCaseUsed = source.beginningEdgeCaseUsed;
        this.endEdgeCaseUsed = source.endEdgeCaseUsed;
    }

    /**
     * Default value for <code>useEdgeCases</code> property.
     *
     * @return Default value for <code>useEdgeCases</code> property.
     */
    public static boolean defaultUseEdgeCases() {
        return false;
    }

    /**
     * Default value for <code>distribution</code> property.
     *
     * @return Default value for <code>distribution</code> property.
     */
    public static Distribution defaultDistrbution() {
        return new UniformDistribution();
    }

    @Override
    protected abstract RangeValue<T> clone();
}
