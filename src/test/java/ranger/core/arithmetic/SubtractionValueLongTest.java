package ranger.core.arithmetic;

import org.junit.jupiter.api.Test;
import ranger.core.CircularValue;
import ranger.core.ConstantValue;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class SubtractionValueLongTest {

    @Test
    void testSubtractionValueLongConstantSource() {
        SubtractionValueLong value = new SubtractionValueLong(ConstantValue.of(1), ConstantValue.of(2.5));
        assertThat(value.get(), is(equalTo(-1L)));
        value.reset();
        assertThat(value.get(), is(equalTo(-1L)));
    }

    @Test
    void testSubtractionValueLongVariableSource() {
        SubtractionValueLong value = new SubtractionValueLong(new CircularValue<>(Arrays.asList(ConstantValue.of(2.5), ConstantValue.of(3.5))), ConstantValue.of(1));
        assertThat(value.get(), is(equalTo(1L)));
        value.reset();
        assertThat(value.get(), is(equalTo(2L)));
        value.reset();
        assertThat(value.get(), is(equalTo(1L)));
    }
    
}