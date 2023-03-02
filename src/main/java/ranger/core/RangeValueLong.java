package ranger.core;


import ranger.distribution.Distribution;

/**
 * Randomly generates long value within specified range.
 */
@SuppressWarnings("DuplicatedCode")
public class RangeValueLong extends RangeValue<Long> {

    /**
     * Constructs range with specified <code>range</code>.
     *
     * @param range Long range.
     */
    public RangeValueLong(Range<Long> range) {
        super(range);
    }

    /**
     * Constructs range with specified <code>range</code> and <code>useEdgeCases</code>.
     *
     * @param range Long range.
     * @param useEdgeCases Indicates whether to create edge cases as first two values or not.
     */
    public RangeValueLong(Range<Long> range, boolean useEdgeCases) {
        super(range, useEdgeCases);
    }

    /**
     * Constructs range with specified <code>range</code>, <code>useEdgeCases</code> and <code>distribution</code>.
     *
     * @param range Long range.
     * @param useEdgeCases Indicates whether to create edge cases as first two values or not.
     * @param distribution Distribution to use for value selection.
     */
    public RangeValueLong(Range<Long> range, boolean useEdgeCases, Distribution distribution) {
        super(range, useEdgeCases, distribution);
    }

    /*
    Copy constructor
     */
    private RangeValueLong(RangeValueLong source) {
        super(source);
    }

    @Override
    protected RangeValueLong clone() {
        return new RangeValueLong(this);
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
            val = end - 1;
            return;
        }
        val = distribution.nextLong(beginning, end);
    }
}
