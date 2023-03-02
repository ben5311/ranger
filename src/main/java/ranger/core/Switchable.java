package ranger.core;

/**
 * Interface for Values holding a list to use with switch()-function
 */
public interface Switchable<T> {

    T get();

    void reset();

    int getIndex();

    int getSize();

    Value<T> getClone();

}
