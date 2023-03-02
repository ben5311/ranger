package ranger.core.arithmetic;


import ranger.core.Value;

/**
 * Divides two values and returns result as {@code Byte} type.
 */
public class DivisionValueByte extends DivisionValue<Byte> {

    /**
     * Creates Division value with specified <code>dividend</code> and <code>divisor</code>.
     *
     * @param dividend Value which will be used as dividend for this division.
     * @param divisor Value which will be used as divisor for this division.
     */
    public DivisionValueByte(Value<? extends Number> dividend, Value<? extends Number> divisor) {
        super(dividend, divisor);
    }

    /*
    Copy constructor
    */
    protected DivisionValueByte(DivisionValueByte source) {
        super(source);
    }

    @Override
    protected DivisionValueByte clone() {
        return new DivisionValueByte(this);
    }

    @Override
    protected void eval() {
        val = (byte) (dividend.get().byteValue() / divisor.get().byteValue());
    }
}
