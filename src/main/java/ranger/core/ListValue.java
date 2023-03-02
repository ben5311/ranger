package ranger.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Generates list out of specified values.
 *
 * @param <T> Type which evaluated list will contain.
 */
public class ListValue<T> extends Value<List<T>> {

    private final List<Value<T>> values;

    /**
     * Constructs list value out of specified values.
     *
     * @param values Values which will constitute list.
     * @throws ValueException if values is null or empty
     */
    public ListValue(List<Value<T>> values) {
        if (values == null || values.isEmpty()) {
            throw new ValueException("List of values cannot be null nor empty.");
        }
        this.values = new ArrayList<>(values);
    }

    /*
    Copy constructor
     */
    private ListValue(ListValue<T> source) {
        super(source);
        this.values = new ArrayList<>(source.values.size());
        source.values.forEach(v -> this.values.add(v.getClone()));
    }

    @Override
    public void reset() {
        super.reset();
        for (Value<T> value : values) {
            value.reset();
        }
    }

    @Override
    protected void eval() {
        List<T> result = new ArrayList<>(values.size());
        for (Value<T> value : values) {
            result.add(value.get());
        }
        val = Collections.unmodifiableList(result);
    }

    @Override
    protected ListValue<T> clone() {
        return new ListValue<>(this);
    }
}
