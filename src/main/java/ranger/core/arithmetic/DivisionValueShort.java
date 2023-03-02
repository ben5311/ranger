package ranger.core.arithmetic;


import ranger.core.Value;

/**
 * Divides two values and returns result as {@code Short} type.
 */
public class DivisionValueShort extends DivisionValue<Short> {

    /**
     * Creates Division value with specified <code>dividend</code> and <code>divisor</code>.
     *
     * @param dividend Value which will be used as dividend for this division.
     * @param divisor Value which will be used as divisor for this division.
     */
    public DivisionValueShort(Value<? extends Number> dividend, Value<? extends Number> divisor) {
        super(dividend, divisor);
    }

    /*
    Copy constructor
    */
    protected DivisionValueShort(DivisionValueShort source) {
        super(source);
    }

    @Override
    protected DivisionValueShort clone() {
        return new DivisionValueShort(this);
    }

    @Override
    protected void eval() {
        val = (short) (dividend.get().shortValue() / divisor.get().shortValue());
    }
}
