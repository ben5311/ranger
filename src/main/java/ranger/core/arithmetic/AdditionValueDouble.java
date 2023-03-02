package ranger.core.arithmetic;


import ranger.core.Value;

/**
 * Adds up two values and returns result as {@code Double} type.
 */
public class AdditionValueDouble extends AdditionValue<Double> {

    /**
     * Creates Addition value with specified <code>summand1</code> and <code>summand2</code>.
     *
     * @param summand1 Value which will be used as summand1 for this addition.
     * @param summand2 Value which will be used as summand2 for this addition.
     */
    public AdditionValueDouble(Value<? extends Number> summand1, Value<? extends Number> summand2) {
        super(summand1, summand2);
    }

    /*
    Copy constructor
    */
    protected AdditionValueDouble(AdditionValueDouble source) {
        super(source);
    }

    @Override
    protected AdditionValueDouble clone() {
        return new AdditionValueDouble(this);
    }

    @Override
    protected void eval() {
        val = summand1.get().doubleValue() + summand2.get().doubleValue();
    }
}
