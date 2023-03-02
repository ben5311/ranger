package ranger.core;



import ranger.distribution.Distribution;
import ranger.distribution.UniformDistribution;

import java.time.LocalDate;

/**
 * Randomly generates {@link LocalDate} value within specified range.
 */
public class RangeValueLocalDate extends Value<LocalDate> {

    private final LocalDate beginning;
    private final LocalDate end;
    private final boolean useEdgeCases;
    private final Distribution distribution;

    private boolean beginningEdgeCaseUsed = false;
    private boolean endEdgeCaseUsed = false;

    /**
     * Constructs range value with specified <code>range</code>. <code>useEdgeCases</code> is set to
     * <code>true</code> and <code>distribution</code> is set to {@link UniformDistribution}.
     *
     * @param beginning The beginning of the range.
     * @param end The end of the range.
     */
    public RangeValueLocalDate(LocalDate beginning, LocalDate end) {
        this(beginning, end, true);
    }

    /**
     * Constructs range value with specified <code>range</code> and <code>useEdgeCases</code>.
     * <code>distribution</code> is set to {@link UniformDistribution}.
     *
     * @param beginning The beginning of the range.
     * @param end The end of the range.
     * @param useEdgeCases Indicates whether to create edge cases as first two values or not.
     */
    public RangeValueLocalDate(LocalDate beginning, LocalDate end, boolean useEdgeCases) {
        this(beginning, end, useEdgeCases, new UniformDistribution());
    }

    /**
     * Constructs range value with specified <code>range</code>, <code>useEdgeCases</code> and
     * <code>distribution</code>.
     *
     * @param beginning The beginning of the range.
     * @param end The end of the range.
     * @param useEdgeCases Indicates whether to create edge cases as first two values or not.
     * @param distribution Distribution to use for value selection.
     * @throws InvalidRangeBoundsException if range is not increasing
     * @throws ValueException if any argument is null
     */
    public RangeValueLocalDate(LocalDate beginning, LocalDate end, boolean useEdgeCases, Distribution distribution) {
        if (beginning == null) {
            throw new ValueException("Beginning cannot be null.");
        }
        if (end == null) {
            throw new ValueException("End cannot be null.");
        }
        if (!isRangeIncreasing(beginning, end)) {
            throw new InvalidRangeBoundsException("End of the range must be greater than the beginning of the range.");
        }
        if (distribution == null) {
            throw new ValueException("Distribution cannot be null.");
        }
        this.beginning = beginning;
        this.end = end;
        this.useEdgeCases = useEdgeCases;
        this.distribution = distribution;
    }

    /*
    Copy constructor
     */
    private RangeValueLocalDate(RangeValueLocalDate source) {
        super(source);
        this.beginning = source.beginning;
        this.end = source.end;
        this.useEdgeCases = source.useEdgeCases;
        this.distribution = source.distribution.clone();
        this.beginningEdgeCaseUsed = source.beginningEdgeCaseUsed;
        this.endEdgeCaseUsed = source.endEdgeCaseUsed;
    }

    @Override
    protected RangeValueLocalDate clone() {
        return new RangeValueLocalDate(this);
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
            val = end.minusDays(1);
            return;
        }
        val = LocalDate.ofEpochDay(distribution.nextLong(beginning.toEpochDay(), end.toEpochDay()));
    }

    private boolean isRangeIncreasing(LocalDate beginning, LocalDate end) {
        return beginning.compareTo(end) < 0;
    }
}
