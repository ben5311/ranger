package ranger.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Transforms value into its JSON representation.
 */
public class JsonTransformer extends Transformer<String> {

    private final Value<?> value;
    private final ObjectMapper objectMapper;

    /**
     * Constructs JSON transformer with specified <code>value</code>.
     * New default instance of {@link ObjectMapper} will be used.
     *
     * @param value Value which will be transformed into its JSON representation.
     */
    public JsonTransformer(Value<?> value) {
        this(value, new ObjectMapper());
    }

    /**
     * Constructs JSON transformer with specified <code>value</code> and <code>objectMapper</code>.
     *
     * @param value Value which will be transformed into its JSON representation.
     * @param objectMapper Object mapper which will be used to map value to JSON.
     * @throws ValueException if any argument is null or if an error occurs during JSON conversion
     */
    public JsonTransformer(Value<?> value, ObjectMapper objectMapper) {
        if (value == null) {
            throw new ValueException("Value cannot be null.");
        }
        if (objectMapper == null) {
            throw new ValueException("Object mapper cannot be null.");
        }
        this.value = value;
        this.objectMapper = objectMapper;
    }

    /*
    Copy constructor
     */
    private JsonTransformer(JsonTransformer source) {
        super(source);
        this.value = source.value.getClone();
        this.objectMapper = source.objectMapper;
    }

    @Override
    public void reset() {
        super.reset();
        value.reset();
    }

    @Override
    protected JsonTransformer clone() {
        return new JsonTransformer(this);
    }

    @Override
    protected void eval() {
        try {
            val = objectMapper.writeValueAsString(value.get());
        } catch (JsonProcessingException e) {
            throw new ValueException("Error while converting '"+value.get()+"' to json", e);
        }
    }
}
