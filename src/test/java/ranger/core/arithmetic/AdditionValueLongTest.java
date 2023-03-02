package ranger.core.arithmetic;

import org.junit.jupiter.api.Test;
import ranger.core.CircularValue;
import ranger.core.ConstantValue;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class AdditionValueLongTest {

    @Test
    void testAdditionValueLongConstantSource() {
        AdditionValueLong value = new AdditionValueLong(ConstantValue.of(1), ConstantValue.of(2.5));
        assertThat(value.get(), is(equalTo(3L)));
        value.reset();
        assertThat(value.get(), is(equalTo(3L)));
    }

    @Test
    void testAdditionValueLongVariableSource() {
        AdditionValueLong value = new AdditionValueLong(new CircularValue<>(Arrays.asList(ConstantValue.of(2.5), ConstantValue.of(3.5))), ConstantValue.of(1));
        assertThat(value.get(), is(equalTo(3L)));
        value.reset();
        assertThat(value.get(), is(equalTo(4L)));
        value.reset();
        assertThat(value.get(), is(equalTo(3L)));
    }
    
}