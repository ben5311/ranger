package ranger.cli.writer;

import org.apache.commons.csv.CSVFormat;
import ranger.cli.Constants;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static ranger.cli.Constants.CSV_FORMAT_DEFAULT;
import static ranger.cli.Constants.OutputFormat;

public class OutputWriterFactory {

    /**
     * Produce OutputWriter for each fileName in fileNames that writes to file in target output directory
     * in the given output format.
     */
    public static List<OutputWriter> produceWriters(List<String> fileNames, File outputDir, OutputFormat format,
                                                    Charset charset, Character csvDelimiter, String elasticsearchIndex)
            throws IOException {
        List<OutputWriter> writers = new ArrayList<>();
        for (String fileName : fileNames) {
            fileName = new File(outputDir, fileName).getPath();
            switch (format) {
                case json:
                    writers.add(new JsonWriter(fileName, charset));
                    break;
                case jsonl:
                    writers.add(new JsonlWriter(fileName, charset));
                    break;
                case elastic_json:
                    writers.add(new ElasticJsonWriter(fileName, charset, elasticsearchIndex));
                    break;
                case csv:
                    CSVFormat csvFormat = CSV_FORMAT_DEFAULT;
                    if (csvDelimiter != null) {
                        csvFormat = csvFormat.withDelimiter(csvDelimiter);
                    }
                    writers.add(new CsvWriter(fileName, charset, false, csvFormat));
                    break;
            }
        }
        return writers;
    }

    /**
     * Produce OutputWriters that write the output to given Writer in given output format.
     */
    public static List<OutputWriter> produceWriters(List<String> fileNames, Writer writer, OutputFormat format,
                                                    Character csvDelimiter, String elasticsearchIndex)
            throws IOException {
        List<OutputWriter> writers = new ArrayList<>();
        for (String ignored : fileNames) {
            switch (format) {
                case json:
                    writers.add(new JsonWriter(writer));
                    break;
                case jsonl:
                    writers.add(new JsonlWriter(writer));
                    break;
                case elastic_json:
                    writers.add(new ElasticJsonWriter(writer, elasticsearchIndex));
                    break;
                case csv:
                    CSVFormat csvFormat = CSV_FORMAT_DEFAULT;
                    if (csvDelimiter != null) {
                        csvFormat = csvFormat.withDelimiter(csvDelimiter);
                    }
                    writers.add(new CsvWriter(writer, csvFormat));
                    break;
            }
        }
        return writers;
    }

    public static List<OutputWriter> produceWriters(List<String> fileNames, File outputDir, OutputFormat format,
                                                    Character csvDelimiter, String elasticsearchIndex,
                                                    Constants.Encoding encoding, boolean dryRun) throws IOException {
        if (dryRun) {   // on dry run write output to console instead of file
            Writer console = new OutputStreamWriter(System.out);
            return produceWriters(fileNames, console, format, csvDelimiter, elasticsearchIndex);
        } else {
            return produceWriters(fileNames, outputDir, format, encoding.getCharset(),
                    csvDelimiter, elasticsearchIndex);
        }
    }

}
