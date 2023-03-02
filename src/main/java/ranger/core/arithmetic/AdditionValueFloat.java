package ranger.core.arithmetic;


import ranger.core.Value;

/**
 * Adds up two values and returns result as {@code Float} type.
 */
public class AdditionValueFloat extends AdditionValue<Float> {

    /**
     * Creates Addition value with specified <code>summand1</code> and <code>summand2</code>.
     *
     * @param summand1 Value which will be used as summand1 for this addition.
     * @param summand2 Value which will be used as summand2 for this addition.
     */
    public AdditionValueFloat(Value<? extends Number> summand1, Value<? extends Number> summand2) {
        super(summand1, summand2);
    }

    /*
    Copy constructor
    */
    protected AdditionValueFloat(AdditionValueFloat source) {
        super(source);
    }

    @Override
    protected AdditionValueFloat clone() {
        return new AdditionValueFloat(this);
    }

    @Override
    protected void eval() {
        val = summand1.get().floatValue() + summand2.get().floatValue();
    }
}
