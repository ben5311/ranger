package ranger.util;

import org.yaml.snakeyaml.Yaml;
import ranger.parser.ConfigException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Util class for loading config YAML into Map.
 */
public class YamlUtils {

    private static final String JSON_LIST_PATTERN = "^(.+)\\[([0-9]+)\\]$";
    private static final String ERROR_SECTION_NOT_A_MAP = "YAML does not contain required fields";

    private YamlUtils() {
    }

    /**
     * Loads YAML from string.
     *
     * @param rawYaml String representing YAML file.
     * @return YAML config as Map.
     */
    public static Map<String, Object> load(String rawYaml) {
        return load(rawYaml, "$.");
    }

    /**
     * Loads YAML from string wth specified path to read.
     *
     * @param rawYaml String representing YAML file.
     * @param path    JSON Path in file to parse (default: "$.")
     * @return YAML config at given path as Map.
     */
    public static Map<String, Object> load(String rawYaml, String path) {
        if (rawYaml == null || rawYaml.isEmpty()) {
            throw new IllegalArgumentException("rawYaml must not be null nor empty");
        }
        return load(new ByteArrayInputStream(rawYaml.getBytes(RangerConfig.getEncoding())), path);
    }

    /**
     * Loads YAML from input stream.
     *
     * @param inputStream Stream representing YAML file.
     * @return YAML config as Map.
     */
    public static Map<String, Object> load(InputStream inputStream) {
        return load(inputStream, "$.");
    }

    /**
     * Loads YAML from input stream with specified path to read.
     *
     * @param inputStream Stream representing YAML file.
     * @param path        JSON Path in file to parse (default: "$.").
     * @return YAML config at given path as Map.
     */
    public static Map<String, Object> load(InputStream inputStream, String path) {
        if (inputStream == null) { throw new IllegalArgumentException("inputStream must not be null"); }
        if (path == null || path.isEmpty()) { throw new IllegalArgumentException("path must not be null nor empty"); }
        if (!path.startsWith("$.")) { throw new IllegalArgumentException("path must start with '$.'"); }
        Yaml yaml = new Yaml();
        Object parsedYaml = yaml.load(new InputStreamReader(inputStream, RangerConfig.getEncoding()));
        if (!(parsedYaml instanceof Map)) { throw new IllegalArgumentException(ERROR_SECTION_NOT_A_MAP); }
        Map<?, ?> parsedMap = (Map<?, ?>) parsedYaml;
        Object config = getSection(parsedMap, path);
        return castToMap(config);
    }

    /**
     * Strips of leading $-sign from json path
     */
    public static String stripOffDollarSign(String jsonPath) {
        if (jsonPath == null || jsonPath.equals("$.")) {
            return "";
        }
        if (jsonPath.startsWith("$.") && jsonPath.length() > 2) {
            return jsonPath.substring(2);
        }
        return jsonPath;
    }

    /**
     * Checks if parsed YAML Map contains an object at given path
     *
     * @param config the Map to search in
     * @param path   the json path to the object you want to check
     * @return true if config contains object at path, else false
     */
    public static boolean containsSection(Map<String, Object> config, String path) {
        return getSection(config, path) != null;
    }

    /**
     * Returns object from parsed YAML Map at given path
     *
     * @param config the Map to retrieve the object from
     * @param path   the json path to the object you want to get
     * @return the object at json path or null if Map contains no object at this path
     */
    public static Object getSection(Map<?, ?> config, String path) {
        if (config == null || path == null) { throw new IllegalArgumentException("config and path must not be null"); }
        path = stripOffDollarSign(path);
        if (path.equals("")) {
            return config;
        }
        if (path.contains(".")) {
            String root = path.substring(0, path.indexOf('.'));
            String subPath = path.substring(path.indexOf('.') + 1);
            Object subConfig = root.matches(JSON_LIST_PATTERN) ? selectFromList(config, root) : config.get(root);
            if (!(subConfig instanceof Map)) { return null; }
            return (getSection((Map<?, ?>) subConfig, subPath));
        } else {
            return path.matches(JSON_LIST_PATTERN) ? selectFromList(config, path) : config.get(path);
        }
    }

    /**
     * Returns element from list within config
     */
    private static Object selectFromList(Map<?, ?> config, String listElement) {
        if (listElement.contains(".")) { throw new IllegalArgumentException("path is nested"); }
        Matcher listMatcher = Pattern.compile(JSON_LIST_PATTERN).matcher(listElement);
        if (listMatcher.matches()) {
            String listName = listMatcher.group(1);
            int index = Integer.parseInt(listMatcher.group(2));
            if (!(config.get(listName) instanceof List)) {
                throw new IllegalArgumentException("'" + listElement + "' is not a list");
            }
            List<?> list = (List<?>) config.get(listName);
            if (index >= list.size()) {
                throw new ArrayIndexOutOfBoundsException(
                        "index " + index + " is out of bounds for list '" + listElement + "'");
            }
            return list.get(index);
        } else {
            throw new IllegalArgumentException("path does not point to a list element");
        }
    }


    /**
     * Checks if mapObject is of type Map and if each key in mapObject is of type String.
     * On success returns {@code Map<String, Object>}. <br>
     * Only Strings are allowed as keys because Ranger creates a reference (of type String) to any key.
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> castToMap(Object mapObject) {
        if (!(mapObject instanceof Map)) { throw new ConfigException(ERROR_SECTION_NOT_A_MAP); }
        Map<Object, Object> map = (Map<Object, Object>) mapObject;
        for (Object key : map.keySet()) {
            if (!(key instanceof String)) {
                throw new ConfigException("YAML contains a non-String key (only String keys are allowed)");
            }
        }
        return (Map<String, Object>) mapObject;
    }

}
