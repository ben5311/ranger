package ranger.core.arithmetic;


import ranger.core.Value;

/**
 * Adds up two values and returns result as {@code Short} type.
 */
public class AdditionValueShort extends AdditionValue<Short> {

    /**
     * Creates Addition value with specified <code>summand1</code> and <code>summand2</code>.
     *
     * @param summand1 Value which will be used as summand1 for this addition.
     * @param summand2 Value which will be used as summand2 for this addition.
     */
    public AdditionValueShort(Value<? extends Number> summand1, Value<? extends Number> summand2) {
        super(summand1, summand2);
    }

    /*
    Copy constructor
    */
    protected AdditionValueShort(AdditionValueShort source) {
        super(source);
    }

    @Override
    protected AdditionValueShort clone() {
        return new AdditionValueShort(this);
    }

    @Override
    protected void eval() {
        val = (short) (summand1.get().shortValue() + summand2.get().shortValue());
    }
}
