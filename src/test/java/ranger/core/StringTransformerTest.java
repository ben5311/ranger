package ranger.core;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StringTransformerTest {

    @Test
    void testStringTransformerConstantSource() {
        DiscreteValue<String> randomName = new DiscreteValue<>(Arrays.asList(ConstantValue.of("Katharina"), ConstantValue.of("Emil"), ConstantValue.of("Heinrich")));
        RangeValueInt randomAge = new RangeValueInt(new Range<>(18, 80));
        StringTransformer stringTransformer = new StringTransformer(ConstantValue.of("{} is {}"), Arrays.asList(randomName, randomAge));

        for (int i = 0; i < 10; i++) {
            String nextString = stringTransformer.get();
            assertThat(nextString, matchesPattern("\\w+ is \\d{2}"));
            assertThat(nextString.split(" is ")[0], is(oneOf("Katharina", "Emil", "Heinrich")));
            assertThat(nextString.split(" is ")[1], is(both(greaterThanOrEqualTo("18")).and(lessThan("80"))));
        }
    }

    @Test
    void testStringTransformerVariableSource() {
        DiscreteValue<String> randomFormatString = new DiscreteValue<>(Arrays.asList(ConstantValue.of("{} is {}"), ConstantValue.of("{}, {}")));
        DiscreteValue<String> randomName = new DiscreteValue<>(Arrays.asList(ConstantValue.of("Katharina"), ConstantValue.of("Emil"), ConstantValue.of("Heinrich")));
        RangeValueInt randomAge = new RangeValueInt(new Range<>(18, 80));
        StringTransformer stringTransformer = new StringTransformer(randomFormatString, Arrays.asList(randomName, randomAge));

        for (int i = 0; i < 10; i++) {
            String nextString = stringTransformer.get();
            assertThat(nextString, matchesPattern("(\\w+ is \\d{2})|(\\w+, \\d{2})"));
            assertThat(nextString.split("( is )|(, )")[0], is(oneOf("Katharina", "Emil", "Heinrich")));
            assertThat(nextString.split("( is )|(, )")[1], is(both(greaterThanOrEqualTo("18")).and(lessThan("80"))));
        }
    }

    @Test
    void testErrorConstructWithNullArgument() {
        assertThrows(ValueException.class, () -> new StringTransformer(null, null));
        assertThrows(ValueException.class, () -> new StringTransformer(ConstantValue.of("{}"), null));
        assertThrows(ValueException.class, () -> new StringTransformer(null, Collections.emptyList()));
    }

}