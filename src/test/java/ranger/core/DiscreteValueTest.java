package ranger.core;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.oneOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DiscreteValueTest {

    @Test
    void testDiscreteValueConstantSources() {
        DiscreteValue<String> randomName = new DiscreteValue<>(Arrays.asList(ConstantValue.of("Max"), ConstantValue.of("Peter"), ConstantValue.of("Paul")));
        for (int i = 1; i <= 4; i++) {
            assertThat(randomName.get(), is(oneOf("Max", "Peter", "Paul")));
            randomName.reset();
        }
    }

    @Test
    void testDiscreteValueVariableSources() {
        DiscreteValue<String> randomFemaleFirstname = new DiscreteValue<>(Arrays.asList(ConstantValue.of("Max"), ConstantValue.of("Peter")));
        DiscreteValue<String> randomMaleFirstname = new DiscreteValue<>(Arrays.asList(ConstantValue.of("Anna"), ConstantValue.of("Lisa")));
        DiscreteValue<String> randomName = new DiscreteValue<>(Arrays.asList(randomFemaleFirstname, randomMaleFirstname));
        for (int i = 1; i <= 10; i++) {
            assertThat(randomName.get(), is(oneOf("Max", "Peter", "Anna", "Lisa")));
            randomName.reset();
        }
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    void testErrorConstructWithNullArgument() {
        assertThrows(ValueException.class, () -> new DiscreteValue<>((List) null));
        assertThrows(ValueException.class, () -> new DiscreteValue<>(Collections.emptyList())); //empty list
        assertThrows(ValueException.class, () -> new DiscreteValue<>(Collections.singletonList(ConstantValue.of(1)), null));
    }

}