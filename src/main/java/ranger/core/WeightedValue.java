package ranger.core;

import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Randomly selects one of the provided values following the provided weights.
 *
 * @param <T> Type this value would evaluate to.
 */
public class WeightedValue<T> extends Value<T> implements Switchable<T> {

    private final List<Pair<Integer, Double>> weightList;
    private final List<Value<T>> values;
    private final EnumeratedDistribution<Integer> enumeratedDistribution;
    private int currentIndex;

    /**
     * Constructs discrete weighted value with specified <code>values</code> and <code>weights</code>.
     *
     * @param weightedValuePairs List of values with their corresponding weights.
     * @throws ValueException if weightedValuePairs is null or empty
     */
    public WeightedValue(List<WeightedValuePair<T>> weightedValuePairs) {
        if (weightedValuePairs == null || weightedValuePairs.isEmpty()) {
            throw new ValueException("List of weighted values cannot be null nor empty.");
        }
        this.weightList = IntStream.range(0, weightedValuePairs.size()).mapToObj(i -> new Pair<>(i, weightedValuePairs.get(i).getWeight())).collect(Collectors.toList());  //list containing Index-Weight-Pairs
        this.values = weightedValuePairs.stream().map(WeightedValuePair::getValue).collect(Collectors.toList());    //list containing corresponding values
        this.enumeratedDistribution = new EnumeratedDistribution<>(weightList);
        this.currentIndex = -1;
    }

    /*
    Copy constructor
     */
    private WeightedValue(WeightedValue<T> source) {
        super(source);
        this.weightList = source.weightList;
        this.values = new ArrayList<>(source.values.size());
        source.values.forEach(v -> this.values.add(v.getClone()));
        this.enumeratedDistribution = new EnumeratedDistribution<>(this.weightList);
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
        currentIndex = enumeratedDistribution.sample();
        Value<T> chosenValue = values.get(currentIndex);
        val = chosenValue.get();
    }

    @Override
    protected WeightedValue<T> clone() {
        return new WeightedValue<>(this);
    }


    /**
     * Represents value with its weight.
     *
     * @param <T> Type which value will return.
     */
    public static class WeightedValuePair<T> {
        private final Value<T> value;
        private final double weight;

        /**
         * Constructs weighted value pair with specified <code>value</code> and <code>weight</code>.
         *
         * @param value The value.
         * @param weight Weight of the value.
         * @throws ValueException if value is null or weight is not positive
         */
        public WeightedValuePair(Value<T> value, double weight) {
            if (value == null) {
                throw new ValueException("Value cannot be null.");
            }
            if (weight <= 0) {
                throw new ValueException("Weight must be greater than 0.");
            }
            this.value = value;
            this.weight = weight;
        }

        /**
         * Returns the value.
         *
         * @return The value.
         */
        public Value<T> getValue() {
            return value;
        }

        /**
         * Returns weight of the value.
         *
         * @return weight of the value.
         */
        public double getWeight() {
            return weight;
        }

    }

}
