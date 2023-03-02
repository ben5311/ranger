package ranger.core;

import ranger.distribution.Distribution;

/**
 * Randomly generates {@link Float} value within specified range.
 */
@SuppressWarnings("DuplicatedCode")
public class RangeValueFloat extends RangeValue<Float> {

    /**
     * Epsilon value used for edge cases.
     */
    public static final Float EPSILON = 1E-5f;

    /**
     * Constructs range with specified <code>range</code>.
     *
     * @param range Float range.
     */
    public RangeValueFloat(Range<Float> range) {
        super(range);
    }

    /**
     * Constructs range with specified <code>range</code> and <code>useEdgeCases</code>.
     *
     * @param range Float range.
     * @param useEdgeCases Indicates whether to create edge cases as first two values or not.
     */
    public RangeValueFloat(Range<Float> range, boolean useEdgeCases) {
        super(range, useEdgeCases);
    }

    /**
     * Constructs range with specified <code>range</code>, <code>useEdgeCases</code> and <code>distribution</code>.
     *
     * @param range Float range.
     * @param useEdgeCases Indicates whether to create edge cases as first two values or not.
     * @param distribution Distribution to use for value selection.
     */
    public RangeValueFloat(Range<Float> range, boolean useEdgeCases, Distribution distribution) {
        super(range, useEdgeCases, distribution);
    }

    /*
    Copy constructor
     */
    private RangeValueFloat(RangeValueFloat source) {
        super(source);
    }

    @Override
    protected RangeValueFloat clone() {
        return new RangeValueFloat(this);
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
        val = (float) distribution.nextDouble(beginning, end);
    }
}
