package ranger.core;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class CircularRangeValueCharTest {

    @Test
    void testCircularRangeValueByte() {
        CircularRangeValueChar circularChar = new CircularRangeValueChar(new Range<>('a', 'z'), 1);
        for (char c = 'a'; c <= 'z'; c++) {
            char nextChar = circularChar.get();
            assertThat(nextChar, is(equalTo(c)));
            circularChar.reset();
        }
        assertThat(circularChar.get(), is(equalTo('a')));   //starts at range's left edge again
    }
}