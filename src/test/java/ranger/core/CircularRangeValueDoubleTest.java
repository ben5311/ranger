package ranger.core;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class CircularRangeValueDoubleTest {

    @Test
    void testCircularRangeValueDouble() {
        CircularRangeValueDouble circularDouble = new CircularRangeValueDouble(new Range<>(-10.0, 10.0), 0.5);
        for (double d = -10.0; d <= 10.0; d += 0.5) {
            double nextDouble = circularDouble.get();
            assertThat(nextDouble, is(equalTo(d)));
            circularDouble.reset();
        }
        assertThat(circularDouble.get(), is(equalTo(-10.0)));   //starts at range's left edge again
    }
}