package ranger.cli;

import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarStyle;
import ranger.ObjectGenerator;
import ranger.cli.option.ParameterException;
import ranger.cli.option.RangerCLIOptions;
import ranger.cli.writer.OutputWriter;
import ranger.parser.ConfigurationParser;
import ranger.util.ErrorUtil;
import ranger.util.RangerConfig;
import ranger.util.UrlUtils;
import ranger.util.YamlUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static ranger.cli.Constants.*;
import static ranger.cli.writer.OutputWriterFactory.produceWriters;
import static ranger.util.YamlUtils.getSection;

public class RangerCLI extends RangerCLIOptions implements Callable<Integer> {

    private List<String> fileNames;


    /**
     * Generate test data out of YAML configuration file
     */
    @SuppressWarnings("rawtypes")
    public int run() {
        List<OutputWriter> writers = null;
        try (InputStream in = UrlUtils.URLof(yamlFile).openStream()) {
            long started = System.currentTimeMillis();
            long numObjects = Collections.max(counts);
            Charset charset = encoding.getCharset();
            RangerConfig.setEncoding(charset);
            Map<String, Object> yamlConfig = YamlUtils.load(in, YAML_ROOT);
            // parse the output file names
            fileNames = parseOutputFileNames(yamlConfig);
            validateArgs();
            // produce the output writers
            writers = produceWriters(fileNames, outputDir, format, csvDelimiter, elasticsearchIndex, encoding, dryRun);
            ObjectGenerator generator = new ConfigurationParser(yamlFile, VALUES_PATH, OUTPUT_VALUE_PATH).build();
            // generate the elements
            System.out.printf("Generating %,d elements from \"%s\" and saving output to ", numObjects, yamlFile);
            System.out.println(dryRun ? "console" : '"' + outputDir.getAbsolutePath() + '"');
            generateObjects(generator, numObjects, writers);
            writers.forEach(ErrorUtil::tryFlush);
            String duration = Duration.ofMillis(System.currentTimeMillis() - started).toString().substring(2);
            System.out.printf("Successfully generated %d elements (took %s)%n", numObjects, duration);
            return 0;
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
        } finally {
            if (writers != null) {
                writers.forEach(ErrorUtil::tryClose);
            }
        }
        return 1;
    }

    private void generateObjects(ObjectGenerator<?> generator, long numObjects, List<OutputWriter> writers)
            throws InterruptedException {
        // Show ProgressBar unless dryRun is active
        PrintStream progressBarOutput = dryRun ? new PrintStream(OutputStream.nullOutputStream()) : System.err;
        try (ProgressBar progressBar = new ProgressBar("Generated", numObjects, 500, progressBarOutput,
                ProgressBarStyle.ASCII, "", 1L, false, null, ChronoUnit.SECONDS, 0L, Duration.ZERO)) {
            if (!parallelProcessing) {
                GenerationTask task = new GenerationTask(generator, writers, counts, progressBar);
                task.run();
            } else {
                int numThreads = Runtime.getRuntime().availableProcessors();
                List<GenerationTask> tasks = new ArrayList<>(numThreads);
                for (int i = 1; i <= numThreads; i++) {     // Split the work across threads
                    List<Long> counts;
                    if (i < numThreads) {
                        counts = this.counts.stream().map(c -> c / numThreads).collect(Collectors.toList());
                    } else {
                        counts = this.counts.stream().map(c -> c - (numThreads - 1) * (c / numThreads))
                                .collect(Collectors.toList());
                    }
                    GenerationTask task = new GenerationTask(generator.getClone(), writers, counts, progressBar);
                    task.start();
                    tasks.add(task);
                }
                for (GenerationTask task : tasks) {
                    task.join();
                }
            }
        }
    }

    static List<String> parseOutputFileNames(Map<String, Object> yamlConfig) {
        Object outputObject = getSection(yamlConfig, OUTPUT_VALUE_PATH);
        try {
            String outputString = (String) Objects.requireNonNull(outputObject);
            List<String> fileNames = new ArrayList<>();
            // parse output file names
            if (outputString.matches(OUTPUT_REFERENCE_PATTERN)) {   // output is single reference
                fileNames.add(outputString.substring(1)); // strip off $-sign
            } else if (outputString.matches(OUTPUT_LIST_PATTERN)) {    // output is list of references
                // strip off list-function header and split into outputs
                String[] outputReferences = outputString.substring(6, outputString.length() - 2).split(",( )*");
                Arrays.stream(outputReferences).forEach(f -> fileNames.add(f.substring(1))); // strip off $-sign
            } else {
                throw new RuntimeException();
            }
            return fileNames;
        } catch (RuntimeException e) {
            throw new ParameterException(String.format("The field '%s' must be set to a reference (e.g. '$ValueA') " +
                    "or a list of references (e.g. 'list([$ValueA, $ValueB])')", OUTPUT_VALUE_PATH), e);
        }
    }

    @Override
    public void validateArgs() {
        super.validateArgs();
        validateCounts();
    }

    /**
     * ensure that each output has a count.
     */
    private void validateCounts() {
        if (counts.size() != fileNames.size()) {
            if (counts.size() == 1) {
                // if only one count is specified, assume that all objects should have the same count
                for (int i = 1; i < fileNames.size(); i++) {
                    counts.add(counts.get(0));
                }
            } else {
                throw new ParameterException("you must specify only one or as many counts as there are outputs " +
                        "in the YAML config");
            }
        }
    }

    @Override
    public Integer call() {
        return run();
    }

}
