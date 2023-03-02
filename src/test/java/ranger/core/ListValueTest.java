package ranger.core;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ListValueTest {

    @Test
    void testListValueConstantSources() {
        ListValue<String> bankWithCountry = new ListValue<>(Arrays.asList(ConstantValue.of("Deutsche Bank"), ConstantValue.of("Germany")));

        assertThat(bankWithCountry.get(), is(equalTo(Arrays.asList("Deutsche Bank", "Germany"))));
        bankWithCountry.reset();
        assertThat(bankWithCountry.get(), is(equalTo(Arrays.asList("Deutsche Bank", "Germany"))));
    }

    @Test
    void testListValueVariableSources() {
        CircularValue<String> circularBank = new CircularValue<>(Arrays.asList(ConstantValue.of("Deutsche Bank"), ConstantValue.of("Santander"), ConstantValue.of("JPMorgan")));
        CircularValue<String> circularCountry = new CircularValue<>(Arrays.asList(ConstantValue.of("Germany"), ConstantValue.of("Spain"), ConstantValue.of("USA")));
        ListValue<String> bankWithCountry = new ListValue<>(Arrays.asList(circularBank, circularCountry));

        assertThat(bankWithCountry.get(), is(equalTo(Arrays.asList("Deutsche Bank", "Germany"))));
        bankWithCountry.reset();
        assertThat(bankWithCountry.get(), is(equalTo(Arrays.asList("Santander", "Spain"))));
        bankWithCountry.reset();
        assertThat(bankWithCountry.get(), is(equalTo(Arrays.asList("JPMorgan", "USA"))));
        bankWithCountry.reset();
        assertThat(bankWithCountry.get(), is(equalTo(Arrays.asList("Deutsche Bank", "Germany"))));
        bankWithCountry.reset();
    }

    @Test
    void testErrorConstructWithNullArgument() {
        assertThrows(ValueException.class, () -> new ListValue<>(null));
        assertThrows(ValueException.class, () -> new ListValue<>(Collections.emptyList()));
    }

}