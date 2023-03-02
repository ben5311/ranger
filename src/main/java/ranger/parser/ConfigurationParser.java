package ranger.parser;

import org.parboiled.Parboiled;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;
import ranger.ObjectGenerator;
import ranger.core.*;
import ranger.util.UrlUtils;
import ranger.util.YamlUtils;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static ranger.util.YamlUtils.*;

/**
 * Constructs {@link ObjectGenerator} out of parsed configuration.
 */
public class ConfigurationParser {

    private static final String VALUES = "values";  //default path to values object
    private static final String OUTPUT = "output";  //default path to output object

    private Map<String, Object> values;
    private Object outputExpression;
    private final URL fileUrl;
    private final URL workingDirectoryUrl;
    private Map<String, ValueProxy<?>> valueProxies;
    private ValueExpressionParser parser;
    private ReportingParseRunner<Value<?>> parseRunner;



    /**
     * Constructs Builder that will build {@link ObjectGenerator} out of given YAML configuration file.
     * The YAML file must contain 'values' and 'output' fields in it's root.
     *
     * @param filePath path to YAML configuration file
     * @throws IOException if an IO Error occurs while reading YAML file
     * @throws ConfigException if an error occurs while parsing YAML file
     */
    public ConfigurationParser(String filePath) throws IOException {
        this(filePath, "$.");
    }

    /**
     * Constructs Builder with given yamlRoot that will build {@link ObjectGenerator} out of given YAML configuration file.
     * You can set yamlRoot to custom json path to root object containing 'values' and 'output' fields.
     * If you set yamlRoot to null, default value "$." is selected.
     *
     * @param filePath path to YAML configuration file
     * @param yamlRoot json path to the root object containing 'values' and 'output' fields (e.g. $.root)
     * @throws IOException if an IO Error occurs while reading YAML file
     * @throws ConfigException if an error occurs while parsing YAML file
     */
    public ConfigurationParser(String filePath, String yamlRoot) throws IOException {
        this.fileUrl = UrlUtils.URLof(filePath);
        this.workingDirectoryUrl = UrlUtils.getParentURL(this.fileUrl);
        Object yaml = YamlUtils.load(fileUrl.openStream());
        loadConfig(yaml, yamlRoot);
    }


    /**
     * Constructs Builder with given valuesRoot and outputRoot that will build {@link ObjectGenerator} out of given YAML configuration file.
     * valuesRoot must be the json path to values object and outputRoot the json path to output object.
     *
     * @param filePath path to YAML configuration file
     * @param valuesRoot json path to values object within YAML file (e.g. $.root.values)
     * @param outputRoot json path to output object within YAML file (e.g. $.root.output)
     * @throws IOException if an IO Error occurs while reading YAML file
     * @throws ConfigException if an error occurs while parsing YAML file
     */
    public ConfigurationParser(String filePath, String valuesRoot, String outputRoot) throws IOException {
        this.fileUrl = UrlUtils.URLof(filePath);
        this.workingDirectoryUrl = UrlUtils.getParentURL(this.fileUrl);
        Object yaml = YamlUtils.load(fileUrl.openStream());
        loadConfig(yaml, valuesRoot, outputRoot);
    }

    /*
     * Constructs Builder from file at fileUrl with given yamlRoot that will build {@link ObjectGenerator}.
     * For internal use only.
     */
    ConfigurationParser(URL fileUrl, String yamlRoot) throws IOException {
        this.fileUrl = Objects.requireNonNull(fileUrl);
        this.workingDirectoryUrl = UrlUtils.getParentURL(fileUrl);
        Object yaml = YamlUtils.load(fileUrl.openStream());
        loadConfig(yaml, yamlRoot);
    }

    /**
     * Constructs Builder from parsed YAML config that will build {@link ObjectGenerator}.
     *
     * @param config parsed YAML configuration.
     * @throws ConfigException if config does not contain 'values' or 'output'
     */
    @Deprecated
    public ConfigurationParser(Map<String, Object> config) throws ConfigException {
        this.fileUrl = null;
        this.workingDirectoryUrl = UrlUtils.URLof("."); //take JVM's working directory
        loadConfig(config, "$.");
    }


