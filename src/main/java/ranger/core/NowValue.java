package ranger.core;

/**
 * Generates current epoch milliseconds.
 */
public class NowValue extends Value<Long> {

    @Override
    public void eval() {
        val = System.currentTimeMillis();
    }

    @Override
    protected NowValue clone() {
        return this;
    }
}
