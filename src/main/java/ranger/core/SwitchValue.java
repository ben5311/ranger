package ranger.core;


import java.util.List;

/**
 * Selects a Value of it's list that relates to source's randomly selected value.
 *
 */
public class SwitchValue<T> extends DiscreteValue<T> {

    private final Switchable<?> source;

    /**
     * Constructs SwitchValue that selects a Value of it's list dependently on switchableSource's value
     * @param switchableSource the source Value this SwitchValue should depend on
     * @param values the List of Values to select from
     * @throws ValueException if switchableSource is null or value's size differs from switchableSource's size
     */
    public SwitchValue(Switchable<?> switchableSource, List<Value<T>> values) {
        super(values);
        if (switchableSource == null) { throw new ValueException("source for switch() must not be null"); }
        this.source = switchableSource;
        if (values.size() != source.getSize()) {
            throw new ValueException(String.format("Count of switch arguments must be equal to source's list's size. Count of source values: '%d' vs count of switch arguments: '%d'", source.getSize(), values.size()));
        }
    }

    /*
    Copy constructor
     */
    private SwitchValue(SwitchValue<T> source) {
        super(source);
        this.source = (Switchable<?>) source.source.getClone();
    }

    @Override
    protected SwitchValue<T> clone() {
        return new SwitchValue<>(this);
    }

    @Override
    protected void eval() {
        source.get();   //ensure that source is evaluated before retrieving index
        currentIndex = source.getIndex();
        if (currentIndex < 0 || currentIndex >= source.getSize()) { throw new ArrayIndexOutOfBoundsException(String.format("Switchable %s returned index %d which is out of bounds for length %d", source.getClass().getName(), currentIndex, source.getSize())); }
        Value<T> chosenValue = values.get(currentIndex);
        val = chosenValue.get();
    }

    @Override
    public void reset() {
        source.reset();
        super.reset();
    }

}
