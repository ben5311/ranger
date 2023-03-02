package ranger.core;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class CircularRangeValueDateTest {

    @Test
    void testCircularRangeValueDate() {
        CircularRangeValueDate circularDate = new CircularRangeValueDate(new Range<>(new Date(2019, 12, 30), new Date(2020, 01, 02)), 1);
        assertThat(circularDate.get(), is(equalTo(new Date(2019, 12, 30 ))));
        circularDate.reset();
        assertThat(circularDate.get(), is(equalTo(new Date(2019, 12, 31 ))));
        circularDate.reset();
        assertThat(circularDate.get(), is(equalTo(new Date(2020, 01, 01 ))));
        circularDate.reset();
        assertThat(circularDate.get(), is(equalTo(new Date(2020, 01, 02 ))));
        circularDate.reset();
        assertThat(circularDate.get(), is(equalTo(new Date(2019, 12, 30 ))));
    }

    @Test
    void testCircularRangeValueDate2() {
        CircularRangeValueDate circularDate = new CircularRangeValueDate(new Range<>(new Date(2019, 12, 30), new Date(2020, 01, 02)), 2);
        assertThat(circularDate.get(), is(equalTo(new Date(2019, 12, 30 ))));
        circularDate.reset();
        assertThat(circularDate.get(), is(equalTo(new Date(2020, 01, 01 ))));
        circularDate.reset();
        assertThat(circularDate.get(), is(equalTo(new Date(2019, 12, 30 ))));
    }
}