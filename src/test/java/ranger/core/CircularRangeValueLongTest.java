package ranger.core;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class CircularRangeValueLongTest {

    @Test
    void testCircularRangeValueLong() {
        CircularRangeValueLong circularLong = new CircularRangeValueLong(new Range<>(-10L, 10L), 1L);
        for (long l = -10L; l <= 10L; l++) {
            long nextLong = circularLong.get();
            assertThat(nextLong, is(equalTo(l)));
            circularLong.reset();
        }
        assertThat(circularLong.get(), is(equalTo(-10L)));   //starts at range's left edge again
    }
}