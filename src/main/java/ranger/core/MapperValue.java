package ranger.core;

import java.util.HashMap;
import java.util.List;

/**
 * MapperValue selects from Values dependently on a source value.
 * It retrieves source's Value and then picks the KeyValuePair with matching key from keyValuePairs and evaluates to it's Value.
 */
public class MapperValue<T> extends Value<T> {

    private final Value<?> source;
    private final HashMap<Object, Value<T>> map;
    private static final String DEFAULT_KEY = "default";

    /**
     * Constructs MapperValue that selects from keyValuePairs dependently on a source Value.
     * It retrieves source's Value and then picks the KeyValuePair with matching key from keyValuePairs and evaluates to it's Value.
     * @param source the source Value the MapperValue depends on
     * @param keyValuePairs the List of KeyValuePairs that define in which case which Value has to be chosen
     * @throws ValueException if source generates a null value or a value not defined in keyValuePairs (and DEFAULT_KEY is not defined)
     * @throws ValueException if any argument is null
     */
    public MapperValue(Value<?> source, List<KeyValuePair<T>> keyValuePairs) {
        if (source == null || keyValuePairs == null || keyValuePairs.isEmpty()) { throw new ValueException("source must not be null and keyValuePairs must not be null nor empty"); }
        this.source = source;
        map = new HashMap<>(keyValuePairs.size());
        for (KeyValuePair<T> pair : keyValuePairs) {
            map.put(pair.key, pair.value);
        }

    }

    /*
    Copy constructor
     */
    private MapperValue(MapperValue<T> source) {
        super(source);
        this.source = source.source.getClone();
        this.map = new HashMap<>(source.map.size());
        source.map.forEach((k, v) -> this.map.put(k, v.getClone()));
    }

    @Override
    public void eval() {
        Object nextKey = source.get();
        if (nextKey == null) { throw new ValueException("map()'s source value must not be null"); }
        Value<T> nextValue;
        if (map.containsKey(nextKey)) {
            nextValue = map.get(nextKey);
        } else {
            if (!map.containsKey(DEFAULT_KEY)) { throw new ValueException(String.format("map()'s source value evaluated to '%s' but you did not define this key nor the '%s' key", nextKey, DEFAULT_KEY)); }
            nextValue = map.get(DEFAULT_KEY);
        }
        val = nextValue.get();
    }

    @Override
    public void reset() {
        source.reset();
        map.values().forEach(Value::reset);
        super.reset();
    }

    @Override
    protected MapperValue<T> clone() {
        return new MapperValue<>(this);
    }

    /**
     * KeyValuePair for use with MapperValue
     */
    public static class KeyValuePair<T> {
        private final Object key;
        private final Value<T> value;

        /**
         * Constructs KeyValuePair with constant key and dynamic value
         * @param key the constant key
         * @param value the constant or dynamic value
         * @throws ValueException if any argument is null or key is not a constant value
         */
        public KeyValuePair(Value<?> key, Value<T> value) {
            if (key == null || value == null) { throw new ValueException("key and value must not be null"); }
            if (!(key instanceof ConstantValue)) { throw new ValueException("key must be a constant value"); }
            this.key = key.get();
            this.value = value;
        }
    }

}
