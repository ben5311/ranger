package ranger.core.arithmetic;


import ranger.core.Value;

/**
 * Subtracts two values and returns result as {@code Long} type.
 */
public class SubtractionValueLong extends SubtractionValue<Long> {

    /**
     * Creates Subtraction value with specified <code>minuend</code> and <code>subtrahend</code>.
     *
     * @param minuend Value which will be used as minuend for this subtraction.
     * @param subtrahend Value which will be used as subtrahend for this subtraction.
     */
    public SubtractionValueLong(Value<? extends Number> minuend, Value<? extends Number> subtrahend) {
        super(minuend, subtrahend);
    }

    /*
    Copy constructor
    */
    protected SubtractionValueLong(SubtractionValueLong source) {
        super(source);
    }

    @Override
    protected SubtractionValueLong clone() {
        return new SubtractionValueLong(this);
    }

    @Override
    protected void eval() {
        val = minuend.get().longValue() - subtrahend.get().longValue();
    }
}
