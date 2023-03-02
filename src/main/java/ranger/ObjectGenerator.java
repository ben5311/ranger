package ranger;


import ranger.core.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates objects of type {@code <T>}.
 *
 * @param <T> Type of objects to be generated.
 */
public class ObjectGenerator<T> {

    final Value<T> value;

    /**
     * Constructs object generator out of specified <code>value</code>.
     *
     * @param value The value.
     */
    public ObjectGenerator(Value<T> value) {
        this.value = value;
    }


    /**
     * @return Value contained in this ObjectGenerator
     */
    public Value<T> getValue() {
        return value;
    }


    /**
     * Generates list containing specified <code>numberOfObjects</code>.
     *
     * @param numberOfObjects Number of objects to be generated.
     * @return List of generated objects, or empty list, never null.
     * @throws IllegalArgumentException if numberOfObjects is not positive
     */
    public List<T> generate(int numberOfObjects) {
        if (numberOfObjects < 0) {
            throw new IllegalArgumentException(
                    "Cannot generate negative number of objects. numberOfObjects: " + numberOfObjects);
        }
        List<T> result = new ArrayList<>(numberOfObjects);
        for (int i = 0; i < numberOfObjects; i++) {
            result.add(next());
        }
        return result;
    }

    /**
     * Generates next object.
     *
     * @return An instance of {@code <T>}.
     */
    public T next() {
        T result = value.get();
        value.reset();
        return result;
    }

    /**
     * Returns new instance of ObjectGenerator generating the same type of objects
     *
     * @return new instance of this
     */
    public ObjectGenerator<T> getClone() {
        return new ObjectGenerator<>(value.getClone());
    }

}
