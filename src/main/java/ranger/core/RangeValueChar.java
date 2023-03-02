package ranger.core;

import ranger.distribution.Distribution;
import ranger.distribution.UniformDistribution;

/**
 * Randomly generates {@link Character} value within specified range.
 */
public class RangeValueChar extends RangeValue<Character> {

    /**
     * Constructs range with specified <code>range</code>.
     *
     * @param range Integer range.
     */
    public RangeValueChar(Range<Character> range) {
        this(range, false, new UniformDistribution());
    }

    /**
     * Constructs range with specified <code>range</code> and <code>useEdgeCases</code>.
     *
     * @param range Integer range.
     * @param useEdgeCases Indicates whether to create edge cases as first two values or not.
     */
    public RangeValueChar(Range<Character> range, boolean useEdgeCases) {
        this(range, useEdgeCases, new UniformDistribution());
    }

    /**
     * Constructs range with specified <code>range</code>, <code>useEdgeCases</code> and <code>distribution</code>.
     *
     * @param range Integer range.
     * @param useEdgeCases Indicates whether to create edge cases as first two values or not.
     * @param distribution Distribution to use for value selection.
     */
    public RangeValueChar(Range<Character> range, boolean useEdgeCases, Distribution distribution) {
        super(extendRange(range), useEdgeCases, distribution);
    }

    /*
    Copy constructor
     */
    private RangeValueChar(RangeValueChar source) {
        super(source);
    }

    /**
     * Swap the bounds if beginning and end are the wrong way around and extend upper bound by 1 to include last char.
     */
    private static Range<Character> extendRange(Range<Character> range) {
        if (range.getBeginning() <= range.getEnd()) {
            return new Range<>(range.getBeginning(), (char) (range.getEnd()+1));
        } else {
            return new Range<>(range.getEnd(), (char) (range.getBeginning()+1));
        }
    }

    @Override
    protected RangeValueChar clone() {
        return new RangeValueChar(this);
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
            val = (char) (end - 1);
            return;
        }
        int unicode = distribution.nextInt(beginning, end);
        val = (char) unicode;
    }
}
