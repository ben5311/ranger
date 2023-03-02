package ranger.core.arithmetic;


import ranger.core.Value;

/**
 * Subtracts two values and returns result as {@code Float} type.
 */
public class SubtractionValueFloat extends SubtractionValue<Float> {

    /**
     * Creates Subtraction value with specified <code>minuend</code> and <code>subtrahend</code>.
     *
     * @param minuend Value which will be used as minuend for this subtraction.
     * @param subtrahend Value which will be used as subtrahend for this subtraction.
     */
    public SubtractionValueFloat(Value<? extends Number> minuend, Value<? extends Number> subtrahend) {
        super(minuend, subtrahend);
    }

    /*
    Copy constructor
    */
    protected SubtractionValueFloat(SubtractionValueFloat source) {
        super(source);
    }

    @Override
    protected SubtractionValueFloat clone() {
        return new SubtractionValueFloat(this);
    }

    @Override
    protected void eval() {
        val = minuend.get().floatValue() - subtrahend.get().floatValue();
    }
}
