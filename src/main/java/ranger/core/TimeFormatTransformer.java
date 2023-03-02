package ranger.core;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Creates a formatted string using a specified time format and long value representing time in epoch milliseconds.
 */
public class TimeFormatTransformer extends Transformer<String> {

    private final Value<String> formatValue;
    private final Value<?> value;
    private DateTimeFormatter dateTimeFormatter;
    private SimpleDateFormat dateFormatter;
    private String formatString = null;

    /**
     * Creates a formatted string with specified <code>format</code> and <code>value</code>. Format can be any date
     * format (e.g. 'YYYY-MM-dd', 'dd.MM.YYYY-hh:mm:ss').
     *
     * @param formatValue Value that returns format string.
     * @param value Value representing time, can return {@link Long}, {@link Date}, {@link LocalDate} or
     *            {@link LocalDateTime}.
     * @param <T> Type value parameter returns.
     * @throws ValueException if any argument or generated String from formatValue is null
     */
    public <T> TimeFormatTransformer(Value<String> formatValue, Value<T> value) {
        if (formatValue == null) {
            throw new ValueException("Format value cannot be null");
        }
        if (value == null) {
            throw new ValueException("Value cannot be null.");
        }
        this.formatValue = formatValue;
        this.value = value;
    }

    /*
    Copy constructor
     */
    private TimeFormatTransformer(TimeFormatTransformer source) {
        super(source);
        this.formatValue = source.formatValue.getClone();
        this.value = source.value.getClone();
        this.dateTimeFormatter = source.dateTimeFormatter;
        this.dateFormatter = source.dateFormatter;
        this.formatString = source.formatString;
    }

    @Override
    protected TimeFormatTransformer clone() {
        return new TimeFormatTransformer(this);
    }

    @Override
    public void reset() {
        super.reset();
        value.reset();
    }

    @Override
    protected void eval() {
        String nextFormatString = formatValue.get();
        if (nextFormatString == null || nextFormatString.isEmpty()) {
            throw new ValueException("Format string cannot be null nor empty.");
        }
        if (!nextFormatString.equals(formatString)) {   //only change dateFormatter when formatString changed
            this.formatString = nextFormatString;
            this.dateTimeFormatter = DateTimeFormatter.ofPattern(formatString);
            this.dateFormatter = new SimpleDateFormat(formatString);
        }
        Object generatedValue = value.get();
        if (generatedValue instanceof Long) {
            long epochMilli = (long) generatedValue;
            LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneId.systemDefault());
            val = date.format(dateTimeFormatter);
        }
        if (generatedValue instanceof Date) {
            Date date = (Date) generatedValue;
            val = dateFormatter.format(date);
        }
        if (generatedValue instanceof LocalDate) {
            LocalDate date = (LocalDate) generatedValue;
            val = date.format(dateTimeFormatter);
        }
        if (generatedValue instanceof LocalDateTime) {
            LocalDateTime date = (LocalDateTime) generatedValue;
            val = date.format(dateTimeFormatter);
        }
    }
}
