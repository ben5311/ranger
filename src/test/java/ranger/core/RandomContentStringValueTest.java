package ranger.core;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RandomContentStringValueTest {

    @Test
    void testRandomContentStringValueDefaultChars() {
        RandomContentStringValue randomString = new RandomContentStringValue(ConstantValue.of(3));
        for (int i = 0; i < 100; i++) {
            assertThat(randomString.get(), matchesPattern("[a-zA-Z0-9]{3}"));
            randomString.reset();
        }
    }

    @Test
    void testRandomContentStringValueCustomChars() {
        RandomContentStringValue randomString = new RandomContentStringValue(ConstantValue.of(3), Arrays.asList(new Range<>('a', 'b'), new Range<>('o', 'z')));
        for (int i = 0; i < 100; i++) {
            assertThat(randomString.get(), matchesPattern("[a-bo-z]{3}"));
            randomString.reset();
        }
    }

    @Test
    void testRandomContentStringValueVariableLength() {
        RandomContentStringValue randomString = new RandomContentStringValue(new RangeValueInt(new Range<>(1, 5)));
        for (int i = 0; i < 100; i++) {
            assertThat(randomString.get(), matchesPattern("[a-zA-Z0-9]{1,4}"));
            randomString.reset();
        }
    }

    @Test
    void testErrorConstructWithNullArgument() {
        assertThrows(ValueException.class, () -> new RandomContentStringValue(null));
        assertThrows(ValueException.class, () -> new RandomContentStringValue(null, null));
        assertThrows(ValueException.class, () -> new RandomContentStringValue(null, Collections.emptyList()));
    }

}