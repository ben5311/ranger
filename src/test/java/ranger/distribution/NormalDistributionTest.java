package ranger.distribution;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NormalDistributionTest {

    private final NormalDistribution distribution = new NormalDistribution(20, 5, 15, 25);      //these settings should not change te test results

    @Test
    void testNextIntFromZero() {
        for (int i = 0; i < 100; i++) {
            int nextInt = distribution.nextInt(10);
            assertThat(nextInt, is(both(greaterThanOrEqualTo(0)).and(lessThan(10))));
        }
    }

    @Test
    void testNextInt() {
        for (int i = 0; i < 100; i++) {
            int nextInt = distribution.nextInt(-10, 10);
            assertThat(nextInt, is(both(greaterThanOrEqualTo(-10)).and(lessThan(10))));
        }
    }

    @Test
    void testNextLongFromZero() {
        for (int i = 0; i < 100; i++) {
            long nextLong = distribution.nextLong(10L);
            assertThat(nextLong, is(both(greaterThanOrEqualTo(0L)).and(lessThan(10L))));
        }
    }

    @Test
    void testNextLong() {
        for (int i = 0; i < 100; i++) {
            long nextLong = distribution.nextLong(-10L, 10L);
            assertThat(nextLong, is(both(greaterThanOrEqualTo(-10L)).and(lessThan(10L))));
        }
    }

    @Test
    void testNextDouble() {
        for (int i = 0; i < 100; i++) {
            double nextDouble = distribution.nextDouble(-10.0, 10.0);
            assertThat(nextDouble, is(both(greaterThanOrEqualTo(-10.0)).and(lessThan(10.0))));
        }
    }

    @Test
    void testNextBoolean() {
        for (int i = 0; i < 100; i++) {
            boolean nextBoolean = distribution.nextBoolean();
        }
    }

    @Test
    void testErrorConstructWithMeanOutsideRange() {
        assertThrows(ArithmeticException.class, () -> new NormalDistribution(10, 2, 1, 5));
    }
}