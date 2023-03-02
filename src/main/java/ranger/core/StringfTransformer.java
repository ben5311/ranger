package ranger.core;

import java.util.List;

/**
 * Creates a formatted String using the specified format String in printf syntax and specified values
 */
public class StringfTransformer extends StringTransformer {


    /**
     * Constructs stringf transformer with specified <code>format</code> String and List of <code>values</code>.
     * Syntax is printf like. See javadoc for String.format() for explanation
     *
     * @param formatValue Value that returns format string.
     * @param values List of values.
     * @throws ValueException if any argument or generated String from formatValue is null
     */
    public StringfTransformer(Value<String> formatValue, List<Value<?>> values) {
        super(formatValue, values);
    }

    /*
    Copy constructor
     */
    private StringfTransformer(StringfTransformer source) {
        super(source);

    }

    @Override
    protected StringfTransformer clone() {
        return new StringfTransformer(this);
    }

    @Override
    protected void eval() {
        String formatString = formatValue.get();
        if (formatString == null || formatString.isEmpty()) {
            throw new ValueException("format string cannot be null nor empty");
        }
        calculateValues();
        val = String.format(formatString, calculatedValues);
    }

}
