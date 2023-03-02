package ranger.core.arithmetic;


import ranger.core.Value;

/**
 * Multiplies two values and returns result as {@code Long} type.
 */
public class MultiplicationValueLong extends MultiplicationValue<Long> {

    /**
     * Creates Multiplication value with specified <code>factor1</code> and <code>factor2</code>.
     *
     * @param factor1 Value which will be used as factor1 for this multiplication.
     * @param factor2 Value which will be used as factor2 for this multiplication.
     */
    public MultiplicationValueLong(Value<? extends Number> factor1, Value<? extends Number> factor2) {
        super(factor1, factor2);
    }

    /*
    Copy constructor
    */
    protected MultiplicationValueLong(MultiplicationValueLong source) {
        super(source);
    }

    @Override
    protected MultiplicationValueLong clone() {
        return new MultiplicationValueLong(this);
    }

    @Override
    protected void eval() {
        val = factor1.get().longValue() * factor2.get().longValue();
    }
}
