package ranger.core.arithmetic;

import ranger.core.Value;
import ranger.core.ValueException;

/**
 * Abstract super class for all Values performing subtraction
 * @param <T> the Number type the Value returns
 */
public abstract class SubtractionValue<T extends Number> extends Value<T> {

    protected final Value<? extends Number> minuend;
    protected final Value<? extends Number> subtrahend;

    /**
     * Creates Subtraction value with specified <code>minuend</code> and <code>subtrahend</code>.
     *
     * @param minuend Value which will be used as minuend for this subtraction.
     * @param subtrahend Value which will be used as subtrahend for this subtraction.
     */
    public SubtractionValue(Value<? extends Number> minuend, Value<? extends Number> subtrahend) {
        if (minuend == null) { throw new ValueException("minuend must not be null"); }
        if (subtrahend == null) { throw new ValueException("subtrahend must not be null"); }
        this.minuend = minuend;
        this.subtrahend = subtrahend;
    }

    /*
    Copy constructor
     */
    protected SubtractionValue(SubtractionValue<T> source) {
        super(source);
        this.minuend = source.minuend.getClone();
        this.subtrahend = source.subtrahend.getClone();
    }

    @Override
    public void reset() {
        super.reset();
        minuend.reset();
        subtrahend.reset();
    }

    @Override
    protected abstract SubtractionValue<T> clone();

}
