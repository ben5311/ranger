package ranger.core.arithmetic;


import ranger.core.Value;

/**
 * Divides two values and returns result as {@code Long} type.
 */
public class DivisionValueLong extends DivisionValue<Long> {

    /**
     * Creates Division value with specified <code>dividend</code> and <code>divisor</code>.
     *
     * @param dividend Value which will be used as dividend for this division.
     * @param divisor Value which will be used as divisor for this division.
     */
    public DivisionValueLong(Value<? extends Number> dividend, Value<? extends Number> divisor) {
        super(dividend, divisor);
    }

    /*
    Copy constructor
    */
    protected DivisionValueLong(DivisionValueLong source) {
        super(source);
    }

    @Override
    protected DivisionValueLong clone() {
        return new DivisionValueLong(this);
    }

    @Override
    protected void eval() {
        val = dividend.get().longValue() / divisor.get().longValue();
    }
}
