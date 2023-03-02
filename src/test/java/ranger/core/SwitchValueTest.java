package ranger.core;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SwitchValueTest {

    @Test
    void testSwitchValue() {
        CircularValue<String> circularUsState = new CircularValue<>(Arrays.asList(ConstantValue.of("AL"), ConstantValue.of("AZ"), ConstantValue.of("CA"), ConstantValue.of("DE"), ConstantValue.of("NY")));
        SwitchValue<String> UsStateLong = new SwitchValue<>(circularUsState, Arrays.asList(ConstantValue.of("Alabama"), ConstantValue.of("Arizona"), ConstantValue.of("California"), ConstantValue.of("Delaware"), ConstantValue.of("New York")));
        assertThat(UsStateLong.get(), is(equalTo("Alabama")));  //AL
        UsStateLong.reset();
        assertThat(UsStateLong.get(), is(equalTo("Arizona")));  //AZ
        UsStateLong.reset();
        assertThat(UsStateLong.get(), is(equalTo("California")));   //CA
        UsStateLong.reset();
        assertThat(UsStateLong.get(), is(equalTo("Delaware")));   //DE
        UsStateLong.reset();
        assertThat(UsStateLong.get(), is(equalTo("New York")));   //NY
        UsStateLong.reset();
        assertThat(UsStateLong.get(), is(equalTo("Alabama")));  //AL
    }

    @Test
    void testErrorConstructWithNullArgument() {
        assertThrows(ValueException.class, () -> new SwitchValue<>(null, null));
        assertThrows(ValueException.class, () -> new SwitchValue<>(new DiscreteValue<>(Collections.singletonList(ConstantValue.of("source"))), null));
        assertThrows(ValueException.class, () -> new SwitchValue<>(null, Arrays.asList(ConstantValue.of("firstDependent"), ConstantValue.of("secondDependent"))));
    }

    @Test
    void testErrorWrongListSize() {
        assertThrows(ValueException.class, () -> new SwitchValue<>(new DiscreteValue<>(Collections.singletonList(ConstantValue.of("source"))), Arrays.asList(ConstantValue.of("firstDependent"), ConstantValue.of("secondDependent"))));    // 1 vs 2
        assertThrows(ValueException.class, () -> new SwitchValue<>(new DiscreteValue<>(Arrays.asList(ConstantValue.of("firstSource"), ConstantValue.of("secondSource"))), Collections.singletonList(ConstantValue.of("dependent"))));    // 2 vs 1
    }
    
}