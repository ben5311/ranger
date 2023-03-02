package ranger.core.arithmetic;

import org.junit.jupiter.api.Test;
import ranger.core.CircularValue;
import ranger.core.ConstantValue;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class MultiplicationValueByteTest {

    @Test
    void testMultiplicationValueByteConstantSource() {
        MultiplicationValueByte value = new MultiplicationValueByte(ConstantValue.of(1), ConstantValue.of(2.5));
        assertThat(value.get(), is(equalTo((byte) 2)));
        value.reset();
        assertThat(value.get(), is(equalTo((byte) 2)));
    }

    @Test
    void testMultiplicationValueByteVariableSource() {
        MultiplicationValueByte value = new MultiplicationValueByte(new CircularValue<>(Arrays.asList(ConstantValue.of(2.5), ConstantValue.of(3.5))), ConstantValue.of(1));
        assertThat(value.get(), is(equalTo((byte) 2)));
        value.reset();
        assertThat(value.get(), is(equalTo((byte) 3)));
        value.reset();
        assertThat(value.get(), is(equalTo((byte) 2)));
    }
    
}