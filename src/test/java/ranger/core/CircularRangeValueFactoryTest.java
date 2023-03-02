package ranger.core;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("CanBeFinal")
class CircularRangeValueFactoryTest {
    private static CircularRangeValueFactory factory = new CircularRangeValueFactory();

    @Test
    void testCreate() {
        assertThat(factory.create(new Range<>((byte) 1, (byte) 10), (byte) 1), is(instanceOf(CircularRangeValueByte.class)));
        assertThat(factory.create(new Range<>('a', 'z'), 1), is(instanceOf(CircularRangeValueChar.class)));
        assertThat(factory.create(new Range<>(1.0, 10.0), 0.5), is(instanceOf(CircularRangeValueDouble.class)));
        assertThat(factory.create(new Range<>(1.0f, 10.0f), 0.5f), is(instanceOf(CircularRangeValueFloat.class)));
        assertThat(factory.create(new Range<>(1, 10), 1), is(instanceOf(CircularRangeValueInt.class)));
        assertThat(factory.create(new Range<>(1L, 10L), 1L), is(instanceOf(CircularRangeValueLong.class)));
        assertThat(factory.create(new Range<>((short) 1, (short) 10), (short) 1), is(instanceOf(CircularRangeValueShort.class)));
    }

    @Test
    void testErrorCallWithNullArgument() {
        assertThrows(ValueException.class, () -> factory.create(null, null));
        assertThrows(ValueException.class, () -> factory.create(new Range<>(1, 2), null));
        assertThrows(ValueException.class, () -> factory.create(null, 1));
        assertThrows(ValueException.class, () -> factory.create(new Range<>(1, 2), 1.0));   //type mismatch
    }
}