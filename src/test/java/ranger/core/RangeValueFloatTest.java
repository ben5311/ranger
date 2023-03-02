package ranger.core;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class RangeValueFloatTest {

    @Test
    void testRangeValueFloat() {
        RangeValueFloat randomFloat = new RangeValueFloat(new Range<>(-10.0f, 10.0f));
        for (int i = 0; i < 100; i++) {
            float nextFloat = randomFloat.get();
            assertThat(nextFloat, is(both(greaterThanOrEqualTo(-10.0f)).and(lessThan(10.0f))));
            randomFloat.reset();
        }
    }

    @Test
    void testRangeValueFloatWithUseEdgeCases() {
        RangeValueFloat randomFloat = new RangeValueFloat(new Range<>(-10.0f, 10.0f), true);
        assertThat(randomFloat.get(), is(equalTo(-10.0f)));
        randomFloat.reset();
        assertThat(randomFloat.get(), is(equalTo(10.0f - RangeValueFloat.EPSILON)));
        randomFloat.reset();
        for (int i = 0; i < 100; i++) {
            float nextFloat = randomFloat.get();
            assertThat(nextFloat, is(both(greaterThanOrEqualTo(-10.0f)).and(lessThan(10.0f))));
            randomFloat.reset();
        }
    }

}