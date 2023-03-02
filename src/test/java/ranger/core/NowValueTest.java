package ranger.core;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

class NowValueTest {

    @Test
    void testNowValue() {
        NowValue nowValue = new NowValue();
        assertThat((double) nowValue.get(), is(closeTo(System.currentTimeMillis(), 100)));
        nowValue.reset();
        assertThat((double) nowValue.get(), is(closeTo(System.currentTimeMillis(), 100)));
    }

}