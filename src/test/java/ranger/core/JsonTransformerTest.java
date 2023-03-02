package ranger.core;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonTransformerTest {

    @Test
    void testJsonTransformerConstantSource() {
        CompositeValue source = new CompositeValue(Collections.singletonMap("name", ConstantValue.of("Jonas")));
        JsonTransformer transformer = new JsonTransformer(source);
        assertThat(transformer.get(), is(equalTo("{\"name\":\"Jonas\"}")));
        transformer.reset();
        assertThat(transformer.get(), is(equalTo("{\"name\":\"Jonas\"}")));
    }

    @Test
    void testGetterTransformerVariableSource() {
        CompositeValue source = new CompositeValue(Collections.singletonMap("name", new CircularValue<>(Arrays.asList(ConstantValue.of("Jonas"), ConstantValue.of("Lisa")))));
        JsonTransformer transformer = new JsonTransformer(source);
        assertThat(transformer.get(), is(equalTo("{\"name\":\"Jonas\"}")));
        transformer.reset();
        assertThat(transformer.get(), is(equalTo("{\"name\":\"Lisa\"}")));
        transformer.reset();
        assertThat(transformer.get(), is(equalTo("{\"name\":\"Jonas\"}")));
    }


    @Test
    void testErrorConstructWithNullArgument() {
        assertThrows(ValueException.class, () -> new JsonTransformer(null));
        assertThrows(ValueException.class, () -> new JsonTransformer(ConstantValue.of("value"), null));
    }

}