package ranger.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ValueProxyTest {

    @Test
    void testErrorSetDelegateToNull() {
        ValueProxy<?> valueProxy = new ValueProxy<>();
        assertThrows(ValueException.class, () -> valueProxy.setDelegate(null));
    }

    @Test
    void testErrorSetDelegateTwice() {
        ValueProxy<String> valueProxy = new ValueProxy<>();
        valueProxy.setDelegate(ConstantValue.of("value"));
        assertThrows(ValueException.class, () -> valueProxy.setDelegate(ConstantValue.of("another value")));
    }
}