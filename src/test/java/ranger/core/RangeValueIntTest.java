package ranger.core;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class RangeValueIntTest {

    @Test
    void testRangeValueInt() {
        RangeValueInt randomInt = new RangeValueInt(new Range<>(-10, 10));
        for (int i = 0; i < 100; i++) {
            int nextInt = randomInt.get();
            assertThat(nextInt, is(both(greaterThanOrEqualTo(-10)).and(lessThan(10))));
            randomInt.reset();
        }
    }

    @Test
    void testRangeValueIntWithUseEdgeCases() {
        RangeValueInt randomInt = new RangeValueInt(new Range<>(-10, 10), true);
        assertThat(randomInt.get(), is(equalTo(-10)));
        randomInt.reset();
        assertThat(randomInt.get(), is(equalTo(9)));
        randomInt.reset();
        for (int i = 0; i < 100; i++) {
            int nextInt = randomInt.get();
            assertThat(nextInt, is(both(greaterThanOrEqualTo(-10)).and(lessThan(10))));
            randomInt.reset();
        }
    }

}