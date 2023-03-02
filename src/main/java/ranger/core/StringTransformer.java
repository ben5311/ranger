package ranger.core;

import org.slf4j.helpers.MessageFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates a formatted string using the specified format string and values.
 */
public class StringTransformer extends Transformer<String> {

    protected final Value<String> formatValue;
    protected final List<Value<?>> values;
    protected final Object[] calculatedValues;

    /**
     * Constructs string transformer with specified <code>format</code> string and list of <code>values</code>.
     * Placeholder for value is defined as '{}', first placeholder uses first value, second, second value, and so on.
     *
     * @param formatValue Value that returns format string.
     * @param values List of values.
     * @throws ValueException if any argument or generated String from formatValue is null
     */
    public StringTransformer(Value<String> formatValue, List<Value<?>> values) {
        if (formatValue == null) {
            throw new ValueException("Format value cannot be null");
        }
        if (values == null) {
            throw new ValueException("values cannot be null nor empty.");
        }
        this.formatValue = formatValue;
        this.values = values;
        this.calculatedValues = new Object[this.values.size()];
    }

    /*
    Copy constructor
     */
    protected StringTransformer(StringTransformer source) {
        super(source);
        this.formatValue = source.formatValue.getClone();
        this.values = new ArrayList<>(source.values.size());
        source.values.forEach(v -> this.values.add(v.getClone()));
        this.calculatedValues = source.calculatedValues;
    }

    @Override
    protected StringTransformer clone() {
        return new StringTransformer(this);
    }

    @Override
    public void reset() {
        super.reset();
        formatValue.reset();
        values.forEach(Value::reset);
    }

    @Override
    protected void eval() {
        String formatString = formatValue.get();
        if (formatString == null || formatString.isEmpty()) {
            throw new ValueException("format string cannot be null nor empty");
        }
        calculateValues();
        val = MessageFormatter.arrayFormat(formatString, calculatedValues).getMessage();
    }

    protected void calculateValues() {
        for (int i = 0; i < values.size(); i++) {
            Value<?> value = values.get(i);
            calculatedValues[i] = value.get();
        }
    }
}
