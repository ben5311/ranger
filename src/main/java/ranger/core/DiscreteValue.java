package ranger.core;

import ranger.distribution.Distribution;
import ranger.distribution.UniformDistribution;

import java.util.ArrayList;
import java.util.List;

/**
 * Randomly selects one of the provided values following the specified distribution.
 *
 * @param <T> Type this value would evaluate to.
 */
public class DiscreteValue<T> extends Value<T> implements Switchable<T> {

    protected final List<Value<T>> values;
    protected final Distribution distribution;
    protected int currentIndex;

    /**
     * Constructs discrete value with specified <code>values</code>, <code>distribution</code> is set to Uniform
     * distribution.
     *
     * @param values List of possible values.
     * @throws ValueException if values is null or empty
     */
    public DiscreteValue(List<Value<T>> values) {
        this(values, new UniformDistribution());
    }

    /**
     * Constructs discrete value with specified <code>values</code> and <code>distribution</code>.
     *
     * @param values List of possible values.
     * @param distribution Distribution to use for value selection.
     * @throws ValueException if any argument is null or values is empty
     */
    public DiscreteValue(List<Value<T>> values, Distribution distribution) {
        if (values == null || values.isEmpty()) {
            throw new ValueException("List of values cannot be null nor empty.");
        }
        if (distribution == null) {
            throw new ValueException("Distribution cannot be null.");
        }
        this.values = new ArrayList<>(values);
        this.distribution = distribution;
        this.currentIndex = -1;
    }

    /*
    Copy constructor
     */
    protected DiscreteValue(DiscreteValue<T> source) {
        super(source);
        this.values = new ArrayList<>(source.values.size());
        source.values.forEach(v -> this.values.add(v.getClone()));
        this.distribution = source.distribution.clone();
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
        values.forEach(Value::reset);
    }

    @Override
    protected void eval() {
        Value<T> chosenValue;
        currentIndex = distribution.nextInt(values.size());
        chosenValue = values.get(currentIndex);
        val = chosenValue.get();
    }

    @Override
    protected DiscreteValue<T> clone() {
        return new DiscreteValue<>(this);
    }
}
