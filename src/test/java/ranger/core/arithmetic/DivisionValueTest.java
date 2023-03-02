package ranger.core.arithmetic;

import org.junit.jupiter.api.Test;
import ranger.core.ConstantValue;
import ranger.core.ValueException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class DivisionValueTest {

    @Test
    void testErrorConstructWithNullArgument() {
        assertThrows(ValueException.class, () -> new DivisionValueInteger(null, null));
        assertThrows(ValueException.class, () -> new DivisionValueInteger(null, ConstantValue.of(1)));
        assertThrows(ValueException.class, () -> new DivisionValueInteger(ConstantValue.of(1), null));
    }
    
}