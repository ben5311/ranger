package ranger;

import org.junit.jupiter.api.Test;
import ranger.core.CircularRangeValueInt;
import ranger.core.CompositeValue;
import ranger.core.ConstantValue;
import ranger.core.Range;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ObjectGeneratorTest {

    @Test
    /*
     * test if generate() produces equal results as when generating one object after another
     */
    void testGenerate() {
        ObjectGenerator<Map<String, Object>> firstGenerator = new ObjectGenerator<>(new CompositeValue(Collections.singletonMap("number", new CircularRangeValueInt(new Range<>(1, 100), 1))));
        ObjectGenerator<Map<String, Object>> secondGenerator = new ObjectGenerator<>(new CompositeValue(Collections.singletonMap("number", new CircularRangeValueInt(new Range<>(1, 100), 1))));
        List<Map<String, Object>> generatedObjects = firstGenerator.generate(200);
        for (int i = 0; i <= 199; i++) {
            assertThat(generatedObjects.get(i), is(equalTo(secondGenerator.next())));
        }
    }

    @Test
    void testErrorGenerateNegativeCount() {
        ObjectGenerator<String> generator = new ObjectGenerator<>(ConstantValue.of("value"));
        assertThrows(IllegalArgumentException.class, () -> generator.generate(-1));
    }

}