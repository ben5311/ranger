package ranger.core;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class CircularRangeValueShortTest {

    @Test
    void testCircularRangeValueShort() {
        CircularRangeValueShort circularShort = new CircularRangeValueShort(new Range<>((short) -10, (short) 10), (short) 1);
        for (short s = -10; s <= 10; s++) {
            short nextShort = circularShort.get();
            assertThat(nextShort, is(equalTo(s)));
            circularShort.reset();
        }
        assertThat(circularShort.get(), is(equalTo((short) -10)));   //starts at range's left edge again
    }
}