package ranger.cli.option;

import ranger.cli.Constants;
import ranger.util.UrlUtils;
import ranger.util.YamlUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static picocli.CommandLine.*;
import static ranger.util.YamlUtils.getSection;

@Command(name = "Ranger",
        description = "Generate test data from YAML configuration file.",
        version = "1.2",
        customSynopsis = "ranger <yamlFile> [-o <outputDir>] [-c <count>[,<count>...]] [-f <format>] [OPTIONS]",
        defaultValueProvider = RangerCLIOptions.YamlValueProvider.class,
        headerHeading = "@|bold,underline Usage|@:%n",
        synopsisHeading = "%n",
        descriptionHeading = "%n@|bold,underline Description|@:%n%n",
        parameterListHeading = "%n@|bold,underline Parameters|@:%n",
        optionListHeading = "%n@|bold,underline Options|@:%n",
        sortOptions = false,
        mixinStandardHelpOptions = true)
public class RangerCLIOptions {

    @Parameters(index = "0", description = "Path to YAML configuration file")
    protected String yamlFile;

    @Option(names = {"-o", "--output"}, description = "The output directory (default: ${DEFAULT-VALUE})",
            defaultValue = "output")
    protected File outputDir;

    @Option(names = {"-c", "--counts"}, description = "The count of objects to generate " +
            "(comma separated for multiple outputs)", paramLabel = "<count>", split = ",", required = true)
    protected List<Long> counts;

    @Option(names = {"-f", "--output-format"}, description = "The output format. Must be one of " +
            "[${COMPLETION-CANDIDATES}]", required = true)
    protected Constants.OutputFormat format;

    @Option(names = "--csv-delimiter", description = "The csv delimiter character (for csv format)")
    protected Character csvDelimiter;

    @Option(names = "--elastic-index", description = "The elasticsearch index (for elastic_json format)")
    protected String elasticsearchIndex;

    @Option(names = "--encoding", description = "The encoding for reading and writing files. Must be one of " +
            "[${COMPLETION-CANDIDATES}] (default: ${DEFAULT-VALUE})", defaultValue = "UTF_8")
    protected Constants.Encoding encoding;

    @Option(names = "--dry-run", description = "Print output to console and not to file")
    protected boolean dryRun;

    @Option(names = "-m", description = "Enable multi core processing " +
            "(experimental, use only if order of generated objects is not important)")
    protected boolean parallelProcessing;

    public void validateArgs() {
        for (Long count : counts) {
            if (count < 1) {
                throw new ParameterException("counts must be greater than 0");
            }
        }
        if (format == Constants.OutputFormat.elastic_json && elasticsearchIndex == null) {
            throw new ParameterException("You must specify the elastic search index " +
                    "when using elastic_json output format");
        }
    }

    /**
     * Static default value provider that loads default options from YAML configuration file
     * via loadConfigValues function
     */
    public static class YamlValueProvider implements IDefaultValueProvider {

        private static final Map<String, String> OPTIONS = new HashMap<>();

        public static void loadConfigValues(String yamlFile) throws IOException {
            try (InputStream in = UrlUtils.URLof(yamlFile).openStream()) {
                Map<String, Object> yamlConfig = YamlUtils.load(in, Constants.YAML_ROOT);
                Object countObject = getSection(yamlConfig, Constants.OUTPUT_COUNT_PATH);
                Object formatObject = getSection(yamlConfig, Constants.OUTPUT_FORMAT_PATH);
                Object csvDelimiter = getSection(yamlConfig, Constants.OUTPUT_CSV_DELIMITER_PATH);
                Object elasticsearchIndex = getSection(yamlConfig, Constants.OUTPUT_ELASTIC_INDEX_PATH);
                OPTIONS.put("--counts", stringOf(countObject));
                OPTIONS.put("--output-format", stringOf(formatObject));
                OPTIONS.put("--csv-delimiter", stringOf(csvDelimiter));
                OPTIONS.put("--elastic-index", stringOf(elasticsearchIndex));
            }
        }

        private static String stringOf(Object object) {
            return object != null ? object.toString() : null;
        }

        @Override
        public String defaultValue(Model.ArgSpec argSpec) {
            String key = argSpec.isOption() ? ((Model.OptionSpec) argSpec).longestName() : argSpec.paramLabel();
            return OPTIONS.get(key);
        }

    }


}
