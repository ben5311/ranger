package ranger.core;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AsciiTransformerTest {

    @Test
    void testAsciiTransformerConstantSource() {
       AsciiTransformer asciiTransformer = new AsciiTransformer(ConstantValue.of("Zwölf Boxkämpfer jagen Viktor quer über den großen Sylter Deich"));
       assertThat(asciiTransformer.get(), is(equalTo("Zwoelf Boxkaempfer jagen Viktor quer ueber den grossen Sylter Deich")));
       asciiTransformer.reset();
       assertThat(asciiTransformer.get(), is(equalTo("Zwoelf Boxkaempfer jagen Viktor quer ueber den grossen Sylter Deich")));
    }

    @Test
    void testAsciiTransformerVariableSource() {
        CircularValue<String> sourceStringGenerator = new CircularValue<>(Arrays.asList(ConstantValue.of("Zwölf Boxkämpfer jagen Viktor quer über den großen Sylter Deich"), ConstantValue.of("Alte Würzburger Straße")));
        AsciiTransformer asciiTransformer = new AsciiTransformer(sourceStringGenerator);
        assertThat(asciiTransformer.get(), is(equalTo("Zwoelf Boxkaempfer jagen Viktor quer ueber den grossen Sylter Deich")));
        asciiTransformer.reset();
        assertThat(asciiTransformer.get(), is(equalTo("Alte Wuerzburger Strasse")));
    }

    @Test
    void testErrorConstructWithNullArgument() {
        assertThrows(ValueException.class, () -> new AsciiTransformer(null));
    }

}