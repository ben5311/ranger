package ranger.core.arithmetic;

import ranger.core.Value;
import ranger.core.ValueException;

/**
 * Abstract super class for all Values performing addition
 * @param <T> the Number type the Value returns
 */
public abstract class AdditionValue<T extends Number> extends Value<T> {

    protected final Value<? extends Number> summand1;
    protected final Value<? extends Number> summand2;

    /**
     * Creates Addition value with specified <code>summand1</code> and <code>summand2</code>.
     *
     * @param summand1 Value which will be used as summand1 for this addition.
     * @param summand2 Value which will be used as summand2 for this addition.
     */
    public AdditionValue(Value<? extends Number> summand1, Value<? extends Number> summand2) {
        if (summand1 == null) { throw new ValueException("left summand must not be null"); }
        if (summand2 == null) { throw new ValueException("right summand must not be null"); }
        this.summand1 = summand1;
        this.summand2 = summand2;
    }

    /*
    Copy constructor
     */
    protected AdditionValue(AdditionValue<T> source) {
        super(source);
        this.summand1 = source.summand1.getClone();
        this.summand2 = source.summand2.getClone();
    }

    @Override
    public void reset() {
        super.reset();
        summand1.reset();
        summand2.reset();
    }

    @Override
    protected abstract AdditionValue<T> clone();

}
