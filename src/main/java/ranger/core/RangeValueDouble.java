package ranger.core;

import ranger.distribution.Distribution;

/**
 * Randomly generates {@link Double} value within specified range.
 */
@SuppressWarnings("ALL")
public class RangeValueDouble extends RangeValue<Double> {

    /**
     * Epsilon value used for edge cases.
     */
    public static final Double EPSILON = 1E-11;

    /**
     * Constructs range with specified <code>range</code>.
     *
     * @param range Double range.
     */
    public RangeValueDouble(Range<Double> range) {
        super(range);
    }

    /**
     * Constructs range with specified <code>range</code> and <code>useEdgeCases</code>.
     *
     * @param range Double range.
     * @param useEdgeCases Indicates whether to create edge cases as first two values or not.
     */
    public RangeValueDouble(Range<Double> range, boolean useEdgeCases) {
        super(range, useEdgeCases);
    }

    /**
     * Constructs range with specified <code>range</code>, <code>useEdgeCases</code> and <code>distribution</code>.
     *
     * @param range Double range.
     * @param useEdgeCases Indicates whether to create edge cases as first two values or not.
     * @param distribution Distribution to use for value selection.
     */
    public RangeValueDouble(Range<Double> range, boolean useEdgeCases, Distribution distribution) {
        super(range, useEdgeCases, distribution);
    }

    /*
    Copy constructor
     */
    private RangeValueDouble(RangeValueDouble source) {
        super(source);
    }

    @Override
    protected RangeValueDouble clone() {
        return new RangeValueDouble(this);
    }

    @Override
    protected void eval() {
        if (useEdgeCases && !beginningEdgeCaseUsed) {
            beginningEdgeCaseUsed = true;
            val = beginning;
            return;
        }
        if (useEdgeCases && !endEdgeCaseUsed) {
            endEdgeCaseUsed = true;
            val = end - EPSILON;
            return;
        }
        val = distribution.nextDouble(beginning, end);
    }
}
