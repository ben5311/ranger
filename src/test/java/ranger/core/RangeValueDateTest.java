package ranger.core;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class RangeValueDateTest {

    @Test
    void testRangeValueDate() {
        RangeValueDate randomDate = new RangeValueDate(new Range<>(new Date(1500000000000L), new Date(1600000000000L)));
        for (int i = 0; i < 100; i++) {
            Date nextDate = randomDate.get();
            assertThat(nextDate.getTime(), is(both(greaterThanOrEqualTo(1500000000000L)).and(lessThan(1600000000000L))));
            randomDate.reset();
        }
    }

    @Test
    void testRangeValueDateWithUseEdgeCases() {
        RangeValueDate randomDate = new RangeValueDate(new Range<>(new Date(1500000000000L), new Date(1600000000000L)), true);
        assertThat(randomDate.get().getTime(), is(equalTo(1500000000000L)));
        randomDate.reset();
        assertThat(randomDate.get().getTime(), is(equalTo(1600000000000L -1)));
        randomDate.reset();
        for (int i = 0; i < 100; i++) {
            Date nextDate = randomDate.get();
            assertThat(nextDate.getTime(), is(both(greaterThanOrEqualTo(1500000000000L)).and(lessThan(1600000000000L))));
            randomDate.reset();
        }
    }

}