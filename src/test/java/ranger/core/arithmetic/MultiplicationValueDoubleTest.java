package ranger.core.arithmetic;

import org.junit.jupiter.api.Test;
import ranger.core.CircularValue;
import ranger.core.ConstantValue;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class MultiplicationValueDoubleTest {

    @Test
    void testMultiplicationValueDoubleConstantSource() {
        MultiplicationValueDouble value = new MultiplicationValueDouble(ConstantValue.of(1), ConstantValue.of(2.5));
        assertThat(value.get(), is(equalTo(2.5)));
        value.reset();
        assertThat(value.get(), is(equalTo(2.5)));
    }

    @Test
    void testMultiplicationValueDoubleVariableSource() {
        MultiplicationValueDouble value = new MultiplicationValueDouble(new CircularValue<>(Arrays.asList(ConstantValue.of(2.5), ConstantValue.of(3.5))), ConstantValue.of(1));
        assertThat(value.get(), is(equalTo(2.5)));
        value.reset();
        assertThat(value.get(), is(equalTo(3.5)));
        value.reset();
        assertThat(value.get(), is(equalTo(2.5)));
    }
    
}