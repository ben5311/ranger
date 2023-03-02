package ranger.core;

/**
 * Marker interface for all transformers.
 *
 * @param <T> Type this value would evaluate to.
 */
public abstract class Transformer<T> extends Value<T> {

    public Transformer() {

    }

    /*
    Copy constructor
     */
    protected Transformer(Transformer<T> source) {
        super(source);
    }

    @Override
    protected abstract Transformer<T> clone();

}
