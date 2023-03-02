package ranger.core;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class CircularRangeValueIntTest {

    @Test
    void testCircularRangeValueInt() {
        CircularRangeValueInt circularInt = new CircularRangeValueInt(new Range<>(-10, 10), 1);
        for (int i = -10; i <= 10; i++) {
            int nextInt = circularInt.get();
            assertThat(nextInt, is(equalTo(i)));
            circularInt.reset();
        }
        assertThat(circularInt.get(), is(equalTo(-10)));   //starts at range's left edge again
    }
}