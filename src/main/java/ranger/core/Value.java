package ranger.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Root of type hierarchy. It can evaluate to a value.
 *
 * @param <T> Type value would evaluate to.
 */
public abstract class Value<T> {

    /**
     * Indicates whether value is evaluated or not.
     */
    protected boolean evaluated = false;

    /**
     * The value.
     */
    protected T val;

    // For cloning
    private static final Map<Value<?>, Value<?>> clones = new HashMap<>();
    private static int openClones = 0;

    public Value() {

    }

    /*
    Copy constructor
     */
    protected Value(Value<T> source) {
        this.val = source.val;
        this.evaluated = source.evaluated;
    }

    /**
     * Returns a value depending on concrete implementation.
     *
     * @return A value depending on concrete implementation.
     */
    public T get() {
        if (!evaluated) {
            eval();
            evaluated = true;
        }
        return val;
    }

    /**
     * Enforces reevaluation of value for next {@link #get()} invocation.
     */
    public void reset() {
        evaluated = false;
    }

    /**
     * Evaluates {@link #val} variable.
     */
    protected void eval() {
    }

    /**
     * @return a new instance of Value that generates same type of object.
     */
    @SuppressWarnings("unchecked")
    public Value<T> getClone() {
        openClones++;
        if (!clones.containsKey(this)) {
            // Put all cloned values into a HashMap to ensure that each value is cloned only once across value hierarchy
            clones.put(this, this.clone());
        }
        Value<T> clone = (Value<T>) clones.get(this);
        openClones--;
        if (openClones == 0) {
            clones.clear();
        }
        return clone;
    }

    protected abstract Value<T> clone();
}
