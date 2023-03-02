package ranger.core;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;

class UUIDValueTest {

    @Test
    void testUUIDValue() {
        UUIDValue uuidValue = new UUIDValue();
        for (int i = 1; i < 10; i++) {
            assertThat(uuidValue.get(), matchesPattern("\\b[0-9a-f]{8}\\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\\b[0-9a-f]{12}\\b"));
            uuidValue.reset();
        }
    }

}