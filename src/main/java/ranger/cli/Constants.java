package ranger.cli;

import org.apache.commons.csv.CSVFormat;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Constants {

    public enum OutputFormat {
        json, jsonl, elastic_json, csv
    }

    public enum Encoding {
        US_ASCII(StandardCharsets.US_ASCII),
        ISO_8859_1(StandardCharsets.ISO_8859_1),
        UTF_8(StandardCharsets.UTF_8),
        UTF_16(StandardCharsets.UTF_16),
        ANSI(Charset.forName("windows-1252")),
        SYSTEM(Charset.defaultCharset());

        private final Charset charset;

        Encoding(Charset charset) {
            this.charset = charset;
        }

        public Charset getCharset() {
            return charset;
        }
    }

    // Writer constants
    public static final String WINDOWS_CONSOLE_ENCODING = "Cp850";
    public static final CSVFormat CSV_FORMAT_DEFAULT = CSVFormat.DEFAULT.withFirstRecordAsHeader();
    public static final String EXTENSION_JSON = ".json";
    public static final String EXTENSION_JSONL = ".jsonl";
    public static final String EXTENSION_CSV = ".csv";

    // YAML constants
    public static final String YAML_ROOT = "$.";
    public static final String VALUES_PATH = YAML_ROOT + "values";
    public static final String OUTPUT_VALUE_PATH = YAML_ROOT + "output";
    public static final String OUTPUT_OPTIONS_PATH = YAML_ROOT + "output_options";
    public static final String OUTPUT_COUNT_PATH = OUTPUT_OPTIONS_PATH + ".count";
    public static final String OUTPUT_FORMAT_PATH = OUTPUT_OPTIONS_PATH + ".format";
    public static final String OUTPUT_CSV_DELIMITER_PATH = OUTPUT_OPTIONS_PATH + ".csv.delimiter";
    public static final String OUTPUT_ELASTIC_INDEX_PATH = OUTPUT_OPTIONS_PATH + ".elastic_json.index";
    public static final String OUTPUT_REFERENCE_PATTERN = "\\$(.)+";
    public static final String OUTPUT_LIST_PATTERN = String.format("list\\(\\[%s(, %s)*\\]\\)",
            OUTPUT_REFERENCE_PATTERN, OUTPUT_REFERENCE_PATTERN);

    // Error messages
    public static final String ERROR_FILE_NOT_FOUND = "file not found";

}
