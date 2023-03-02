package ranger.core.arithmetic;

import org.junit.jupiter.api.Test;
import ranger.core.CircularValue;
import ranger.core.ConstantValue;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class AdditionValueShortTest {

    @Test
    void testAdditionValueShortConstantSource() {
        AdditionValueShort value = new AdditionValueShort(ConstantValue.of(1), ConstantValue.of(2.5));
        assertThat(value.get(), is(equalTo((short) 3)));
        value.reset();
        assertThat(value.get(), is(equalTo((short) 3)));
    }

    @Test
    void testAdditionValueShortVariableSource() {
        AdditionValueShort value = new AdditionValueShort(new CircularValue<>(Arrays.asList(ConstantValue.of(2.5), ConstantValue.of(3.5))), ConstantValue.of(1));
        assertThat(value.get(), is(equalTo((short) 3)));
        value.reset();
        assertThat(value.get(), is(equalTo((short) 4)));
        value.reset();
        assertThat(value.get(), is(equalTo((short) 3)));
    }
    
}