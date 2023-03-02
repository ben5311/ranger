package ranger.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Returns values in order specified within <code>values</code> parameter. When end is reached, it starts over from the
 * beginning.
 *
 * @param <T> Type this value would evaluate to.
 */
public class CircularValue<T> extends Value<T> implements Switchable<T> {

    private final List<Value<T>> values;
    private int currentIndex;

    /**
     * Constructs circular value with specified <code>values</code>.
     *
     * @param values List of possible values.
     * @throws ValueException if values is null
     */
    public CircularValue(List<Value<T>> values) {
        if (values == null || values.isEmpty()) {
            throw new ValueException("List of values cannot be null nor empty.");
        }
        this.values = new ArrayList<>(values);
        this.currentIndex = -1;
    }

    /*
    Copy constructor
     */
    private CircularValue(CircularValue<T> source) {
        super(source);
        this.values = new ArrayList<>(source.values.size());
        source.values.forEach(v -> this.values.add(v.getClone()));
        this.currentIndex = source.currentIndex;
    }

    public int getIndex() {
        return currentIndex;
    }

    public int getSize() {
        return values.size();
    }

    @Override
    public void reset() {
        super.reset();
        values.get(nextIndex()).reset();
    }

    @Override
    protected CircularValue<T> clone() {
        return new CircularValue<>(this);
    }

    @Override
    protected void eval() {
        currentIndex = nextIndex();
        val = values.get(currentIndex).get();
    }

    private int nextIndex() {
        if (currentIndex == values.size()-1) {
            return 0;
        } else {
            return currentIndex + 1;
        }
    }
}
