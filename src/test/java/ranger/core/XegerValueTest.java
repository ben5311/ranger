package ranger.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertThrows;

class XegerValueTest {

    @Test
    void testXegerValueSimplePattern() {
        ConstantValue<String> germanIbanPattern = ConstantValue.of("DE[0-9]{20}");
        XegerValue randomGermanIban = new XegerValue(germanIbanPattern);
        for (int i = 0; i < 1000; i++) {
            assertThat(randomGermanIban.get(), matchesPattern("DE[0-9]{20}"));
            randomGermanIban.reset();
        }
    }

    @Test
    void testXegerValueComplexPattern() {
        ConstantValue<String> emailPattern = ConstantValue.of("[\\p{Alpha}&&[a-z]]{6,12}(\\.[a-z\\d]{2,4})?@[a-z]{4,10}\\.(com|de|gb|pl|info)");
        XegerValue randomEmail = new XegerValue(emailPattern);
        for (int i = 0; i < 1000; i++) {
            assertThat(randomEmail.get(), matchesPattern("[a-z]{6,12}(\\.[a-z\\d]{2,4})?@[a-z]{4,10}\\.(com|de|gb|pl|info)"));
            randomEmail.reset();
        }
    }

    @Test
    void testXegerValueVariablePattern() {
        DiscreteValue<String> ibanPattern = new DiscreteValue<>(Arrays.asList(ConstantValue.of("DE[0-9]{20}"), ConstantValue.of("FR\\d{10}[A-Z\\d]{11}\\d{2}")));
        XegerValue randomIban = new XegerValue(ibanPattern);
        for (int i = 0; i < 1000; i++) {
            assertThat(randomIban.get(), matchesPattern("(DE[0-9]{20})|(FR\\d{10}[A-Z\\d]{11}\\d{2})"));
            randomIban.reset();
        }
    }

    @Test
    void testErrorConstructWithNullArgument() {
        assertThrows(ValueException.class, () -> new XegerValue(null));
        XegerValue emptyXeger = new XegerValue(ConstantValue.of(""));
        assertThrows(ValueException.class, emptyXeger::get);
    }

    @Test
    void testErrorInvalidRegex() {
        assertThrows(XegerValue.RegexException.class, () -> new XegerValue(ConstantValue.of("[0-9]+*?")).get());
    }

    @Test
    void testErrorNestedClassRegex() {
        assertThrows(XegerValue.RegexException.class, () -> new XegerValue(ConstantValue.of("[0-9[A-Z]]")).get());
    }

    @ParameterizedTest
    @ValueSource(strings = {"\\D", "\\S", "\\W"})
    void testErrorPredefinedNegatedInsideClassRegex(String negatedCharClass) {
        assertThrows(XegerValue.RegexException.class, () -> new XegerValue(ConstantValue.of("[0-9"+negatedCharClass+"]")).get());
    }

    @ParameterizedTest
    @ValueSource(strings = {"\\b", "\\B", "\\A", "\\G", "\\Z", "\\z"})
    void testErrorBoundaryMatcherRegex(String boundaryMatcher) {
        assertThrows(XegerValue.RegexException.class, () -> new XegerValue(ConstantValue.of("[0-9]"+boundaryMatcher+"\\d")).get());
    }

    @ParameterizedTest
    @ValueSource(strings = {"??", "*?", "+?", "}?"})
    void testErrorReluctantQuantifierRegex(String reluctantQuantifier) {
        assertThrows(XegerValue.RegexException.class, () -> new XegerValue(ConstantValue.of("[0-9]"+reluctantQuantifier+"\\d")).get());
    }

    @ParameterizedTest
    @ValueSource(strings = {"?+", "*+", "++", "}+"})
    void testErrorPossessiveQuantifierRegex(String possessiveQuantifier) {
        assertThrows(XegerValue.RegexException.class, () -> new XegerValue(ConstantValue.of("[0-9]"+possessiveQuantifier+"\\d")).get());
    }

    @Test
    void testErrorUnclosedPosixClassRegex() {
        assertThrows(XegerValue.RegexException.class, () -> new XegerValue(ConstantValue.of("[0-9\\p{Alpha]")).get());
    }

    @Test
    void testErrorInvalidPosixClassRegex() {
        assertThrows(XegerValue.RegexException.class, () -> new XegerValue(ConstantValue.of("[0-9\\p{ALPHA}]")).get());
    }
    
}