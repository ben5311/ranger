package ranger.core;

import org.junit.jupiter.api.Test;
import ranger.distribution.UniformDistribution;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RandomLengthListValueTest {

    @Test
    void testRandomLengthListValueConstantSource() {
        RandomLengthListValue<String> randomLengthList = new RandomLengthListValue<>(0, 10, ConstantValue.of("content"));
        for (int i = 0; i < 100; i++) {
            List<String> generatedList = randomLengthList.get();
            assertThat(generatedList.size(), is(lessThan(10)));
            for (String s : generatedList) {
                assertThat(s, is(equalTo("content")));
            }
            randomLengthList.reset();
        }
    }

    @Test
    void testRandomLengthListValueVariableSource() {
        DiscreteValue<String> randomContent = new DiscreteValue<>(Arrays.asList(ConstantValue.of("Lorem"), ConstantValue.of("ipsum"), ConstantValue.of("dolor")));
        RandomLengthListValue<String> randomLengthList = new RandomLengthListValue<>(0, 10, randomContent);
        for (int i = 0; i < 100; i++) {
            List<String> generatedList = randomLengthList.get();
            assertThat(generatedList.size(), is(lessThan(10)));
            for (String s : generatedList) {
                assertThat(s, is(oneOf("Lorem", "ipsum", "dolor")));
            }
            randomLengthList.reset();
        }
    }

    @Test
    void testErrorConstructWithNullArgument() {
        assertThrows(ValueException.class, () -> new RandomLengthListValue<>(1, 10, null));
        assertThrows(ValueException.class, () -> new RandomLengthListValue<>(1, 10, null, null));
        assertThrows(ValueException.class, () -> new RandomLengthListValue<>(1, 10, ConstantValue.of("value"), null));
        assertThrows(ValueException.class, () -> new RandomLengthListValue<>(1, 10, null, new UniformDistribution()));
    }

    @Test
    void testErrorWrongLengthRange() {
        assertThrows(ValueException.class, () -> new RandomLengthListValue<>(-2, -1, ConstantValue.of("value")));   //both negative
        assertThrows(ValueException.class, () -> new RandomLengthListValue<>(-1, 2, ConstantValue.of("value")));    //min negative
        assertThrows(ValueException.class, () -> new RandomLengthListValue<>(1, 1, ConstantValue.of("value")));     //empty range
        assertThrows(ValueException.class, () -> new RandomLengthListValue<>(10, 5, ConstantValue.of("value")));    //decreasing range
    }

}