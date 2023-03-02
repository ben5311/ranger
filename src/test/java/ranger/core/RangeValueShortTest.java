package ranger.core;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class RangeValueShortTest {

    @Test
    void testRangeValueShort() {
        RangeValueShort randomShort = new RangeValueShort(new Range<>((short) -10, (short) 10));
        for (int i = 0; i < 100; i++) {
            short nextShort = randomShort.get();
            assertThat(nextShort, is(both(greaterThanOrEqualTo((short) -10)).and(lessThan((short) 10))));
            randomShort.reset();
        }
    }

    @Test
    void testRangeValueShortWithUseEdgeCases() {
        RangeValueShort randomShort = new RangeValueShort(new Range<>((short) -10, (short) 10), true);
        assertThat(randomShort.get(), is(equalTo((short) -10)));
        randomShort.reset();
        assertThat(randomShort.get(), is(equalTo((short) 9)));
        randomShort.reset();
        for (int i = 0; i < 100; i++) {
            short nextShort = randomShort.get();
            assertThat(nextShort, is(both(greaterThanOrEqualTo((short) -10)).and(lessThan((short) 10))));
            randomShort.reset();
        }
    }
    
}