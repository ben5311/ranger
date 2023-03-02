package ranger.core;


import java.text.Normalizer;

/**
 * Replaces all non ASCII characters like accents with their ASCII representation.
 */
public class AsciiTransformer extends Transformer<String>{

    private final Value<String> stringValue;

    /**
     * Constructs ASCII transformer that replaces all non ASCII characters like accents with their ASCII representation
     *
     * @param stringValue Value that returns String
     * @throws ValueException if stringValue or generated String from stringValue is null
     */
    public AsciiTransformer(Value<String> stringValue) {
        if (stringValue == null) { throw new ValueException("String value cannot be null"); }
        this.stringValue = stringValue;
    }

    /*
    Copy constructor
     */
    private AsciiTransformer(AsciiTransformer source) {
        super(source);
        this.stringValue = source.stringValue.getClone();
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
        string = string.replace("ä", "ae").replace("ö", "oe").replace("ü", "ue").replace("ß", "ss");    //replace german accents
        val = Normalizer.normalize(string, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");    //drop other accents
    }

    @Override
    protected AsciiTransformer clone() {
        return new AsciiTransformer(this);
    }
}
