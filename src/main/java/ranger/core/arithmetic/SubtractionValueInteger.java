package ranger.core.arithmetic;


import ranger.core.Value;

/**
 * Subtracts two values and returns result as {@code Integer} type.
 */
public class SubtractionValueInteger extends SubtractionValue<Integer> {

    /**
     * Creates Subtraction value with specified <code>minuend</code> and <code>subtrahend</code>.
     *
     * @param minuend Value which will be used as minuend for this subtraction.
     * @param subtrahend Value which will be used as subtrahend for this subtraction.
     */
    public SubtractionValueInteger(Value<? extends Number> minuend, Value<? extends Number> subtrahend) {
        super(minuend, subtrahend);
    }

    /*
    Copy constructor
    */
    protected SubtractionValueInteger(SubtractionValueInteger source) {
        super(source);
    }

    @Override
    protected SubtractionValueInteger clone() {
        return new SubtractionValueInteger(this);
    }

    @Override
    protected void eval() {
        val = minuend.get().intValue() - subtrahend.get().intValue();
    }
}
