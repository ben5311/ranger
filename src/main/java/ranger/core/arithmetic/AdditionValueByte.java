package ranger.core.arithmetic;


import ranger.core.Value;

/**
 * Adds up two values and returns result as {@code Byte} type.
 */
public class AdditionValueByte extends AdditionValue<Byte> {

    /**
     * Creates Addition value with specified <code>summand1</code> and <code>summand2</code>.
     *
     * @param summand1 Value which will be used as summand1 for this addition.
     * @param summand2 Value which will be used as summand2 for this addition.
     */
    public AdditionValueByte(Value<? extends Number> summand1, Value<? extends Number> summand2) {
        super(summand1, summand2);
    }

    /*
    Copy constructor
     */
    protected AdditionValueByte(AdditionValueByte source) {
        super(source);
    }

    @Override
    protected AdditionValueByte clone() {
        return new AdditionValueByte(this);
    }

    @Override
    protected void eval() {
        val = (byte) (summand1.get().byteValue() + summand2.get().byteValue());
    }
}
