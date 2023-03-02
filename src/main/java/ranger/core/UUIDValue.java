package ranger.core;

import java.util.UUID;

/**
 * Generates random UUID.
 */
public class UUIDValue extends Value<String> {

    public UUIDValue() {}

    /*
    Copy constructor
     */
    private UUIDValue(UUIDValue source) {
        super(source);
    }

    @Override
    protected Value<String> clone() {
        return new UUIDValue(this);
    }

    @Override
    public void eval() {
        val = UUID.randomUUID().toString();
    }
}
