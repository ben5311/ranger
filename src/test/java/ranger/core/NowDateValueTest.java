package ranger.core;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

class NowDateValueTest {

    @Test
    void testNowDateValue() {
        NowDateValue nowDateValue = new NowDateValue();
        assertThat((double) nowDateValue.get().getTime(), is(closeTo(System.currentTimeMillis(), 100)));
        nowDateValue.reset();
        assertThat((double) nowDateValue.get().getTime(), is(closeTo(System.currentTimeMillis(), 100)));
    }

}