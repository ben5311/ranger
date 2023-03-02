package ranger.core.arithmetic;

import org.junit.jupiter.api.Test;
import ranger.core.CircularValue;
import ranger.core.ConstantValue;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class AdditionValueByteTest {

    @Test
    void testAdditionValueByteConstantSource() {
        AdditionValueByte value = new AdditionValueByte(ConstantValue.of(1), ConstantValue.of(2.5));
        assertThat(value.get(), is(equalTo((byte) 3)));
        value.reset();
        assertThat(value.get(), is(equalTo((byte) 3)));
    }

    @Test
    void testAdditionValueByteVariableSource() {
        AdditionValueByte value = new AdditionValueByte(new CircularValue<>(Arrays.asList(ConstantValue.of(2.5), ConstantValue.of(3.5))), ConstantValue.of(1));
        assertThat(value.get(), is(equalTo((byte) 3)));
        value.reset();
        assertThat(value.get(), is(equalTo((byte) 4)));
        value.reset();
        assertThat(value.get(), is(equalTo((byte) 3)));
    }

}