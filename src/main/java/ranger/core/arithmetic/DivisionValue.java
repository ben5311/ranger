package ranger.core.arithmetic;

import ranger.core.Value;
import ranger.core.ValueException;

/**
 * Abstract super class for all Values performing division
 * @param <T> the Number type the Value returns
 */
public abstract class DivisionValue<T extends Number> extends Value<T> {

    protected final Value<? extends Number> dividend;
    protected final Value<? extends Number> divisor;

    /**
     * Creates Division value with specified <code>dividend</code> and <code>divisor</code>.
     *
     * @param dividend Value which will be used as dividend for this division.
     * @param divisor Value which will be used as divisor for this division.
     */
    public DivisionValue(Value<? extends Number> dividend, Value<? extends Number> divisor) {
        if (dividend == null) { throw new ValueException("dividend must not be null"); }
        if (divisor == null) { throw new ValueException("divisor must not be null"); }
        this.dividend = dividend;
        this.divisor = divisor;
    }

    /*
    Copy constructor
     */
    protected DivisionValue(DivisionValue<T> source) {
        super(source);
        this.dividend = source.dividend.getClone();
        this.divisor = source.divisor.getClone();
    }

    @Override
    public void reset() {
        super.reset();
        dividend.reset();
        divisor.reset();
    }

    @Override
    protected abstract DivisionValue<T> clone();

}
