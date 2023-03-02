package ranger.core.arithmetic;

import org.junit.jupiter.api.Test;
import ranger.core.CircularValue;
import ranger.core.ConstantValue;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class SubtractionValueFloatTest {

    @Test
    void testSubtractionValueFloatConstantSource() {
        SubtractionValueFloat value = new SubtractionValueFloat(ConstantValue.of(1), ConstantValue.of(2.5));
        assertThat(value.get(), is(equalTo(-1.5f)));
        value.reset();
        assertThat(value.get(), is(equalTo(-1.5f)));
    }

    @Test
    void testSubtractionValueFloatVariableSource() {
        SubtractionValueFloat value = new SubtractionValueFloat(new CircularValue<>(Arrays.asList(ConstantValue.of(2.5), ConstantValue.of(3.5))), ConstantValue.of(1));
        assertThat(value.get(), is(equalTo(1.5f)));
        value.reset();
        assertThat(value.get(), is(equalTo(2.5f)));
        value.reset();
        assertThat(value.get(), is(equalTo(1.5f)));
    }
    
}