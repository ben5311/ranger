package ranger.core;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CaseTransformerTest {

    @Test
    void testCaseTransformerConstantSource() {
       CaseTransformer lowerCaseTransformer = new CaseTransformer(ConstantValue.of("Lorem Ipsum Dolor sit AMET."), false);
       CaseTransformer upperCaseTransformer = new CaseTransformer(ConstantValue.of("Lorem Ipsum Dolor sit AMET."), true);
       assertThat(lowerCaseTransformer.get(), is(equalTo("lorem ipsum dolor sit amet.")));
       assertThat(upperCaseTransformer.get(), is(equalTo("LOREM IPSUM DOLOR SIT AMET.")));
       lowerCaseTransformer.reset();
       upperCaseTransformer.reset();
        assertThat(lowerCaseTransformer.get(), is(equalTo("lorem ipsum dolor sit amet.")));
        assertThat(upperCaseTransformer.get(), is(equalTo("LOREM IPSUM DOLOR SIT AMET.")));
    }

    @Test
    void testCaseTransformerVariableSource() {
        CircularValue<String> sourceStringGenerator = new CircularValue<>(Arrays.asList(ConstantValue.of("First"), ConstantValue.of("Second")));
        CaseTransformer lowerCaseTransformer = new CaseTransformer(sourceStringGenerator, false);
        CaseTransformer upperCaseTransformer = new CaseTransformer(sourceStringGenerator, true);
        assertThat(lowerCaseTransformer.get(), is(equalTo("first")));
        assertThat(upperCaseTransformer.get(), is(equalTo("FIRST")));
        lowerCaseTransformer.reset();
        upperCaseTransformer.reset();
        assertThat(lowerCaseTransformer.get(), is(equalTo("second")));
        assertThat(upperCaseTransformer.get(), is(equalTo("SECOND")));
    }

    @Test
    void testErrorConstructWithNullArgument() {
        assertThrows(ValueException.class, () -> new CaseTransformer(null, false));
    }

}