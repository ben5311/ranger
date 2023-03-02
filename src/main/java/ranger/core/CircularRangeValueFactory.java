package ranger.core;

import java.util.Date;

/**
 * Factory that creates sub types of {@link CircularRangeValue} based on <code>range</code> and <code>increment</code>
 * type.
 */
public class CircularRangeValueFactory {

    /**
     * Creates appropriate sub type of {@link CircularRangeValue} based on <code>range</code> and <code>increment</code>
     * type.
     *
     * @param range Range to use when creating {@link CircularRangeValue} sub type, cannot be null.
     * @param increment Number increment to use when creating {@link CircularRangeValue} sub type, cannot be null.
     * @return Sub type of {@link CircularRangeValue}.
     * @throws ValueException if any argument is null or if increment's type differs from range's type (e.g. increment of 0.5 with Integer range)
     */
    @SuppressWarnings("unchecked")
    public CircularRangeValue<?> create(Range<?> range, Number increment) {
        if (range == null) {
            throw new ValueException("Range cannot be null.");
        }
        if (increment == null) {
            throw new ValueException("Increment cannot be null.");
        }
        if (isType(Double.class, range) && increment instanceof Double) {
            return new CircularRangeValueDouble((Range<Double>) range, increment.doubleValue());
        }
        if (isType(Float.class, range) && increment instanceof Float) {
            return new CircularRangeValueFloat((Range<Float>) range, increment.floatValue());
        }
        if (isType(Long.class, range) && increment instanceof Long) {
            return new CircularRangeValueLong((Range<Long>) range, increment.longValue());
        }
        if (isType(Integer.class, range) && increment instanceof Integer) {
            return new CircularRangeValueInt((Range<Integer>) range, increment.intValue());
        }
        if (isType(Short.class, range) && increment instanceof Short) {
            return new CircularRangeValueShort((Range<Short>) range, increment.shortValue());
        }
        if (isType(Byte.class, range) && increment instanceof Byte) {
            return new CircularRangeValueByte((Range<Byte>) range, increment.byteValue());
        }
        if (isType(Character.class, range) && increment instanceof Integer) {
            return new CircularRangeValueChar((Range<Character>) range, increment.intValue());
        }
        if (isType(Date.class, range) && increment instanceof Integer) {
            return new CircularRangeValueDate((Range<Date>) range, (int) increment);
        }
        throw new ValueException("Range and increment type mismatch. Range type: "
                + range.getBeginning().getClass().getName() + " Increment type: " + increment.getClass().getName());
    }

    private boolean isType(Class<?> clazz, Range<?> range) {
        return clazz.isInstance(range.getBeginning());
    }
}
