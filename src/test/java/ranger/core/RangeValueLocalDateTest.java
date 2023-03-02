package ranger.core;

import org.junit.jupiter.api.Test;
import ranger.distribution.UniformDistribution;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RangeValueLocalDateTest {

    private static final LocalDate begin = LocalDate.of(2019, 1, 1);
    private static final LocalDate end = LocalDate.of(2019, 12, 31);

    @Test
    void testRangeValueLocalDate() {
        RangeValueLocalDate randomLocalDate = new RangeValueLocalDate(begin, end);
        for (int i = 0; i < 100; i++) {
            LocalDate nextLocalDate = randomLocalDate.get();
            assertThat(nextLocalDate, is(both(greaterThanOrEqualTo(begin)).and(lessThan(end))));
            randomLocalDate.reset();
        }
    }

    @Test
    void testRangeValueLocalDateWithUseEdgeCases() {
        RangeValueLocalDate randomLocalDate = new RangeValueLocalDate(begin, end, true);
        assertThat(randomLocalDate.get(), is(equalTo(begin)));
        randomLocalDate.reset();
        assertThat(randomLocalDate.get(), is(equalTo(LocalDate.of(2019, 12, 30))));
        randomLocalDate.reset();
        for (int i = 0; i < 100; i++) {
            LocalDate nextLocalDate = randomLocalDate.get();
            assertThat(nextLocalDate, is(both(greaterThanOrEqualTo(begin)).and(lessThan(end))));
            randomLocalDate.reset();
        }
    }

    @Test
    void testErrorConstructWithNullArgument() {
        assertThrows(ValueException.class, () -> new RangeValueLocalDate(null, null));
        assertThrows(ValueException.class, () -> new RangeValueLocalDate(LocalDate.now(), null));
        assertThrows(ValueException.class, () -> new RangeValueLocalDate(null, LocalDate.now()));
        assertThrows(ValueException.class, () -> new RangeValueLocalDate(null, null, false, null));
        assertThrows(ValueException.class, () -> new RangeValueLocalDate(LocalDate.now(), null, false, null));
        assertThrows(ValueException.class, () -> new RangeValueLocalDate(null, LocalDate.now(), false, null));
        assertThrows(ValueException.class, () -> new RangeValueLocalDate(null, null, false, new UniformDistribution()));
        assertThrows(InvalidRangeBoundsException.class, () -> new RangeValueLocalDate(LocalDate.now(), LocalDate.now().minusDays(1)));     //decreasing range
        assertThrows(InvalidRangeBoundsException.class, () -> new RangeValueLocalDate(LocalDate.now(), LocalDate.now()));     //empty range
    }

}