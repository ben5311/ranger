package ranger.core;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WeightedValueTest {

    @Test
    void testWeightedValueConstantSources() {
        WeightedValue<String> genderValue = new WeightedValue<>(Arrays.asList(new WeightedValue.WeightedValuePair<>(ConstantValue.of("female"), 60), new WeightedValue.WeightedValuePair<>(ConstantValue.of("male"), 40)));

        int femaleCount = 0;
        int maleCount = 0;
        for (int i = 1; i <= 1000; i++) {
            String nextString = genderValue.get();
            assertThat(nextString, is(oneOf("female", "male")));
            switch(nextString) {
                case "female":
                    femaleCount++;
                    break;
                case "male":
                    maleCount++;
                    break;
            }
            genderValue.reset();
        }
        assertThat((double) femaleCount, is(closeTo(600, 60))); //allow 10% error
        assertThat((double) maleCount, is(closeTo(400, 40)));
    }

    @Test
    void testWeightedValueVariableSources() {
        DiscreteValue<String> femaleNameValue = new DiscreteValue<>(Arrays.asList(ConstantValue.of("Emma"), ConstantValue.of("Sofia")));
        DiscreteValue<String> maleNameValue = new DiscreteValue<>(Arrays.asList(ConstantValue.of("Alfred"), ConstantValue.of("Bruce")));
        WeightedValue<String> value = new WeightedValue<>(Arrays.asList(new WeightedValue.WeightedValuePair<>(femaleNameValue, 30), new WeightedValue.WeightedValuePair<>(maleNameValue, 20)));

        int femaleNameCount = 0;
        int maleNameCount = 0;
        for (int i = 1; i <= 100; i++) {
            String nextName = value.get();
            assertThat(nextName, is(oneOf("Emma", "Sofia", "Alfred", "Bruce")));
            if (nextName.equals("Emma") || nextName.equals("Sofia")) { femaleNameCount++; }
            else if (nextName.equals("Alfred") || nextName.equals("Bruce")) { maleNameCount++; }
            value.reset();
        }
        assertThat((double) femaleNameCount, is(closeTo(60, 10)));
        assertThat((double) maleNameCount, is(closeTo(40, 10)));
    }


    @Test
    void testErrorConstructWithNullArgument() {
        assertThrows(ValueException.class, () -> new WeightedValue.WeightedValuePair<>(null, 1));
        assertThrows(ValueException.class, () -> new WeightedValue.WeightedValuePair<>(ConstantValue.of("value"), -1));   //negative weight
        assertThrows(ValueException.class, () -> new WeightedValue<>(null)); //empty list
        assertThrows(ValueException.class, () -> new WeightedValue<>(Collections.emptyList())); //empty list
    }

}