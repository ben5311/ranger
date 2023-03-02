package ranger.core;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class EmptyListValueTest {

    @Test
    void testEmptyListValue() {
        EmptyListValue<?> emptyListValue = new EmptyListValue<>();
        assertThat(emptyListValue.get(), is(equalTo(Collections.emptyList())));
        emptyListValue.reset();
        assertThat(emptyListValue.get(), is(equalTo(Collections.emptyList())));
    }

}