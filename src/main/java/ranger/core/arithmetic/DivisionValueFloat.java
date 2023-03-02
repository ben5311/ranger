package ranger.core.arithmetic;


import ranger.core.Value;

/**
 * Divides two values and returns result as {@code Float} type.
 */
public class DivisionValueFloat extends DivisionValue<Float> {

    /**
     * Creates Division value with specified <code>dividend</code> and <code>divisor</code>.
     *
     * @param dividend Value which will be used as dividend for this division.
     * @param divisor Value which will be used as divisor for this division.
     */
    public DivisionValueFloat(Value<? extends Number> dividend, Value<? extends Number> divisor) {
        super(dividend, divisor);
    }

    /*
    Copy constructor
    */
    protected DivisionValueFloat(DivisionValueFloat source) {
        super(source);
    }

    @Override
    protected DivisionValueFloat clone() {
        return new DivisionValueFloat(this);
    }

    @Override
    protected void eval() {
        val = dividend.get().floatValue() / divisor.get().floatValue();
    }
}
