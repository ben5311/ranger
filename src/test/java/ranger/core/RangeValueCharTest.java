package ranger.core;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class RangeValueCharTest {

    @Test
    void testRangeValueChar() {
        RangeValueChar randomChar = new RangeValueChar(new Range<>('a', 'z'));
        for (int i = 0; i < 100; i++) {
            char nextChar = randomChar.get();
            assertThat(nextChar, is(both(greaterThanOrEqualTo('a')).and(lessThanOrEqualTo('z'))));
            randomChar.reset();
        }
    }

    @Test
    void testRangeValueCharWithUseEdgeCases() {
        RangeValueChar randomChar = new RangeValueChar(new Range<>('a', 'z'), true);
        assertThat(randomChar.get(), is(equalTo('a')));
        randomChar.reset();
        assertThat(randomChar.get(), is(equalTo('z')));
        randomChar.reset();
        for (int i = 0; i < 100; i++) {
            char nextChar = randomChar.get();
            assertThat(nextChar, is(both(greaterThanOrEqualTo('a')).and(lessThanOrEqualTo('z'))));
            randomChar.reset();
        }
    }

}