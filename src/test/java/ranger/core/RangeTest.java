package ranger.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RangeTest {

    @Test
    void testIsEmpty() {
        Range<Integer> emptyRange = new Range<>(1, 1);
        Range<Integer> range = new Range<>(1, 2);
        assertTrue(emptyRange.isEmpty());
        assertFalse(range.isEmpty());
    }

    @Test
    void testIsIncreasing() {
        Range<Integer> increasingRange = new Range<>(1, 2);
        Range<Integer> decreasingRange = new Range<>(2, 1);
        Range<Integer> emptyRange = new Range<>(1, 1);
        assertTrue(increasingRange.isIncreasing());
        assertFalse(decreasingRange.isIncreasing());
        assertFalse(emptyRange.isIncreasing());
    }

    @Test
    void testIsDecreasing() {
        Range<Integer> increasingRange = new Range<>(1, 2);
        Range<Integer> decreasingRange = new Range<>(2, 1);
        Range<Integer> emptyRange = new Range<>(1, 1);
        assertFalse(increasingRange.isDecreasing());
        assertTrue(decreasingRange.isDecreasing());
        assertFalse(emptyRange.isDecreasing());
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    void testErrorConstructWithNullArgument() {
        assertThrows(InvalidRangeBoundsException.class, () -> new Range<>(null, null));
        assertThrows(InvalidRangeBoundsException.class, () -> new Range<>(1, null));
        assertThrows(InvalidRangeBoundsException.class, () -> new Range<>(null, 1));
        assertThrows(InvalidRangeBoundsException.class, () -> new Range(1, "zwei"));     //type mismatch
    }

}
