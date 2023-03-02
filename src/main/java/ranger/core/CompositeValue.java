package ranger.core;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Composite Value containing child Values. <br>
 */
public class CompositeValue extends Composite<Object> {

    /**
     * Constructs composite value with specified initial child values.
     *
     * @param values Initial child values.
     * @throws ValueException if values is null
     */
    public CompositeValue(Map<String, Value<?>> values) {
        if (values == null) { throw new ValueException("values must not be null"); }
        this.values.putAll(values);
    }

    /**
     * Merges two or more Composites to one. The resulting CompositeValue has all elements under the same root.
     *
     * @param composites {@code List} containing all {@code Composite} objects to merge
     * @throws ValueException if compositeValues is null or empty
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public CompositeValue(List<Composite<?>> composites) {
        if (composites == null || composites.isEmpty()) { throw new ValueException("Cannot merge with no specified objects to merge."); }
        for (Composite composite : composites) {
            this.values.putAll(composite.values);
        }
    }

    /*
    Copy constructor
     */
    private CompositeValue(CompositeValue source) {
        super(source);
    }

    @Override
    protected CompositeValue clone() {
        return new CompositeValue(this);
    }

    @Override
    protected void eval() {
        LinkedHashMap<String, Object> evaluatedValues = new LinkedHashMap<>(values.size());
        values.forEach((name, value) -> evaluatedValues.put(name, value.get()));
        val = Collections.unmodifiableMap(evaluatedValues);
    }

}
