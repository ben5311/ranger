package ranger.core;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class ConstantValueTest {

    @Test
    void testConstantValue() {
        ConstantValue<String> constantValue = ConstantValue.of("constant string");
        for (int i = 1; i <= 10; i++) {
            assertThat(constantValue.get(), is(equalTo("constant string")));
            constantValue.reset();
        }
    }

}