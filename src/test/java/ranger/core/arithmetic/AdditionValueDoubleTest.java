package ranger.core.arithmetic;

import org.junit.jupiter.api.Test;
import ranger.core.CircularValue;
import ranger.core.ConstantValue;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class AdditionValueDoubleTest {

    @Test
    void testAdditionValueDoubleConstantSource() {
        AdditionValueDouble value = new AdditionValueDouble(ConstantValue.of(1), ConstantValue.of(2.5));
        assertThat(value.get(), is(equalTo(3.5)));
        value.reset();
        assertThat(value.get(), is(equalTo(3.5)));
    }

    @Test
    void testAdditionValueDoubleVariableSource() {
        AdditionValueDouble value = new AdditionValueDouble(new CircularValue<>(Arrays.asList(ConstantValue.of(2.5), ConstantValue.of(3.5))), ConstantValue.of(1));
        assertThat(value.get(), is(equalTo(3.5)));
        value.reset();
        assertThat(value.get(), is(equalTo(4.5)));
        value.reset();
        assertThat(value.get(), is(equalTo(3.5)));
    }

}