package ranger.util;

import org.junit.jupiter.api.Test;
import ranger.parser.ConfigException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ranger.util.YamlUtils.*;

class YamlUtilsTest {

    @Test
    void testLoadFromRawYaml() {
        //Given
        String rawYaml = "values:\n" +
                         "  name: Marcus\n" +
                         "  age: 18";
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("name", "Marcus");
        expectedValues.put("age", 18);
        Map<String, Object> expectedMap = Collections.singletonMap("values", expectedValues);
        //When
        Object loaded = YamlUtils.load(rawYaml);
        Object loadedWithPath = YamlUtils.load(rawYaml, "$.");
        //Then
        assertThat(loaded, is(equalTo(expectedMap)));
        assertThat(loadedWithPath, is(equalTo(expectedMap)));
    }

    @Test
    void testLoadFromInputStream() {
        String rawYaml = "values:\n" +
                "  name: Marcus\n" +
                "  age: 18";
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("name", "Marcus");
        expectedValues.put("age", 18);
        Map<String, Object> expectedMap = Collections.singletonMap("values", expectedValues);
        //When
        Object loaded = YamlUtils.load(new ByteArrayInputStream(rawYaml.getBytes()));
        Object loadedWithPath = YamlUtils.load(new ByteArrayInputStream(rawYaml.getBytes()), "$.");
        //Then
        assertThat(loaded, is(equalTo(expectedMap)));
        assertThat(loadedWithPath, is(equalTo(expectedMap)));
    }

    @Test
    void testErrorLoadWithNullArgument() {
        assertThrows(IllegalArgumentException.class, () -> YamlUtils.load((String) null));
        assertThrows(IllegalArgumentException.class, () -> YamlUtils.load((InputStream) null));
    }

    @Test
    void testErrorLoadEmptyYaml() {
        assertThrows(IllegalArgumentException.class, () -> YamlUtils.load(""));
        assertThrows(IllegalArgumentException.class, () -> YamlUtils.load("", "$."));
        assertThrows(RuntimeException.class, () -> YamlUtils.load(this.getClass().getResourceAsStream("invalid_empty_file.yaml")));
        assertThrows(RuntimeException.class, () -> YamlUtils.load(this.getClass().getResourceAsStream("invalid_empty_file.yaml"), "$."));
    }


    //HELPER METHODS

    @Test
    void testStripOffDollarSign() {
        assertThat(stripOffDollarSign("$.content.root"), is(equalTo("content.root")));
        assertThat(stripOffDollarSign("$."), is(equalTo("")));
        assertThat(stripOffDollarSign(""), is(equalTo("")));
        assertThat(stripOffDollarSign(null), is(equalTo("")));
    }


    //test getSection()
    @Test
    void testGetSectionFromFlatMap() {
        Map<String, Object> testMap = Collections.singletonMap("text", "Lorem ipsum");
        Object lorem = YamlUtils.getSection(testMap, "$.text");
        assertThat(lorem, is(equalTo("Lorem ipsum")));
    }

    @Test
    void testGetSectionFromNestedMap() {
        Map<String, Object> innerMap = Collections.singletonMap("text", "Lorem ipsum");
        Map<String, Object> rootMap = Collections.singletonMap("innerMap", innerMap);
        Object lorem = YamlUtils.getSection(rootMap, "$.innerMap.text");
        assertThat(lorem, is(equalTo("Lorem ipsum")));
    }

    @Test
    void testGetSectionFromNestedList() {
        List<String> names = Arrays.asList("Max", "Klaus");
        Map<String, Object> rootMap = Collections.singletonMap("names", names);
        assertThat(YamlUtils.getSection(rootMap, "$.names[0]"), is(equalTo("Max")));
        assertThat(YamlUtils.getSection(rootMap, "$.names[1]"), is(equalTo("Klaus")));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> YamlUtils.getSection(rootMap, "$.names[2]"));
    }

    @Test
    void testGetSectionNotExisting() {
        Map<String, Object> emptyMap = new HashMap<>();
        Object retrieved = getSection(emptyMap, "not_existing_object");
        assertThat(retrieved, is(nullValue()));
    }

    @Test
    void testErrorGetSectionWithNullArgument() {
        assertThrows(IllegalArgumentException.class, () -> getSection(null, "not_existing_object"));
        assertThrows(IllegalArgumentException.class, () -> getSection(Collections.emptyMap(), null));
    }

    @Test
    void testErrorGetSectionFromNotExistingList() {
        assertThrows(IllegalArgumentException.class, () -> getSection(Collections.singletonMap("names", "Max"), "$.names[0]"));
    }


    //test containsSection()
    @Test
    void testContainsSectionFlat() {
        Map<String, Object> testMap = Collections.singletonMap("text", "Lorem ipsum");
        assertThat(containsSection(testMap, "text"), is(equalTo(true)));
    }

    @Test
    void testContainsSectionNested() {
        Map<String, Object> innerMap = Collections.singletonMap("text", "Lorem ipsum");
        Map<String, Object> rootMap = Collections.singletonMap("innerMap", innerMap);
        assertThat(containsSection(rootMap, "innerMap.text"), is(equalTo(true)));
    }

    @Test
    void testContainsSectionNotExisting() {
        Map<String, Object> emptyMap = new HashMap<>();
        assertThat(containsSection(emptyMap, "not_existing"), is(equalTo(false)));
    }

    @Test
    void testCastToMap() {
        Map<String, String> validMap = Collections.singletonMap("key", "value");
        assertThat(castToMap(validMap), is(equalTo(validMap)));
        assertThrows(ConfigException.class, () -> castToMap("String"));
        assertThrows(ConfigException.class, () -> castToMap(new ArrayList<>()));
        assertThrows(ConfigException.class, () -> castToMap(null));
        assertThrows(ConfigException.class, () -> castToMap(Collections.singletonMap(1, 2)));   //key is not of type String
    }


}