package ranger.core;

import ranger.distribution.UniformDistribution;

import java.util.ArrayList;
import java.util.List;

/**
 * Randomly selects one of the provided values using the provided counts as weights and as limit.
 *
 * @param <T> Type this value would evaluate to.
 */
public class ExactWeightedValue<T> extends Value<T> implements Switchable<T> {

    private final UniformDistribution distribution;
    private final List<Long> counts;
    private final List<Long> currentNumberOfValues;
    private final List<Value<T>> values;
    private final int totalCount;
    private int currentTotalCount;
    private int currentIndex;

    /**
     * Constructs discrete weighted value with specified <code>values</code> and <code>weights</code>.
     *
     * @param countValuePairs List of values with their corresponding weights.
     * @throws ExactWeightedValueDepletedException if you try to generate more values than sum of all counts in countValuePairs
     * @throws ValueException if countValuePairs is null or empty
     */
    public ExactWeightedValue(List<CountValuePair<T>> countValuePairs) {
        if (countValuePairs == null || countValuePairs.isEmpty()) {
            throw new ValueException("List of count value pairs cannot be null nor empty.");
        }
        this.distribution = new UniformDistribution();
        this.counts = new ArrayList<>();
        this.currentNumberOfValues = new ArrayList<>();
        this.values = new ArrayList<>();
        this.currentTotalCount = 0;
        for (CountValuePair<T> pair : countValuePairs) {
            currentTotalCount += pair.getCount();
            counts.add(pair.getCount());
            currentNumberOfValues.add(0L);
            values.add(pair.getValue());
        }
        this.totalCount = currentTotalCount;
        this.currentIndex = -1;
    }

    /*
    Copy constructor
     */
    private ExactWeightedValue(ExactWeightedValue<T> source) {
        super(source);
        this.distribution = source.distribution.clone();
        this.counts = new ArrayList<>(source.counts);
        this.currentNumberOfValues = new ArrayList<>(source.currentNumberOfValues);
        this.values = new ArrayList<>(source.values.size());
        source.values.forEach(v -> this.values.add(v.getClone()));
        this.totalCount = source.totalCount;
        this.currentTotalCount = source.currentTotalCount;
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
    protected ExactWeightedValue<T> clone() {
        return new ExactWeightedValue<>(this);
    }

    @Override
    protected void eval() {
        currentIndex = sample();
        val = values.get(currentIndex).get();
        currentNumberOfValues.set(currentIndex, currentNumberOfValues.get(currentIndex) + 1L);
        if (currentNumberOfValues.get(currentIndex).equals(counts.get(currentIndex))) {
            removeValue(currentIndex);
        }
    }

    private int sample() {
        if (values.isEmpty()) {
            throw new ExactWeightedValueDepletedException("Exact weighted value depleted."
                    + " It is configured to generate " + totalCount + " elements in total.");
        }
        int randomValue = distribution.nextInt(currentTotalCount);
        int sum = 0;
        for (int i = 0; i < counts.size(); i++) {
            sum += counts.get(i);
            if (randomValue < sum) {
                return i;
            }
        }
        throw new RuntimeException("If you see this exception. There is a bug in " + getClass().getName() + " class.");
    }

    private void removeValue(int index) {
        values.remove(index);
        currentNumberOfValues.remove(index);
        currentTotalCount -= counts.get(index);
        counts.remove(index);
    }

    /**
     * Represents value with its weight.
     *
     * @param <T> Type which value will return.
     */
    public static class CountValuePair<T> {

        private final Value<T> value;
        private final long count;

        /**
         * Constructs weighted value pair with specified <code>value</code> and <code>weight</code>.
         *
         * @param value The value.
         * @param count Weight of the value.
         * @throws ValueException if value is null or weight is not positive
         */
        public CountValuePair(Value<T> value, long count) {
            if (value == null) {
                throw new ValueException("Value cannot be null.");
            }
            if (count <= 0) {
                throw new ValueException("Weight must be greater than 0.");
            }
            this.value = value;
            this.count = count;
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
        public long getCount() {
            return count;
        }

    }

    /**
     * Indicates that all configured values are already generated specified number of times.
     */
    public static class ExactWeightedValueDepletedException extends RuntimeException {

        private static final long serialVersionUID = -5000331731200682045L;

        /**
         * Constructs {@link ExactWeightedValueDepletedException}.
         *
         * @param message The detail message.
         */
        public ExactWeightedValueDepletedException(String message) {
            super(message);
        }
    }
}
