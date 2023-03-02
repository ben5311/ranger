package ranger.core;


/**
 * Creates a String with specified case from source String.
 */
public class CaseTransformer extends Transformer<String>{

    private final Value<String> stringValue;
    private final boolean toUpperCase;

    /**
     * Constructs case transformer that changes the case of the String retrieved from stringValue
     *
     * @param stringValue Value that returns String
     * @param toUpperCase true if String should be changed to upper case, false if to lower case
     * @throws ValueException if stringValue or generated String from stringValue is null
     */
    public CaseTransformer(Value<String> stringValue, boolean toUpperCase) {
        if (stringValue == null) { throw new ValueException("String value cannot be null"); }
        this.stringValue = stringValue;
        this.toUpperCase = toUpperCase;
    }

    /*
    Copy constructor
     */
    private CaseTransformer(CaseTransformer source) {
        super(source);
        this.stringValue = source.stringValue.getClone();
        this.toUpperCase = source.toUpperCase;
    }

    @Override
    public void reset() {
        super.reset();
        stringValue.reset();
    }

    @Override
    protected void eval() {
        String string = stringValue.get();
        if (string == null) { throw new ValueException("string from string value cannot be null"); }
        val = toUpperCase ? string.toUpperCase() : string.toLowerCase();
    }

    @Override
    protected CaseTransformer clone() {
        return new CaseTransformer(this);
    }
}
