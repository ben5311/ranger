package ranger.core;

/**
 * Proxy around value that can cache value and can reset cache.
 *
 * @param <T> Type this value would evaluate to.
 */
public class ValueProxy<T> extends Value<T> {

    private Value<T> delegate;
    private boolean containsDelegate = false;

    /**
     * Constructs proxy without delegate.
     */
    public ValueProxy() {
    }

    /**
     * Constructs proxy with specified <code>delegate</code>.
     *
     * @param delegate Value which will be evaluated and cached.
     * @throws ValueException if delegate is null
     */
    public ValueProxy(Value<T> delegate) {
        setDelegate(delegate);
    }

    /*
    Copy constructor
     */
    private ValueProxy(ValueProxy<T> source) {
        super(source);
        this.delegate = source.containsDelegate ? source.delegate.getClone() : null;
        this.containsDelegate = source.containsDelegate;
    }

    /**
     * Sets value to this proxy.
     *
     * @param delegate Value which will be evaluated and cached.
     * @throws ValueException if you try to set delegate to null or to overwrite existing delegate
     */
    public void setDelegate(Value<T> delegate) {
        if (containsDelegate) { throw new ValueException("Proxy already contains a delegate that cannot be changed."); }
        if (delegate == null) { throw new ValueException("Delegate cannot be null."); }
        this.delegate = delegate;
        containsDelegate = true;
    }

    public Value<T> getDelegate() {
        return delegate;
    }

    @Override
    public void reset() {
        super.reset();
        checkDelegate();
        delegate.reset();
    }

    @Override
    protected ValueProxy<T> clone() {
        return new ValueProxy<>(this);
    }

    @Override
    protected void eval() {
        checkDelegate();
        val = delegate.get();
    }

    private void checkDelegate() {
        if (delegate == null) {
            throw new DelegateNotSetException();
        }
    }

    /**
     * Signals that delegate is not set.
     */
    public static class DelegateNotSetException extends RuntimeException {

        private static final long serialVersionUID = 6257779717961934851L;

        /**
         * Constructs {@link DelegateNotSetException} with default message.
         */
        public DelegateNotSetException() {
            super("Delegate not set for ValueProxy.");
        }
    }
}
