package ranger.parser;

import org.junit.jupiter.api.Test;
import ranger.ObjectGenerator;
import ranger.util.YamlUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConfigurationParserTest {
    
    private static final String YAML_PATH = "src/test/resources/config/";
    
    @Test
    void testConstructWithDefaultRoot() throws IOException {
        ObjectGenerator<Map<String, Object>> firstGenerator = new ConfigurationParser(YAML_PATH+"valid_default_root.yaml").build();
        ObjectGenerator<Map<String, Object>> secondGenerator = new ConfigurationParser(YAML_PATH+"valid_default_root.yaml", "$.").build();
        ObjectGenerator<Map<String, Object>> thirdGenerator = new ConfigurationParser(YAML_PATH+"valid_default_root.yaml", "$.values", "$.output").build();
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("name", "Max");
        expectedMap.put("age", 18);
        assertThat(firstGenerator.next(), is(equalTo(expectedMap)));
        assertThat(secondGenerator.next(), is(equalTo(expectedMap)));
        assertThat(thirdGenerator.next(), is(equalTo(expectedMap)));
    }

    @Test
    void testConstructWithCustomRoot() throws IOException {
        ObjectGenerator<Map<String, Object>> firstGenerator = new ConfigurationParser(YAML_PATH+"valid_custom_root.yaml", "$.Testdata.root").build();
        ObjectGenerator<Map<String, Object>> secondGenerator = new ConfigurationParser(YAML_PATH+"valid_custom_root.yaml", "$.Testdata.root.values", "$.Testdata.root.output").build();
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("name", "Max");
        expectedMap.put("age", 18);
        assertThat(firstGenerator.next(), is(equalTo(expectedMap)));
        assertThat(secondGenerator.next(), is(equalTo(expectedMap)));
    }

    @Test
    void testConstructWithMap() throws FileNotFoundException {
        FileInputStream is = new FileInputStream(YAML_PATH+"valid_default_root.yaml");
        Map<String, Object> config = YamlUtils.load(is);
        ObjectGenerator<Map<String, Object>> generator = new ConfigurationParser(config).build();
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("name", "Max");
        expectedMap.put("age", 18);
        assertThat(generator.next(), is(equalTo(expectedMap)));
    }

    @Test
    void testErrorConstructWithNullArgument() {
        assertThrows(IllegalArgumentException.class, () -> new ConfigurationParser(null, "$.values", "$.output"));
        assertThrows(IllegalArgumentException.class, () -> new ConfigurationParser("", "$.values", "$.output"));
        assertThrows(IllegalArgumentException.class, () -> new ConfigurationParser(":country.yaml", null, "$.output"));
        assertThrows(IllegalArgumentException.class, () -> new ConfigurationParser(":country.yaml", "$.values", null));
        assertThrows(IllegalArgumentException.class, () -> new ConfigurationParser((String) null, "$."));
        assertThrows(IllegalArgumentException.class, () -> new ConfigurationParser("", "$."));
        assertThrows(IllegalArgumentException.class, () -> new ConfigurationParser((String) null));
        assertThrows(IllegalArgumentException.class, () -> new ConfigurationParser(""));
    }

    @Test
    void testErrorNotExistingYaml() {
        assertThrows(FileNotFoundException.class, () -> new ConfigurationParser("path/to/config.yaml"));
    }

    @Test
    void testErrorNotExistingPredefinedYaml() {
        assertThrows(IllegalArgumentException.class, () -> new ConfigurationParser(":config.yaml"));
    }

    @Test
    void testErrorEmptyYaml() {
        assertThrows(IllegalArgumentException.class, () -> new ConfigurationParser(YAML_PATH+"invalid_empty_file.yaml"));
    }

    @Test
    void testErrorNoValuesYaml() {
        assertThrows(ConfigException.class, () -> new ConfigurationParser(YAML_PATH+"invalid_no_values.yaml"));
    }

    @Test
    void testErrorNoOutputYaml() {
        assertThrows(ConfigException.class, () -> new ConfigurationParser(YAML_PATH+"invalid_no_output.yaml"));
    }

    @Test
    void testErrorIntegerKeyYaml() {
        assertThrows(ConfigException.class, () -> new ConfigurationParser(YAML_PATH+"invalid_integer_key.yaml").build());
    }

}
