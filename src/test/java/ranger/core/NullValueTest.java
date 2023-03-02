package ranger.core;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class NullValueTest {

    @Test
    void testNullValue() {
        NullValue nullValue = new NullValue();
        assertThat(nullValue.get(), is(nullValue()));
        nullValue.reset();
        assertThat(nullValue.get(), is(nullValue()));
    }

}