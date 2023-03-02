package ranger.core;

import org.junit.jupiter.api.Test;
import ranger.distribution.UniformDistribution;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RangeValueLocalDateTimeTest {

    private static final LocalDateTime begin = LocalDateTime.of(2019, 1, 1, 0, 0, 0);
    private static final LocalDateTime end = LocalDateTime.of(2019, 12, 31, 23, 59, 59);

    @Test
    void testRangeValueLocalDateTime() {
        RangeValueLocalDateTime randomLocalDateTime = new RangeValueLocalDateTime(begin, end);
        for (int i = 0; i < 100; i++) {
            LocalDateTime nextLocalDateTime = randomLocalDateTime.get();
            assertThat(nextLocalDateTime, is(both(greaterThanOrEqualTo(begin)).and(lessThan(end))));
            randomLocalDateTime.reset();
        }
    }

    @Test
    void testRangeValueLocalDateTimeWithUseEdgeCases() {
        RangeValueLocalDateTime randomLocalDateTime = new RangeValueLocalDateTime(begin, end, true);
        assertThat(randomLocalDateTime.get(), is(equalTo(begin)));
        randomLocalDateTime.reset();
        assertThat(randomLocalDateTime.get(), is(equalTo(end.minusSeconds(1))));
        randomLocalDateTime.reset();
        for (int i = 0; i < 100; i++) {
            LocalDateTime nextLocalDateTime = randomLocalDateTime.get();
            assertThat(nextLocalDateTime, is(both(greaterThanOrEqualTo(begin)).and(lessThan(end))));
            randomLocalDateTime.reset();
        }
    }

    @Test
    void testErrorConstructWithNullArgument() {
        assertThrows(ValueException.class, () -> new RangeValueLocalDateTime(null, null));
        assertThrows(ValueException.class, () -> new RangeValueLocalDateTime(LocalDateTime.now(), null));
        assertThrows(ValueException.class, () -> new RangeValueLocalDateTime(null, LocalDateTime.now()));
        assertThrows(ValueException.class, () -> new RangeValueLocalDateTime(null, null, false, null));
        assertThrows(ValueException.class, () -> new RangeValueLocalDateTime(LocalDateTime.now(), null, false, null));
        assertThrows(ValueException.class, () -> new RangeValueLocalDateTime(null, LocalDateTime.now(), false, null));
        assertThrows(ValueException.class, () -> new RangeValueLocalDateTime(null, null, false, new UniformDistribution()));
        LocalDateTime now = LocalDateTime.now();
        assertThrows(InvalidRangeBoundsException.class, () -> new RangeValueLocalDateTime(now, now.minusDays(1)));     //decreasing range
        assertThrows(InvalidRangeBoundsException.class, () -> new RangeValueLocalDateTime(now, now));     //empty range
    }
    
}