package ranger.core;

/**
 * Indicates that an error occurred during creation of Value or during generation of Value's next value
 */
public class ValueException extends IllegalArgumentException {
    public ValueException() {
    }

    public ValueException(String s) {
        super(s);
    }

    public ValueException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValueException(Throwable cause) {
        super(cause);
    }
}
