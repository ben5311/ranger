package ranger.core;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class RangeValueDoubleTest {

    @Test
    void testRangeValueDouble() {
        RangeValueDouble randomDouble = new RangeValueDouble(new Range<>(-10.0, 10.0));
        for (int i = 0; i < 100; i++) {
            double nextDouble = randomDouble.get();
            assertThat(nextDouble, is(both(greaterThanOrEqualTo(-10.0)).and(lessThan(10.0))));
            randomDouble.reset();
        }
    }

    @Test
    void testRangeValueDoubleWithUseEdgeCases() {
        RangeValueDouble randomDouble = new RangeValueDouble(new Range<>(-10.0, 10.0), true);
        assertThat(randomDouble.get(), is(equalTo(-10.0)));
        randomDouble.reset();
        assertThat(randomDouble.get(), is(equalTo(10.0 - RangeValueDouble.EPSILON)));
        randomDouble.reset();
        for (int i = 0; i < 100; i++) {
            double nextDouble = randomDouble.get();
            assertThat(nextDouble, is(both(greaterThanOrEqualTo(-10.0)).and(lessThan(10.0))));
            randomDouble.reset();
        }
    }

}