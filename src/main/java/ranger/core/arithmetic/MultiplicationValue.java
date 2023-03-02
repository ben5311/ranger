package ranger.core.arithmetic;

import ranger.core.Value;
import ranger.core.ValueException;

/**
 * Abstract super class for all Values performing multiplication
 * @param <T> the Number type the Value returns
 */
public abstract class MultiplicationValue<T extends Number> extends Value<T> {

    protected final Value<? extends Number> factor1;
    protected final Value<? extends Number> factor2;

    /**
     * Creates Multiplication value with specified <code>factor1</code> and <code>factor2</code>.
     *
     * @param factor1 Value which will be used as factor1 for this multiplication.
     * @param factor2 Value which will be used as factor2 for this multiplication.
     */
    public MultiplicationValue(Value<? extends Number> factor1, Value<? extends Number> factor2) {
        if (factor1 == null) { throw new ValueException("left factor must not be null"); }
        if (factor2 == null) { throw new ValueException("right factor must not be null"); }
        this.factor1 = factor1;
        this.factor2 = factor2;
    }

    /*
    Copy constructor
     */
    protected MultiplicationValue(MultiplicationValue<T> source) {
        super(source);
        this.factor1 = source.factor1.getClone();
        this.factor2 = source.factor2.getClone();
    }

    @Override
    public void reset() {
        super.reset();
        factor1.reset();
        factor2.reset();
    }

    @Override
    protected abstract MultiplicationValue<T> clone();

}
