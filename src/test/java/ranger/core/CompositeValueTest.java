package ranger.core;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CompositeValueTest {

    @Test
    void testCompositeValueConstantSources() {
        Map<String, Value<?>> valueMap = new HashMap<>();
        valueMap.put("country", ConstantValue.of("Germany"));
        valueMap.put("capital", ConstantValue.of("Berlin"));

        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("country", "Germany");
        expectedMap.put("capital", "Berlin");

        CompositeValue value = new CompositeValue(valueMap);
        assertThat(value.get(), is(equalTo(expectedMap)));
        value.reset();
        assertThat(value.get(), is(equalTo(expectedMap)));
    }

    @Test
    void testCompositeValueVariableSources() {
        CircularValue<String> circularCountry = new CircularValue<>(Arrays.asList(ConstantValue.of("Germany"), ConstantValue.of("France"), ConstantValue.of("Italy")));
        CircularValue<String> circularCapital = new CircularValue<>(Arrays.asList(ConstantValue.of("Berlin"), ConstantValue.of("Paris"), ConstantValue.of("Rome")));
        Map<String, Value<?>> valueMap = new HashMap<>();
        valueMap.put("country", circularCountry);
        valueMap.put("capital", circularCapital);
        CompositeValue compositeValue = new CompositeValue(valueMap);

        Map<String, ?> generatedMap = compositeValue.get();
        assertThat(generatedMap.get("country"), is(equalTo("Germany")));
        assertThat(generatedMap.get("capital"), is(equalTo("Berlin")));
        compositeValue.reset();
        generatedMap = compositeValue.get();
        assertThat(generatedMap.get("country"), is(equalTo("France")));
        assertThat(generatedMap.get("capital"), is(equalTo("Paris")));
        compositeValue.reset();
        generatedMap = compositeValue.get();
        assertThat(generatedMap.get("country"), is(equalTo("Italy")));
        assertThat(generatedMap.get("capital"), is(equalTo("Rome")));
        compositeValue.reset();
        generatedMap = compositeValue.get();
        assertThat(generatedMap.get("country"), is(equalTo("Germany")));
        assertThat(generatedMap.get("capital"), is(equalTo("Berlin")));
        compositeValue.reset();
    }


    @Test
    void testMergeCompositeValues() {
        //Given
        Map<String, Value<?>> firstnameMap = new LinkedHashMap<>();
        firstnameMap.put("firstname", new ConstantValue<>("Max"));

        Map<String, Value<?>> lastnameJobMap = new LinkedHashMap<>();
        lastnameJobMap.put("lastname", new ConstantValue<>("Mustermann"));
        lastnameJobMap.put("job", new ConstantValue<>("teacher"));

        CompositeValue c1 = new CompositeValue(firstnameMap);
        CompositeValue c2 = new CompositeValue(lastnameJobMap);

        Map<String, String> expectedMap = new LinkedHashMap<>();
        expectedMap.put("firstname", "Max");
        expectedMap.put("lastname", "Mustermann");
        expectedMap.put("job", "teacher");

        //When
        CompositeValue mergedComposite = new CompositeValue(Arrays.asList(c1, c2));

        //Then
        Map<String, Object> mergedMap = mergedComposite.get();
        assertThat(mergedMap, is(equalTo(expectedMap)));
    }

    @Test
    void testErrorConstructWithNullArgument() {
        assertThrows(ValueException.class, () -> new CompositeValue((Map<String, Value<?>>) null));
        assertThrows(ValueException.class, () -> new CompositeValue((List<Composite<?>>) null));
        assertThrows(ValueException.class, () -> new CompositeValue(Collections.emptyList()));
    }

}
