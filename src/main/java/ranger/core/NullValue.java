package ranger.core;

/**
 * Value that always returns null.
 */
public class NullValue extends Value<Object> {

    @Override
    protected NullValue clone() {
        return this;
    }
}
