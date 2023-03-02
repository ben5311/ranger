package ranger.core.arithmetic;


import ranger.core.Value;

/**
 * Multiplies two values and returns result as {@code Byte} type.
 */
public class MultiplicationValueByte extends MultiplicationValue<Byte> {

    /**
     * Creates Multiplication value with specified <code>factor1</code> and <code>factor2</code>.
     *
     * @param factor1 Value which will be used as factor1 for this multiplication.
     * @param factor2 Value which will be used as factor2 for this multiplication.
     */
    public MultiplicationValueByte(Value<? extends Number> factor1, Value<? extends Number> factor2) {
        super(factor1, factor2);
    }

    /*
    Copy constructor
    */
    protected MultiplicationValueByte(MultiplicationValueByte source) {
        super(source);
    }

    @Override
    protected MultiplicationValueByte clone() {
        return new MultiplicationValueByte(this);
    }

    @Override
    protected void eval() {
        val = (byte) (factor1.get().byteValue() * factor2.get().byteValue());
    }
}