    /*
    Loads and verifies parsed yaml config with given yamlRoot
     */
    private void loadConfig(Object config, String yamlRoot) {
        if (config == null) { throw new IllegalArgumentException("config must not be null"); }
        yamlRoot = stripOffDollarSign(yamlRoot);
        String valuesRoot, outputRoot;
        if (yamlRoot.length() > 0) {
            valuesRoot = yamlRoot+"."+VALUES;
            outputRoot = yamlRoot+"."+OUTPUT;
        } else {
            valuesRoot = VALUES;
            outputRoot = OUTPUT;
        }
        this.loadConfig(config, valuesRoot, outputRoot);
    }

    /*
    Verifies and loads parsed yaml config with given valuesRoot and outputRoot
     */
    private void loadConfig(Object config, String valuesRoot, String outputRoot) {
        if (config == null || valuesRoot == null || outputRoot == null || valuesRoot.isEmpty() || outputRoot.isEmpty()) { throw new IllegalArgumentException("config, valuesRoot and outputRoot must not be null nor empty"); }
        valuesRoot = stripOffDollarSign(valuesRoot);
        outputRoot = stripOffDollarSign(outputRoot);
        Map<String, Object> configMap = castToMap(config);
        if (!containsSection(configMap, valuesRoot)) { throw new ConfigException(String.format("YAML file '%s' does not contain required field '%s'", fileUrl.getPath(), valuesRoot)); }
        if (!containsSection(configMap, outputRoot)) { throw new ConfigException(String.format("YAML file '%s' does not contain required field '%s'", fileUrl.getPath(), outputRoot)); }
        Object valuesObject = getSection(configMap, valuesRoot);
        if (!(valuesObject instanceof Map)) { throw new ConfigException(String.format("YAML file '%s' does not contain any values under '%s'", fileUrl.getPath(), valuesRoot)); }
        this.values = castToMap(valuesObject);
        this.outputExpression = getSection(configMap, outputRoot);
    }



    /**
     * Creates an instance of {@link ObjectGenerator} based on provided configuration.
     *
     * @param <T> Type of object {@link ObjectGenerator} will generate. You have to manually specify it on use (unchecked Cast). If output Value is complex, it will be {@code Map<String, Object>}. Else it will be the type of flat output Value.
     * @return An instance of {@link ObjectGenerator}.
     * @throws ConfigException if an error occurs during parsing yaml entries
     */
    @SuppressWarnings({ "unchecked" })
    public <T> ObjectGenerator<T> build() throws ConfigException {
        buildModel();
        return new ObjectGenerator<>((Value<T>) parseSimpleValue("", outputExpression));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} based on provided configuration.. Resulting
     * {@link ObjectGenerator} will try to convert configured output to specified <code>objectType</code>.
     *
     * @param objectType Type of object to which conversion will be attempted.
     * @param <T> Type of object {@link ObjectGenerator} will generate.
     * @return Instance of {@link ObjectGenerator}.
     * @throws ConfigException if an error occurs during parsing yaml entries
     */
    public <T> ObjectGenerator<T> build(Class<T> objectType) throws ConfigException {
        buildModel();
        return new ObjectGenerator<>(new TypeConverterValue<>(objectType, parseSimpleValue("", outputExpression)));
    }

    /**
     * Initialize parser and parse Values
     * @throws ConfigException if an error occurs during parsing yaml entries
     */
    private void buildModel() throws ConfigException {
        this.valueProxies = new HashMap<>();
        this.parser = Parboiled.createParser(ValueExpressionParser.class, valueProxies);
        this.parser.setWorkingDirectoryUrl(workingDirectoryUrl);
        this.parseRunner = new ReportingParseRunner<>(parser.value());
        if (values != null) {
            createProxies();
            parseValues();
        }
    }

