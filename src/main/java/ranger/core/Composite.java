package ranger.core;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Abstract Composite Value serving as common interface of CompositeValue and CsvReaderValue
 * as they both contain sub elements. <br>
 * V is the type of values contained within Composite.
 */
public abstract class Composite<V> extends Value<Map<String, V>> {

    protected final LinkedHashMap<String, Value<?>> values = new LinkedHashMap<>();

    /**
     * Constructs composite
     *
     */
    public Composite() {
    }

    /*
    Copy constructor
     */
    protected Composite(Composite<V> source) {
        super(source);
        source.values.forEach((k, v) -> this.values.put(k, v.getClone()));
    }

    @Override
    public void reset() {
        super.reset();
        values.values().forEach(Value::reset);
    }

    public Map<String, Value<?>> getValues() {
        return Collections.unmodifiableMap(values);
    }

    @Override
    protected abstract Composite<V> clone();

    @Override
    protected abstract void eval();
}
