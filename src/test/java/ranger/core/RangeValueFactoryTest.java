package ranger.core;

import org.junit.jupiter.api.Test;
import ranger.distribution.UniformDistribution;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RangeValueFactoryTest {

    private static final RangeValueFactory factory = new RangeValueFactory();

    @Test
    void testCreate() {
        assertThat(factory.create(new Range<>((byte) 1, (byte) 10), false, new UniformDistribution()), is(instanceOf(RangeValueByte.class)));
        assertThat(factory.create(new Range<>('a', 'z'), false, new UniformDistribution()), is(instanceOf(RangeValueChar.class)));
        assertThat(factory.create(new Range<>(1.0, 10.0), false, new UniformDistribution()), is(instanceOf(RangeValueDouble.class)));
        assertThat(factory.create(new Range<>(1.0f, 10.0f), false, new UniformDistribution()), is(instanceOf(RangeValueFloat.class)));
        assertThat(factory.create(new Range<>(1, 10), false, new UniformDistribution()), is(instanceOf(RangeValueInt.class)));
        assertThat(factory.create(new Range<>(1L, 10L), false, new UniformDistribution()), is(instanceOf(RangeValueLong.class)));
        assertThat(factory.create(new Range<>((short) 1, (short) 10), false, new UniformDistribution()), is(instanceOf(RangeValueShort.class)));
        assertThat(factory.create(new Range<>(new Date(2389232424L), new Date()), false, new UniformDistribution()), is(instanceOf(RangeValueDate.class)));
    }

    @Test
    void testErrorCallWithNullArgument() {
        assertThrows(ValueException.class, () -> factory.create(null, false,new UniformDistribution()));
        assertThrows(ValueException.class, () -> factory.create(new Range<>("beginning", "end"), false, new UniformDistribution()));   //range type not supported
    }

}