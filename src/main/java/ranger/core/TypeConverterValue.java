package ranger.core;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Converts value to specified type.
 *
 * @param <T> Type to which to convert value.
 */
public class TypeConverterValue<T> extends Transformer<T> {

    private final Class<T> objectType;
    private final Value<?> value;
    private final ObjectMapper objectMapper;

    /**
     * Constructs {@link TypeConverterValue} with specified <code>objectType</code> and <code>value</code>. New default
     * instance of {@link ObjectMapper} will be used.
     *
     * @param objectType Type to which to covert value.
     * @param value The value.
     */
    public TypeConverterValue(Class<T> objectType, Value<?> value) {
        this(objectType, value, new ObjectMapper());
    }

    /**
     * Constructs {@link TypeConverterValue} with specified <code>objectType</code>, <code>value</code> and
     * <code>objectMapper</code>.
     *
     * @param objectType Type to which to covert value.
     * @param value The value.
     * @param objectMapper Object mapper to use in conversion process.
     * @throws ValueException if any argument is null
     */
    public TypeConverterValue(Class<T> objectType, Value<?> value, ObjectMapper objectMapper) {
        if (objectType == null) {
            throw new ValueException("Object type cannot be null.");
        }
        if (value == null) {
            throw new ValueException("Value cannot be null.");
        }
        if (objectMapper == null) {
            throw new ValueException("Object mapper cannot be null.");
        }
        this.objectType = objectType;
        this.value = value;
        this.objectMapper = objectMapper;
    }

    /*
    Copy constructor
     */
    private TypeConverterValue(TypeConverterValue<T> source) {
        super(source);
        this.objectType = source.objectType;
        this.value = source.value.getClone();
        this.objectMapper = source.objectMapper;
    }

    @Override
    protected TypeConverterValue<T> clone() {
        return new TypeConverterValue<>(this);
    }

    @Override
    public void reset() {
        super.reset();
        value.reset();
    }

    @Override
    protected void eval() {
        Object object = value.get();
        val = objectMapper.convertValue(object, objectType);
    }
}
