package ranger.core;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class NowLocalDateValueTest {

    @Test
    void testNowLocalDateValue() {
        LocalDate today = LocalDate.now();
        NowLocalDateValue nowLocalDateValue = new NowLocalDateValue();
        assertThat(nowLocalDateValue.get(), is(equalTo(today)));
        nowLocalDateValue.reset();
        assertThat(nowLocalDateValue.get(), is(equalTo(today)));
    }

}