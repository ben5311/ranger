package ranger.core.arithmetic;

import org.junit.jupiter.api.Test;
import ranger.core.ConstantValue;
import ranger.core.ValueException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class MultiplicationValueTest {

    @Test
    void testErrorConstructWithNullArgument() {
        assertThrows(ValueException.class, () -> new MultiplicationValueInteger(null, null));
        assertThrows(ValueException.class, () -> new MultiplicationValueInteger(null, ConstantValue.of(1)));
        assertThrows(ValueException.class, () -> new MultiplicationValueInteger(ConstantValue.of(1), null));
    }
    
}