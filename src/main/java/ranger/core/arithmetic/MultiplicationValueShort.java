package ranger.core.arithmetic;


import ranger.core.Value;

/**
 * Multiplies two values and returns result as {@code Short} type.
 */
public class MultiplicationValueShort extends MultiplicationValue<Short> {

    /**
     * Creates Multiplication value with specified <code>factor1</code> and <code>factor2</code>.
     *
     * @param factor1 Value which will be used as factor1 for this multiplication.
     * @param factor2 Value which will be used as factor2 for this multiplication.
     */
    public MultiplicationValueShort(Value<? extends Number> factor1, Value<? extends Number> factor2) {
        super(factor1, factor2);
    }

    /*
    Copy constructor
    */
    protected MultiplicationValueShort(MultiplicationValueShort source) {
        super(source);
    }

    @Override
    protected MultiplicationValueShort clone() {
        return new MultiplicationValueShort(this);
    }

    @Override
    protected void eval() {
        val = (short) (factor1.get().shortValue() * factor2.get().shortValue());
    }
}
