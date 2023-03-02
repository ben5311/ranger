package ranger.core.arithmetic;


import ranger.core.Value;

/**
 * Adds up two values and returns result as {@code Long} type.
 */
public class AdditionValueLong extends AdditionValue<Long> {

    /**
     * Creates Addition value with specified <code>summand1</code> and <code>summand2</code>.
     *
     * @param summand1 Value which will be used as summand1 for this addition.
     * @param summand2 Value which will be used as summand2 for this addition.
     */
    public AdditionValueLong(Value<? extends Number> summand1, Value<? extends Number> summand2) {
        super(summand1, summand2);
    }

    /*
    Copy constructor
    */
    protected AdditionValueLong(AdditionValueLong source) {
        super(source);
    }

    @Override
    protected AdditionValueLong clone() {
        return new AdditionValueLong(this);
    }

    @Override
    protected void eval() {
        val = summand1.get().longValue() + summand2.get().longValue();
    }
}
