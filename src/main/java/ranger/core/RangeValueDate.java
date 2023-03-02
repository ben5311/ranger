package ranger.core;



import ranger.distribution.Distribution;

import java.util.Date;

/**
 * Randomly generates {@link Date} value within specified range.
 */
public class RangeValueDate extends RangeValue<Date> {

    /**
     * Constructs range with specified <code>range</code>.
     *
     * @param range Double range.
     */
    public RangeValueDate(Range<Date> range) {
        super(range);
    }

    /**
     * Constructs range with specified <code>range</code> and <code>useEdgeCases</code>.
     *
     * @param range Double range.
     * @param useEdgeCases Indicates whether to create edge cases as first two values or not.
     */
    public RangeValueDate(Range<Date> range, boolean useEdgeCases) {
        super(range, useEdgeCases);
    }

    /**
     * Constructs range with specified <code>range</code>, <code>useEdgeCases</code> and <code>distribution</code>.
     *
     * @param range Double range.
     * @param useEdgeCases Indicates whether to create edge cases as first two values or not.
     * @param distribution Distribution to use for value selection.
     */
    public RangeValueDate(Range<Date> range, boolean useEdgeCases, Distribution distribution) {
        super(range, useEdgeCases, distribution);
    }

    /*
    Copy constructor
     */
    private RangeValueDate(RangeValueDate source) {
        super(source);
    }

    @Override
    protected RangeValueDate clone() {
        return new RangeValueDate(this);
    }

    @Override
    protected void eval() {
        if (useEdgeCases && !beginningEdgeCaseUsed) {
            beginningEdgeCaseUsed = true;
            val = new Date(beginning.getTime());
            return;
        }
        if (useEdgeCases && !endEdgeCaseUsed) {
            endEdgeCaseUsed = true;
            val = new Date(end.getTime() - 1);
            return;
        }
        val = new Date(distribution.nextLong(beginning.getTime(), end.getTime()));
    }
}
