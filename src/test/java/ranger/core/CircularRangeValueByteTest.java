package ranger.core;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class CircularRangeValueByteTest {

    @Test
    void testCircularRangeValueByte() {
        CircularRangeValueByte circularByte = new CircularRangeValueByte(new Range<>((byte) -10, (byte) 10), (byte) 1);
        for (byte b = -10; b <= 10; b++) {
            byte nextByte = circularByte.get();
            assertThat(nextByte, is(equalTo(b)));
            circularByte.reset();
        }
        assertThat(circularByte.get(), is(equalTo((byte) -10)));   //starts at range's left edge again
    }

}