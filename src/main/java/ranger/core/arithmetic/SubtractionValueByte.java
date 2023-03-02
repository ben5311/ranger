package ranger.core.arithmetic;


import ranger.core.Value;

/**
 * Subtracts two values and returns result as {@code Byte} type.
 */
public class SubtractionValueByte extends SubtractionValue<Byte> {

    /**
     * Creates Subtraction value with specified <code>minuend</code> and <code>subtrahend</code>.
     *
     * @param minuend Value which will be used as minuend for this subtraction.
     * @param subtrahend Value which will be used as subtrahend for this subtraction.
     */
    public SubtractionValueByte(Value<? extends Number> minuend, Value<? extends Number> subtrahend) {
        super(minuend, subtrahend);
    }

    /*
    Copy constructor
    */
    protected SubtractionValueByte(SubtractionValueByte source) {
        super(source);
    }

    @Override
    protected SubtractionValueByte clone() {
        return new SubtractionValueByte(this);
    }

    @Override
    protected void eval() {
        val = (byte) (minuend.get().byteValue() - subtrahend.get().byteValue());
    }
}
