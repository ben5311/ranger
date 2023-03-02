package ranger.core;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class CircularRangeValueFloatTest {

    @Test
    void testCircularRangeValueFloat() {
        CircularRangeValueFloat circularFloat = new CircularRangeValueFloat(new Range<>(-10.0f, 10.0f), 0.5f);
        for (float f = -10.0f; f <= 10.0f; f += 0.5f) {
            float nextFloat = circularFloat.get();
            assertThat(nextFloat, is(equalTo(f)));
            circularFloat.reset();
        }
        assertThat(circularFloat.get(), is(equalTo(-10.0f)));   //starts at range's left edge again
    }
}