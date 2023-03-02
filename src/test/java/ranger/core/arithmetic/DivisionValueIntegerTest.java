package ranger.core.arithmetic;

import org.junit.jupiter.api.Test;
import ranger.core.CircularValue;
import ranger.core.ConstantValue;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class DivisionValueIntegerTest {

    @Test
    void testDivisionValueIntegerConstantSource() {
        DivisionValueInteger value = new DivisionValueInteger(ConstantValue.of(1), ConstantValue.of(2));
        assertThat(value.get(), is(equalTo(0)));
        value.reset();
        assertThat(value.get(), is(equalTo(0)));
    }

    @Test
    void testDivisionValueIntegerVariableSource() {
        DivisionValueInteger value = new DivisionValueInteger(new CircularValue<>(Arrays.asList(ConstantValue.of(2.5), ConstantValue.of(3.5))), ConstantValue.of(1));
        assertThat(value.get(), is(equalTo(2)));
        value.reset();
        assertThat(value.get(), is(equalTo(3)));
        value.reset();
        assertThat(value.get(), is(equalTo(2)));
    }
    
}