package ranger.core;


import ranger.distribution.Distribution;

/**
 * Randomly generates {@link Byte} value within specified range.
 */
public class RangeValueByte extends RangeValue<Byte> {

    /**
     * Constructs range with specified <code>range</code>.
     *
     * @param range Byte range.
     */
    public RangeValueByte(Range<Byte> range) {
        super(range);
    }

    /**
     * Constructs range with specified <code>range</code> and <code>useEdgeCases</code>.
     *
     * @param range Byte range.
     * @param useEdgeCases Indicates whether to create edge cases as first two values or not.
     */
    public RangeValueByte(Range<Byte> range, boolean useEdgeCases) {
        super(range, useEdgeCases);
    }

    /**
     * Constructs range with specified <code>range</code>, <code>useEdgeCases</code> and <code>distribution</code>.
     *
     * @param range Byte range.
     * @param useEdgeCases Indicates whether to create edge cases as first two values or not.
     * @param distribution Distribution to use for value selection.
     */
    public RangeValueByte(Range<Byte> range, boolean useEdgeCases, Distribution distribution) {
        super(range, useEdgeCases, distribution);
    }

    /*
    Copy constructor
     */
    private RangeValueByte(RangeValueByte source) {
        super(source);
    }

    @Override
    protected RangeValueByte clone() {
        return new RangeValueByte(this);
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
            val = (byte) (end - 1);
            return;
        }
        val = (byte) distribution.nextInt(beginning, end);
    }
}
