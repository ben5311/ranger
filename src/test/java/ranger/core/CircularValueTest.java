package ranger.core;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CircularValueTest {

    @Test
    void testCircularValueConstantSources() {
        CircularValue<String> circularOrdinal = new CircularValue<>(Arrays.asList(ConstantValue.of("first"), ConstantValue.of("second"), ConstantValue.of("third")));
        assertThat(circularOrdinal.get(), is(equalTo("first")));
        circularOrdinal.reset();
        assertThat(circularOrdinal.get(), is(equalTo("second")));
        circularOrdinal.reset();
        assertThat(circularOrdinal.get(), is(equalTo("third")));
        circularOrdinal.reset();
        assertThat(circularOrdinal.get(), is(equalTo("first")));
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    void testCircularValueVariableSources() {
        CircularValue<String> circularOrdinal = new CircularValue<>(Arrays.asList(ConstantValue.of("first"), ConstantValue.of("second"), ConstantValue.of("third")));
        CircularValue<Integer> circularNumber = new CircularValue<>(Arrays.asList(ConstantValue.of(1), ConstantValue.of(2), ConstantValue.of(3)));
        CircularValue<?> circularValue = new CircularValue(Arrays.asList(circularOrdinal, circularNumber));
        assertThat(circularValue.get(), is(equalTo("first")));
        circularValue.reset();
        assertThat(circularValue.get(), is(equalTo(1)));
        circularValue.reset();
        assertThat(circularValue.get(), is(equalTo("second")));
        circularValue.reset();
        assertThat(circularValue.get(), is(equalTo(2)));
        circularValue.reset();
        assertThat(circularValue.get(), is(equalTo("third")));
        circularValue.reset();
        assertThat(circularValue.get(), is(equalTo(3)));
        circularValue.reset();
        assertThat(circularValue.get(), is(equalTo("first")));
        circularValue.reset();
        assertThat(circularValue.get(), is(equalTo(1)));
    }

    @Test
    void testErrorConstructWithNullArgument() {
        assertThrows(ValueException.class, () -> new CircularValue<>(null));
        assertThrows(ValueException.class, () -> new CircularValue<>(Collections.emptyList())); //empty list
    }

}