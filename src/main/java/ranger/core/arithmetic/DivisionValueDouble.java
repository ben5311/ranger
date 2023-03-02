package ranger.core.arithmetic;


import ranger.core.Value;

/**
 * Divides two values and returns result as {@code Double} type.
 */
public class DivisionValueDouble extends DivisionValue<Double> {

    /**
     * Creates Division value with specified <code>dividend</code> and <code>divisor</code>.
     *
     * @param dividend Value which will be used as dividend for this division.
     * @param divisor Value which will be used as divisor for this division.
     */
    public DivisionValueDouble(Value<? extends Number> dividend, Value<? extends Number> divisor) {
        super(dividend, divisor);
    }

    /*
    Copy constructor
    */
    protected DivisionValueDouble(DivisionValueDouble source) {
        super(source);
    }

    @Override
    protected DivisionValueDouble clone() {
        return new DivisionValueDouble(this);
    }

    @Override
    protected void eval() {
        val = dividend.get().doubleValue() / divisor.get().doubleValue();
    }
}
