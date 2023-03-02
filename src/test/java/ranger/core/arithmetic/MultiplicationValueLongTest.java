package ranger.core.arithmetic;

import org.junit.jupiter.api.Test;
import ranger.core.CircularValue;
import ranger.core.ConstantValue;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class MultiplicationValueLongTest {

    @Test
    void testMultiplicationValueLongConstantSource() {
        MultiplicationValueLong value = new MultiplicationValueLong(ConstantValue.of(1), ConstantValue.of(2.5));
        assertThat(value.get(), is(equalTo(2L)));
        value.reset();
        assertThat(value.get(), is(equalTo(2L)));
    }

    @Test
    void testMultiplicationValueLongVariableSource() {
        MultiplicationValueLong value = new MultiplicationValueLong(new CircularValue<>(Arrays.asList(ConstantValue.of(2.5), ConstantValue.of(3.5))), ConstantValue.of(1));
        assertThat(value.get(), is(equalTo(2L)));
        value.reset();
        assertThat(value.get(), is(equalTo(3L)));
        value.reset();
        assertThat(value.get(), is(equalTo(2L)));
    }
    
}