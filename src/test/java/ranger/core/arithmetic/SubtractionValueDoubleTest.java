package ranger.core.arithmetic;

import org.junit.jupiter.api.Test;
import ranger.core.CircularValue;
import ranger.core.ConstantValue;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class SubtractionValueDoubleTest {

    @Test
    void testSubtractionValueDoubleConstantSource() {
        SubtractionValueDouble value = new SubtractionValueDouble(ConstantValue.of(1), ConstantValue.of(2.5));
        assertThat(value.get(), is(equalTo(-1.5)));
        value.reset();
        assertThat(value.get(), is(equalTo(-1.5)));
    }

    @Test
    void testSubtractionValueDoubleVariableSource() {
        SubtractionValueDouble value = new SubtractionValueDouble(new CircularValue<>(Arrays.asList(ConstantValue.of(2.5), ConstantValue.of(3.5))), ConstantValue.of(1));
        assertThat(value.get(), is(equalTo(1.5)));
        value.reset();
        assertThat(value.get(), is(equalTo(2.5)));
        value.reset();
        assertThat(value.get(), is(equalTo(1.5)));
    }
    
}