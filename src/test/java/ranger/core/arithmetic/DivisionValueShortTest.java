package ranger.core.arithmetic;

import org.junit.jupiter.api.Test;
import ranger.core.CircularValue;
import ranger.core.ConstantValue;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class DivisionValueShortTest {

    @Test
    void testDivisionValueShortConstantSource() {
        DivisionValueShort value = new DivisionValueShort(ConstantValue.of(1), ConstantValue.of(2.5));
        assertThat(value.get(), is(equalTo((short) 0)));
        value.reset();
        assertThat(value.get(), is(equalTo((short) 0)));
    }

    @Test
    void testDivisionValueShortVariableSource() {
        DivisionValueShort value = new DivisionValueShort(new CircularValue<>(Arrays.asList(ConstantValue.of(2.5), ConstantValue.of(3.5))), ConstantValue.of(1));
        assertThat(value.get(), is(equalTo((short) 2)));
        value.reset();
        assertThat(value.get(), is(equalTo((short) 3)));
        value.reset();
        assertThat(value.get(), is(equalTo((short) 2)));
    }
    
}