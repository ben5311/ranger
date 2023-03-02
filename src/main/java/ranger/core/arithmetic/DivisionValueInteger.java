package ranger.core.arithmetic;


import ranger.core.Value;

/**
 * Divides two values and returns result as {@code Integer} type.
 */
public class DivisionValueInteger extends DivisionValue<Integer> {

    /**
     * Creates Division value with specified <code>dividend</code> and <code>divisor</code>.
     *
     * @param dividend Value which will be used as dividend for this division.
     * @param divisor Value which will be used as divisor for this division.
     */
    public DivisionValueInteger(Value<? extends Number> dividend, Value<? extends Number> divisor) {
        super(dividend, divisor);
    }

    /*
    Copy constructor
    */
    protected DivisionValueInteger(DivisionValueInteger source) {
        super(source);
    }

    @Override
    protected DivisionValueInteger clone() {
        return new DivisionValueInteger(this);
    }

    @Override
    protected void eval() {
        val = dividend.get().intValue() / divisor.get().intValue();
    }
}
