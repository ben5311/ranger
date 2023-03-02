package ranger.core;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExactWeightedValueTest {

    @Test
    void testExactWeightedValueConstantSources() {
        ExactWeightedValue<String> weightedGender = new ExactWeightedValue<>(Arrays.asList(new ExactWeightedValue.CountValuePair<>(ConstantValue.of("female"), 60), new ExactWeightedValue.CountValuePair<>(ConstantValue.of("male"), 40)));

        int femaleCount = 0;
        int maleCount = 0;
        for (int i = 1; i <= 100; i++) {
            String nextGender = weightedGender.get();
            assertThat(nextGender, is(oneOf("female", "male")));
            switch(nextGender) {
                case "female":
                    femaleCount++;
                    break;
                case "male":
                    maleCount++;
                    break;
            }
            weightedGender.reset();
        }
        assertThat(femaleCount, is(equalTo(60)));
        assertThat(maleCount, is(equalTo(40)));
        assertThrows(ExactWeightedValue.ExactWeightedValueDepletedException.class, weightedGender::get); //all 100 values are depleted, so expect Exception on next get()
    }

    @Test
    void testExactWeightedValueVariableSources() {
        DiscreteValue<String> randomFemaleName = new DiscreteValue<>(Arrays.asList(ConstantValue.of("Emma"), ConstantValue.of("Sofia")));
        DiscreteValue<String> randomMaleName = new DiscreteValue<>(Arrays.asList(ConstantValue.of("Alfred"), ConstantValue.of("Bruce")));
        ExactWeightedValue<String> value = new ExactWeightedValue<>(Arrays.asList(new ExactWeightedValue.CountValuePair<>(randomFemaleName, 20), new ExactWeightedValue.CountValuePair<>(randomMaleName, 30)));

        int femaleNameCount = 0;
        int maleNameCount = 0;
        for (int i = 1; i <= 50; i++) {
            String nextName = value.get();
            assertThat(nextName, is(oneOf("Emma", "Sofia", "Alfred", "Bruce")));
            if (nextName.equals("Emma") || nextName.equals("Sofia")) { femaleNameCount++; }
            else if (nextName.equals("Alfred") || nextName.equals("Bruce")) { maleNameCount++; }
            value.reset();
        }
        assertThat(femaleNameCount, is(equalTo(20)));
        assertThat(maleNameCount, is(equalTo(30)));
        assertThrows(ExactWeightedValue.ExactWeightedValueDepletedException.class, value::get); //all 50 values are depleted, so expect Exception on next get()
    }


    @Test
    void testErrorConstructWithNullArgument() {
        assertThrows(ValueException.class, () -> new ExactWeightedValue.CountValuePair<>(null, 1));
        assertThrows(ValueException.class, () -> new ExactWeightedValue.CountValuePair<>(ConstantValue.of("value"), -1)); //negative count
        assertThrows(ValueException.class, () -> new ExactWeightedValue<>(null));
        assertThrows(ValueException.class, () -> new ExactWeightedValue<>(Collections.emptyList())); //empty list
    }

}