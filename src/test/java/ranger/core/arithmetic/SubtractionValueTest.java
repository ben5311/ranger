package ranger.core.arithmetic;

import org.junit.jupiter.api.Test;
import ranger.core.ConstantValue;
import ranger.core.ValueException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class SubtractionValueTest {

    @Test
    void testErrorConstructWithNullArgument() {
        assertThrows(ValueException.class, () -> new SubtractionValueInteger(null, null));
        assertThrows(ValueException.class, () -> new SubtractionValueInteger(null, ConstantValue.of(1)));
        assertThrows(ValueException.class, () -> new SubtractionValueInteger(ConstantValue.of(1), null));
    }
    
}