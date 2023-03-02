package ranger;

import com.fasterxml.jackson.databind.ObjectMapper;
import ranger.core.CompositeValue;
import ranger.core.ConstantValue;
import ranger.core.TypeConverterValue;
import ranger.core.Value;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Builder for {@link ObjectGenerator}.
 */
public class ObjectGeneratorBuilder {

    private final Map<String, Value<?>> propertyValues;

    /**
     * Constructs {@link ObjectGeneratorBuilder}.
     */
    public ObjectGeneratorBuilder() {
        this.propertyValues = new LinkedHashMap<>();
    }

    /**
     * Sets the ObjectGenerator to be used for generating values for property.
     *
     * @param property Name of the property.
     * @param value Value to be used for generating values.
     * @return This builder.
     */
    public ObjectGeneratorBuilder prop(String property, ObjectGenerator<?> value) {
        propertyValues.put(property, value.value);
        return this;
    }

    /**
     * Sets a constant value to be used for property.
     *
     * @param property Name of the property.
     * @param constantValue constant value to be used for generating values.
     * @return This builder.
     */
    public ObjectGeneratorBuilder prop(String property, Object constantValue) {
        propertyValues.put(property, ConstantValue.of(constantValue));
        return this;
    }

    /**
     * Builds {@link ObjectGenerator} based on current builder configuration. Resulting {@link ObjectGenerator} will
     * have {@code Map<String, Object>} as return type.
     *
     * @return Instance of {@link ObjectGenerator}.
     */
    public ObjectGenerator<Map<String, Object>> build() {
        return new ObjectGenerator<>(new CompositeValue(propertyValues));
    }

    /**
     * Builds {@link ObjectGenerator} based on current builder configuration. Resulting {@link ObjectGenerator} will try
     * to convert configured output to specified <code>objectType</code>.
     *
     * @param objectType Type of object to which conversion will be attempted.
     * @param <T> Type of object {@link ObjectGenerator} will generate.
     * @return Instance of {@link ObjectGenerator}.
     */
    public <T> ObjectGenerator<T> build(Class<T> objectType) {
        return build(objectType, new ObjectMapper());
    }

    /**
     * Builds {@link ObjectGenerator} based on current builder configuration. Resulting {@link ObjectGenerator} will try
     * to convert configured output to specified <code>objectType</code>.
     *
     * @param objectType Type of object to which conversion will be attempted.
     * @param objectMapper Object mapper which will be used for conversion.
     * @param <T> Type of object {@link ObjectGenerator} will generate.
     * @return Instance of {@link ObjectGenerator}.
     * @throws IllegalArgumentException if objectType is null
     */
    public <T> ObjectGenerator<T> build(Class<T> objectType, ObjectMapper objectMapper) {
        if (objectType == null) {
            throw new IllegalArgumentException("objectType cannot be null.");
        }
        CompositeValue compositeValue = new CompositeValue(propertyValues);
        return new ObjectGenerator<>(new TypeConverterValue<>(objectType, compositeValue, objectMapper));
    }

}
