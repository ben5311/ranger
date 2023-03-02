package ranger.core;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MapperValueTest {

    @Test
    void testMapperValueConstantSource() {
      MapperValue<String> mapperValue = new MapperValue<>(ConstantValue.of("source value"), Collections.singletonList(new MapperValue.KeyValuePair<>(ConstantValue.of("source value"), ConstantValue.of("dependent value"))));
      assertThat(mapperValue.get(), is(equalTo("dependent value")));
      mapperValue.reset();
      assertThat(mapperValue.get(), is(equalTo("dependent value")));
    }

    @Test
    void testMapperValueVariableSource() {
        CircularValue<String> circularUsState = new CircularValue<>(Arrays.asList(ConstantValue.of("AL"), ConstantValue.of("AZ"), ConstantValue.of("CA"), ConstantValue.of("DE"), ConstantValue.of("NY")));
        MapperValue<String> UsStateLong = new MapperValue<>(circularUsState, Arrays.asList(new MapperValue.KeyValuePair<>(ConstantValue.of("AL"), ConstantValue.of("Alabama")), new MapperValue.KeyValuePair<>(ConstantValue.of("AZ"), ConstantValue.of("Arizona")),
                                                                                         new MapperValue.KeyValuePair<>(ConstantValue.of("CA"), ConstantValue.of("California")), new MapperValue.KeyValuePair<>(ConstantValue.of("default"), ConstantValue.of("NULL"))));
        assertThat(UsStateLong.get(), is(equalTo("Alabama")));  //AL
        UsStateLong.reset();
        assertThat(UsStateLong.get(), is(equalTo("Arizona")));  //AZ
        UsStateLong.reset();
        assertThat(UsStateLong.get(), is(equalTo("California")));   //CA
        UsStateLong.reset();
        assertThat(UsStateLong.get(), is(equalTo("NULL")));   //DE; fallback value
        UsStateLong.reset();
        assertThat(UsStateLong.get(), is(equalTo("NULL")));   //NY; fallback value
        UsStateLong.reset();
        assertThat(UsStateLong.get(), is(equalTo("Alabama")));  //AL
    }

    @Test
    void testMapperValueKeyNotDefined() {
        MapperValue<String> mapperValue = new MapperValue<>(ConstantValue.of("source value"), Collections.singletonList(new MapperValue.KeyValuePair<>(ConstantValue.of("other value"), ConstantValue.of("dependent value"))));
        assertThrows(ValueException.class, mapperValue::get);
    }

    @Test
    void testErrorConstructWithNullArgument() {
        assertThrows(ValueException.class, () -> new MapperValue.KeyValuePair<>(null, null));
        assertThrows(ValueException.class, () -> new MapperValue.KeyValuePair<>(ConstantValue.of("key"), null));
        assertThrows(ValueException.class, () -> new MapperValue.KeyValuePair<>(null, ConstantValue.of("value")));
        assertThrows(ValueException.class, () -> new MapperValue.KeyValuePair<>(new DiscreteValue<>(Arrays.asList(ConstantValue.of("key1"), ConstantValue.of("key2"))), ConstantValue.of("value")));   //key is not constant

        assertThrows(ValueException.class, () -> new MapperValue<>(null, null));
        assertThrows(ValueException.class, () -> new MapperValue<>(ConstantValue.of("value"), null));
        assertThrows(ValueException.class, () -> new MapperValue<>(ConstantValue.of("value"), Collections.emptyList()));
        assertThrows(ValueException.class, () -> new MapperValue<>(null, Collections.singletonList(new MapperValue.KeyValuePair<>(ConstantValue.of("key"), ConstantValue.of("value")))));
    }
}