    /**
     * Create a ValueProxy for each yaml entry before parsing.
     * Using ValueProxies make it possible to refer to a Value (more precisely, it's ValueProxy)
     * with $path.to.value even if it is defined in a later line in the yaml config and thus parsed later
     */
    private void createProxies() {
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            createProxy(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Recursively creates a ValueProxy for each key and for each sub key in a Map
     * so you can refer to sub elements too
     * @param key root key
     * @param value root value
     */
    private void createProxy(String key, Object value) {
        valueProxies.put(key, new ValueProxy<>());
        if (value instanceof Map) {
            for (Map.Entry<String, Object> entry : castToMap(value).entrySet()) {
                createProxy(key + "." + entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Parses each value from values and then sets it parsed result as
     * delegate to it's ValueProxy
     * @throws ConfigException if an error occurs during parsing Values
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void parseValues() throws ConfigException {
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            Value<?> val = parse(entry.getKey(), entry.getValue());
            ValueProxy proxy = valueProxies.get(entry.getKey());
            proxy.setDelegate(val);
            entry.setValue(proxy);
        }
    }

    /**
     *
     * @throws ConfigException if an error occurs during parsing a Value
     */
    private Value<?> parse(String parentName, Object def) throws ConfigException {
        if (def instanceof Map) {
            return parseCompositeValue(parentName, castToMap(def));
        } else {
            return parseSimpleValue(parentName, def);
        }
    }

    /**
     * parses CompositeValues (YAML objects containing sub elements)
     * @param parentName path to CompositeValue (parent)
     * @param def CompositeValue's map containing the sub elements
     * @return Instance of CompositeValue
     * @throws ConfigException if an error occurs during parsing a sub element
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private CompositeValue parseCompositeValue(String parentName, Map<String, Object> def) throws ConfigException {
        Map<String, Value<?>> values = new LinkedHashMap<>();
        for (String property : def.keySet()) {
            String fullName = parentName + "." + property;
            Value<?> val = parse(fullName, def.get(property));
            ValueProxy proxy = valueProxies.get(fullName);
            proxy.setDelegate(val);
            values.put(property, proxy);
        }
        return new CompositeValue(values);
    }

    /**
     * parses simple Values (plain Objects without sub elements)
     * @param parentName path to Value
     * @param def String or Number expression that is gonna get parsed into Value
     * @return Instance of Value
     * @throws ConfigException if an error occurs during parsing def
     */
    private Value<?> parseSimpleValue(String parentName, Object def) throws ConfigException {
        // handle String as expression and all other types as primitives
        if (def instanceof String) {
            try {
                parser.setCurrentPath(parentName);
                ParsingResult<Value<?>> result = parseRunner.run((String) def);
                return result.valueStack.pop();
            } catch (org.parboiled.errors.ParserRuntimeException e) {   //Error during parsing expression
                String message = e.getMessage();
                ConfigException configException = new ConfigException("Error parsing yaml entry '"+parentName+"' in file '"+ fileUrl.getPath() +"': ");
                if (!message.contains("^")) {
                    throw configException;
                }
                int parsingLineStart = message.indexOf("):")+3;
                String parsingLine = message.substring(parsingLineStart, message.indexOf('^', parsingLineStart)+1);
                configException.addBlock(parsingLine);
                Throwable cause;
                Throwable root = e;
                while ((cause = root.getCause()) != null && cause != root) {    //Unwrap all causes and append their messages
                    root = cause;
                    if (root instanceof ConfigException) {
                        ConfigException rootException = (ConfigException) root;
                        configException.addBlock("Caused by "+root.getClass().getSimpleName()+": "+rootException.blocks.get(0));
                        for (int i = 1; i < rootException.blocks.size(); i++) {
                            configException.addBlock(rootException.blocks.get(i));
                        }
                    } else {
                        configException.addBlock("Caused by "+root.getClass().getSimpleName()+": "+root.getMessage());
                    }
                }
                throw configException;
            }

        } else {
            return ConstantValue.of(def);
        }
    }

}
