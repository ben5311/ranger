package ranger.core;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TimeFormatTransformerTest {
    private static final LocalDate TODAY = LocalDate.now();
    private static final String TIME_STRING = String.format("%d-%02d-%02d", TODAY.getYear(), TODAY.getMonthValue(), TODAY.getDayOfMonth());

    @Test
    void testTimeFormatTransformerMillis() {
        TimeFormatTransformer timeTransformer = new TimeFormatTransformer(ConstantValue.of("yyyy-MM-dd"), new NowValue());
        assertThat(timeTransformer.get(), is(equalTo(TIME_STRING)));
    }

    @Test
    void testTimeFormatTransformerDate() {
        TimeFormatTransformer timeTransformer = new TimeFormatTransformer(ConstantValue.of("yyyy-MM-dd"), new NowDateValue());
        assertThat(timeTransformer.get(), is(equalTo(TIME_STRING)));
    }

    @Test
    void testTimeFormatTransformerLocalDate() {
        TimeFormatTransformer timeTransformer = new TimeFormatTransformer(ConstantValue.of("yyyy-MM-dd"), new NowLocalDateValue());
        assertThat(timeTransformer.get(), is(equalTo(TIME_STRING)));
    }

    @Test
    void testTimeFormatTransformerLocalDateTime() {
        TimeFormatTransformer timeTransformer = new TimeFormatTransformer(ConstantValue.of("yyyy-MM-dd"), new NowLocalDateTimeValue());
        assertThat(timeTransformer.get(), is(equalTo(TIME_STRING)));
    }

    @Test
    void testErrorConstructWithNullArgument() {
        assertThrows(ValueException.class, () -> new TimeFormatTransformer(null, null));
        assertThrows(ValueException.class, () -> new TimeFormatTransformer(ConstantValue.of("yyyy-mm-DD"), null));
        assertThrows(ValueException.class, () -> new TimeFormatTransformer(null, new NowValue()));
    }
    
}