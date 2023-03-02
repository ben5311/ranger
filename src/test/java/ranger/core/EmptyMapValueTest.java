package ranger.core;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class EmptyMapValueTest {

    @Test
    void testEmptyMapValue() {
        EmptyMapValue<?, ?> emptyMapValue = new EmptyMapValue<>();
        assertThat(emptyMapValue.get(), is(equalTo(Collections.emptyMap())));
        emptyMapValue.reset();
        assertThat(emptyMapValue.get(), is(equalTo(Collections.emptyMap())));
    }

}