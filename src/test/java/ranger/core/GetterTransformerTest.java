package ranger.core;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetterTransformerTest {


    @Test
    void testGetterTransformerConstantSource() {
        CompositeValue source = new CompositeValue(Collections.singletonMap("name", ConstantValue.of("Jonas")));
        GetterTransformer<String> transformer = new GetterTransformer<>("name", String.class, source);
        assertThat(transformer.get(), is(equalTo("Jonas")));
        transformer.reset();
        assertThat(transformer.get(), is(equalTo("Jonas")));
    }

    @Test
    void testGetterTransformerVariableSource() {
        CompositeValue source = new CompositeValue(Collections.singletonMap("name", new CircularValue<>(Arrays.asList(ConstantValue.of("Jonas"), ConstantValue.of("Lena")))));
        GetterTransformer<String> transformer = new GetterTransformer<>("name", String.class, source);
        assertThat(transformer.get(), is(equalTo("Jonas")));
        transformer.reset();
        assertThat(transformer.get(), is(equalTo("Lena")));
        transformer.reset();
        assertThat(transformer.get(), is(equalTo("Jonas")));
        transformer.reset();
    }


    @Test
    void testErrorConstructWithNullArgument() {
        assertThrows(ValueException.class, () -> new GetterTransformer<>(null, null, null));
        assertThrows(ValueException.class, () -> new GetterTransformer<>("", String.class, new CompositeValue(Collections.singletonMap("name", ConstantValue.of("Jonas"))))); //empty key
        assertThrows(ValueException.class, () -> new GetterTransformer<>("key", String.class, new ConstantValue<>("value"))); //source not of type Composite
    }

}