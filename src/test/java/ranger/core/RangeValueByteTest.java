package ranger.core;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class RangeValueByteTest {

    @Test
    void testRangeValueByte() {
        RangeValueByte randomByte = new RangeValueByte(new Range<>((byte) -10, (byte) 10));
        for (int i = 0; i < 100; i++) {
            byte nextByte = randomByte.get();
            assertThat(nextByte, is(both(greaterThanOrEqualTo((byte) -10)).and(lessThan((byte)  10))));
            randomByte.reset();
        }
    }

    @Test
    void testRangeValueByteWithUseEdgeCases() {
        RangeValueByte randomByte = new RangeValueByte(new Range<>((byte) -10, (byte) 10), true);
        assertThat(randomByte.get(), is(equalTo((byte) -10)));
        randomByte.reset();
        assertThat(randomByte.get(), is(equalTo((byte) 9)));
        randomByte.reset();
        for (int i = 0; i < 100; i++) {
            byte nextByte = randomByte.get();
            assertThat(nextByte, is(both(greaterThanOrEqualTo((byte) -10)).and(lessThan((byte)  10))));
            randomByte.reset();
        }
    }

}