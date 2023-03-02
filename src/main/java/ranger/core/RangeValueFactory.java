package ranger.core;

import ranger.distribution.Distribution;

import java.util.Date;

/**
 * Factory that creates sub types of {@link RangeValue} based on <code>range</code> type.
 */
public class RangeValueFactory {

    /**
     * Creates appropriate sub type of {@link RangeValue} based on <code>range</code> type.
     *
     * @param range Range to use when creating {@link RangeValue} sub type, cannot be null.
     * @param useEdgeCases Determines whether edge cases will be used or not, can be null.
     * @param distribution Distribution to use when creating {@link RangeValue} sub type, can be null.
     * @return Sub type of {@link RangeValue}.
     * @throws ValueException if range is null or range's type is not supported
     */
    @SuppressWarnings("unchecked")
    public RangeValue<?> create(Range<?> range, Boolean useEdgeCases, Distribution distribution) {
        if (range == null) {
            throw new ValueException("Range cannot be null.");
        }
        boolean edgeCases = useEdgeCases != null ? useEdgeCases : RangeValue.defaultUseEdgeCases();
        Distribution dist = distribution != null ? distribution : RangeValue.defaultDistrbution();
        if (isType(Double.class, range)) {
            return new RangeValueDouble((Range<Double>) range, edgeCases, dist);
        }
        if (isType(Float.class, range)) {
            return new RangeValueFloat((Range<Float>) range, edgeCases, dist);
        }
        if (isType(Long.class, range)) {
            return new RangeValueLong((Range<Long>) range, edgeCases, dist);
        }
        if (isType(Integer.class, range)) {
            return new RangeValueInt((Range<Integer>) range, edgeCases, dist);
        }
        if (isType(Short.class, range)) {
            return new RangeValueShort((Range<Short>) range, edgeCases, dist);
        }
        if (isType(Byte.class, range)) {
            return new RangeValueByte((Range<Byte>) range, edgeCases, dist);
        }
        if (isType(Character.class, range)) {
            return new RangeValueChar((Range<Character>) range, edgeCases, dist);
        }
        if (isType(Date.class, range)) {
            return new RangeValueDate((Range<Date>) range, edgeCases, dist);
        }
        throw new ValueException("Unsupported range type: " + range.getBeginning().getClass().getName());
    }

    private boolean isType(Class<?> clazz, Range<?> range) {
        return clazz.isInstance(range.getBeginning());
    }
}
