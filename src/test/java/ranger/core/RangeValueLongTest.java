package ranger.core;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class RangeValueLongTest {

    @Test
    void testRangeValueLong() {
        RangeValueLong randomLong = new RangeValueLong(new Range<>(-10L, 10L));
        for (int i = 0; i < 100; i++) {
            long nextLong = randomLong.get();
            assertThat(nextLong, is(both(greaterThanOrEqualTo(-10L)).and(lessThan(10L))));
            randomLong.reset();
        }
    }

    @Test
    void testRangeValueLongWithUseEdgeCases() {
        RangeValueLong randomLong = new RangeValueLong(new Range<>(-10L, 10L), true);
        assertThat(randomLong.get(), is(equalTo(-10L)));
        randomLong.reset();
        assertThat(randomLong.get(), is(equalTo(9L)));
        randomLong.reset();
        for (int i = 0; i < 100; i++) {
            long nextLong = randomLong.get();
            assertThat(nextLong, is(both(greaterThanOrEqualTo(-10L)).and(lessThan(10L))));
            randomLong.reset();
        }
    }

}