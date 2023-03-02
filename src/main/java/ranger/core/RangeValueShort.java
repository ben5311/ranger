package ranger.core;


import ranger.distribution.Distribution;

/**
 * Randomly generates {@link Short} value within specified range.
 */
public class RangeValueShort extends RangeValue<Short> {

    /**
     * Constructs range with specified <code>range</code>.
     *
     * @param range Short range.
     */
    public RangeValueShort(Range<Short> range) {
        super(range);
    }

    /**
     * Constructs range with specified <code>range</code> and <code>useEdgeCases</code>.
     *
     * @param range Short range.
     * @param useEdgeCases Indicates whether to create edge cases as first two values or not.
     */
    public RangeValueShort(Range<Short> range, boolean useEdgeCases) {
        super(range, useEdgeCases);
    }

    /**
     * Constructs range with specified <code>range</code>, <code>useEdgeCases</code> and <code>distribution</code>.
     *
     * @param range Short range.
     * @param useEdgeCases Indicates whether to create edge cases as first two values or not.
     * @param distribution Distribution to use for value selection.
     */
    public RangeValueShort(Range<Short> range, boolean useEdgeCases, Distribution distribution) {
        super(range, useEdgeCases, distribution);
    }

    /*
    Copy constructor
     */
    private RangeValueShort(RangeValueShort source) {
        super(source);
    }

    @Override
    protected RangeValueShort clone() {
        return new RangeValueShort(this);
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
            val = (short) (end - 1);
            return;
        }
        val = (short) distribution.nextInt(beginning, end);
    }
}
