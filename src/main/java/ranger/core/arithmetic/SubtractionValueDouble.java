package ranger.core.arithmetic;


import ranger.core.Value;

/**
 * Subtracts two values and returns result as {@code Double} type.
 */
public class SubtractionValueDouble extends SubtractionValue<Double> {

    /**
     * Creates Subtraction value with specified <code>minuend</code> and <code>subtrahend</code>.
     *
     * @param minuend Value which will be used as minuend for this subtraction.
     * @param subtrahend Value which will be used as subtrahend for this subtraction.
     */
    public SubtractionValueDouble(Value<? extends Number> minuend, Value<? extends Number> subtrahend) {
        super(minuend, subtrahend);
    }

    /*
    Copy constructor
    */
    protected SubtractionValueDouble(SubtractionValueDouble source) {
        super(source);
    }

    @Override
    protected SubtractionValueDouble clone() {
        return new SubtractionValueDouble(this);
    }

    @Override
    protected void eval() {
        val = minuend.get().doubleValue() - subtrahend.get().doubleValue();
    }
}
