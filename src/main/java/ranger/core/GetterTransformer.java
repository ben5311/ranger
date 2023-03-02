package ranger.core;

import java.util.Map;

/**
 * Extracts property value for a given key from given composite value.
 *
 * @param <T> Type this value would evaluate to.
 */
public class GetterTransformer<T> extends Transformer<T> {

    private final String keyName;
    private final Class<T> valueType;
    private final Value<?> value;


    /**
     * Constructs getter transformer with specified <code>key</code> and <code>value</code>.
     *
     * @param keyName Name of property for which to attempt get.
     * @param valueType Type of property for which to attempt get.
     * @param value Value from which to attempt get.
     * @throws ValueException if any argument is null or keyName is empty
     */
    public GetterTransformer(String keyName, Class<T> valueType, Value<?> value) {
        if (keyName == null || keyName.isEmpty()) { throw new ValueException("keyName cannot be null nor empty."); }
        if (valueType == null) { throw new ValueException("valueType cannot be null."); }
        while (value instanceof ValueProxy) {
            value = ((ValueProxy<?>) value).getDelegate();
        }
        if (value == null) { throw new ValueException("value cannot be null."); }
        if (!(value instanceof Composite)) { throw new ValueException("source value must be a Value containing a Map"); }
        this.keyName = keyName;
        this.valueType = valueType;
        this.value = value;
    }

    /*
    Copy constructor
     */
    private GetterTransformer(GetterTransformer<T> source) {
        super(source);
        this.keyName = source.keyName;
        this.valueType = source.valueType;
        this.value = source.value.getClone();
    }

    @Override
    public void reset() {
        super.reset();
        value.reset();
    }

    @Override
    protected GetterTransformer<T> clone() {
        return new GetterTransformer<>(this);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected void eval() {
        Map<String, Object> v = (Map) value.get();
        val = (T) v.get(keyName);
    }
}
