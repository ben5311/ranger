package ranger.core.arithmetic;

import org.junit.jupiter.api.Test;
import ranger.core.ConstantValue;
import ranger.core.ValueException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class AdditionValueTest {

    @Test
    void testErrorConstructWithNullArgument() {
        assertThrows(ValueException.class, () -> new AdditionValueInteger(null, null));
        assertThrows(ValueException.class, () -> new AdditionValueInteger(null, ConstantValue.of(1)));
        assertThrows(ValueException.class, () -> new AdditionValueInteger(ConstantValue.of(1), null));
    }

}