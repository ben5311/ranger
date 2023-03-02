package ranger.core;

import ranger.distribution.Distribution;

/**
 * Randomly generates {@link Integer} value within specified range.
 */
@SuppressWarnings("DuplicatedCode")
public class RangeValueInt extends RangeValue<Integer> {

    /**
     * Constructs range with specified <code>range</code>.
     *
     * @param range Integer range.
     */
    public RangeValueInt(Range<Integer> range) {
        super(range);
    }

    /**
     * Constructs range with specified <code>range</code> and <code>useEdgeCases</code>.
     *
     * @param range Integer range.
     * @param useEdgeCases Indicates whether to create edge cases as first two values or not.
     */
    public RangeValueInt(Range<Integer> range, boolean useEdgeCases) {
        super(range, useEdgeCases);
    }

    /**
     * Constructs range with specified <code>range</code>, <code>useEdgeCases</code> and <code>distribution</code>.
     *
     * @param range Integer range.
     * @param useEdgeCases Indicates whether to create edge cases as first two values or not.
     * @param distribution Distribution to use for value selection.
     */
    public RangeValueInt(Range<Integer> range, boolean useEdgeCases, Distribution distribution) {
        super(range, useEdgeCases, distribution);
    }

    /*
    Copy constructor
     */
    private RangeValueInt(RangeValueInt source) {
        super(source);
    }

    @Override
    protected RangeValueInt clone() {
        return new RangeValueInt(this);
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
        val = distribution.nextInt(beginning, end);
    }
}
