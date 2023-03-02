package ranger.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CircularRangeValueTest {

    @Test
    void testErrorConstructWithWrongArguments() {
        assertThrows(ValueException.class, () -> new CircularRangeValueInt(null, null));
        assertThrows(ValueException.class, () -> new CircularRangeValueInt(null, 1));
        assertThrows(ValueException.class, () -> new CircularRangeValueInt(new Range<>(-10, 10), null));
        assertThrows(ValueException.class, () -> new CircularRangeValueInt(new Range<>(-10, 10), 0));     //0 increment
        assertThrows(ValueException.class, () -> new CircularRangeValueInt(new Range<>(-10, 10), -1));    //negative increment with increasing range
        assertThrows(ValueException.class, () -> new CircularRangeValueInt(new Range<>(10, -10), 1));     //positive increment with decreasing range
        assertThrows(ValueException.class, () -> new CircularRangeValueInt(new Range<>(-10, 10), 21));    //increment is greater than range size
    }

}