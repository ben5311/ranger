package ranger.core.arithmetic;


import ranger.core.Value;

/**
 * Subtracts two values and returns result as {@code Short} type.
 */
public class SubtractionValueShort extends SubtractionValue<Short> {

    /**
     * Creates Subtraction value with specified <code>minuend</code> and <code>subtrahend</code>.
     *
     * @param minuend Value which will be used as minuend for this subtraction.
     * @param subtrahend Value which will be used as subtrahend for this subtraction.
     */
    public SubtractionValueShort(Value<? extends Number> minuend, Value<? extends Number> subtrahend) {
        super(minuend, subtrahend);
    }

    /*
    Copy constructor
    */
    protected SubtractionValueShort(SubtractionValueShort source) {
        super(source);
    }

    @Override
    protected SubtractionValueShort clone() {
        return new SubtractionValueShort(this);
    }

    @Override
    protected void eval() {
        val = (short) (minuend.get().shortValue() - subtrahend.get().shortValue());
    }
}
