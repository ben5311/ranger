package ranger.core;

import ranger.distribution.Distribution;
import ranger.distribution.UniformDistribution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Generates random length list out of specified values.
 *
 * @param <T> Type which evaluated list will contain.
 */
public class RandomLengthListValue<T> extends Value<List<T>> {

    private final int minLength;
    private final int maxLength;
    private final Value<T> elementGenerator;
    private final Distribution distribution;

    /**
     * Constructs random length list value out of specified values.
     *
     * @param minLength Minimum list length
     * @param maxLength Maximum list length
     * @param elementGenerator Element generator
     * @throws ValueException if elementGenerator is null
     */
    public RandomLengthListValue(int minLength, int maxLength, Value<T> elementGenerator) {
        this(minLength, maxLength, elementGenerator, new UniformDistribution());
    }

    /**
     * Constructs random length list value out of specified values.
     *
     * @param minLength Minimum list length (inclusive)
     * @param maxLength Maximum list length (exclusive)
     * @param elementGenerator Element generator
     * @param distribution Distribution to use for number of items in list.
     * @throws ValueException if elementGenerator or distribution is null
     */
    public RandomLengthListValue(int minLength, int maxLength, Value<T> elementGenerator, Distribution distribution) {
        if (minLength < 0 || maxLength < 1) { throw new ValueException("minLength must be at least 0 and maxLength at least 1"); }
        if (maxLength <= minLength) { throw new ValueException("maxLength must be greater than minLength"); }
        if (elementGenerator == null) { throw new ValueException("elementGenerator cannot be null."); }
        if (distribution == null) { throw new ValueException("distribution must not be null"); }
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.elementGenerator = elementGenerator;
        this.distribution = distribution;
    }

    /*
    Copy constructor
     */
    private RandomLengthListValue(RandomLengthListValue<T> source) {
        super(source);
        this.minLength = source.minLength;
        this.maxLength = source.maxLength;
        this.elementGenerator = source.elementGenerator.getClone();
        this.distribution = source.distribution.clone();
    }

    @Override
    protected RandomLengthListValue<T> clone() {
        return new RandomLengthListValue<>(this);
    }

    @Override
    protected void eval() {
        int randomLength = distribution.nextInt(minLength, maxLength);
        List<T> result = new ArrayList<>();
        for (int i = 0; i < randomLength; i++) {
            result.add(elementGenerator.get());
            elementGenerator.reset();
        }
        val = Collections.unmodifiableList(result);
    }
}
