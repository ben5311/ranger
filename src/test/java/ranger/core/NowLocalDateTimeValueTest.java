package ranger.core;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

class NowLocalDateTimeValueTest {

    @Test
    void testNowLocalDateValue() {
        LocalDateTime now = LocalDateTime.now();
        NowLocalDateTimeValue nowLocalDateTimeValue = new NowLocalDateTimeValue();
        assertThat(now.until(nowLocalDateTimeValue.get(), ChronoUnit.MILLIS), is(lessThan(100L)));
        nowLocalDateTimeValue.reset();
        assertThat(now.until(nowLocalDateTimeValue.get(), ChronoUnit.MILLIS), is(lessThan(100L)));
    }

}