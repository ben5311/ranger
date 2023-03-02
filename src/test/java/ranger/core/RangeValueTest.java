package ranger.core;

import org.junit.jupiter.api.Test;
import ranger.distribution.UniformDistribution;

import static org.junit.jupiter.api.Assertions.assertThrows;

class RangeValueTest {

    @Test
    void testErrorConstructWithNullArgument() {
        assertThrows(ValueException.class, () -> new RangeValueInt(null, false));
        assertThrows(ValueException.class, () -> new RangeValueInt(null, false, null));
        assertThrows(ValueException.class, () -> new RangeValueInt(null, false, new UniformDistribution()));
        assertThrows(ValueException.class, () -> new RangeValueInt(new Range<>(1, 10), false, null));
        assertThrows(InvalidRangeBoundsException.class, () -> new RangeValueInt(new Range<>(10, 1), false));     //decreasing range
        assertThrows(InvalidRangeBoundsException.class, () -> new RangeValueInt(new Range<>(1, 1), false));     //empty range
    }

}