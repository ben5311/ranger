package ranger.core;

import ranger.distribution.Distribution;
import ranger.distribution.UniformDistribution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Generates random strings of specified <code>length</code> and from specified character ranges.
 */
public class RandomContentStringValue extends Value<String> {

    private static final List<Range<Character>> DEFAULT_RANGES = Arrays.asList(new Range<>('a', 'z'),
            new Range<>('A', 'Z'), new Range<>('0', '9'));

    private final Value<Integer> lengthValue;
    private final List<Character> possibleCharacters;
    private final Distribution distribution;

    /**
     * Constructs random content string value with specified <code>lengthValue</code> and default character range.
     *
     * @param lengthValue Value that returns integer which represents length of generated string. It should never
     *            generate length that is less than 1.
     * @throws ValueException if lengthValue is null
     */
    public RandomContentStringValue(Value<Integer> lengthValue) {
        this(lengthValue, DEFAULT_RANGES);
    }

    /**
     * Constructs random content string value with specified <code>lengthValue</code> and specified
     * <code>charRanges</code>.
     *
     * @param lengthValue Value that returns integer which represents length of generated string. It should never
     *            generate length that is less than 1.
     * @param charRanges Ranges of characters from which string will be constructed.
     * @throws ValueException if any argument null or a range in charRanges is decreasing
     */
    public RandomContentStringValue(Value<Integer> lengthValue, List<Range<Character>> charRanges) {
        if (lengthValue == null) {
            throw new ValueException("lengthValue cannot be null.");
        }
        if (charRanges == null || charRanges.isEmpty()) {
            throw new ValueException("charRanges cannot be null nor empty.");
        }
        this.lengthValue = lengthValue;
        Set<Character> chars = new HashSet<>();
        for (Range<Character> range : charRanges) {
            if (range.isDecreasing()) {
                throw new ValueException("All ranges must be increasing.");
            }
            for (Character c = range.getBeginning(); c <= range.getEnd(); c++) {
                chars.add(c);
            }
        }
        possibleCharacters = new ArrayList<>(chars);
        distribution = new UniformDistribution();
    }

    /*
    Copy constructor
     */
    private RandomContentStringValue(RandomContentStringValue source) {
        super(source);
        this.lengthValue = source.lengthValue.getClone();
        this.possibleCharacters = source.possibleCharacters;
        this.distribution = source.distribution.clone();
    }

    @Override
    public void reset() {
        super.reset();
        lengthValue.reset();
    }

    @Override
    protected RandomContentStringValue clone() {
        return new RandomContentStringValue(this);
    }

    @Override
    protected void eval() {
        int length = lengthValue.get();
        if (length < 1) {
            throw new ValueException("Generated length cannot be less than 1, but was: " + length);
        }
        char[] chars = new char[length];
        for (int i = 0; i < length; i++) {
            chars[i] = possibleCharacters.get(distribution.nextInt(possibleCharacters.size()));
        }
        val = new String(chars);
    }
}
