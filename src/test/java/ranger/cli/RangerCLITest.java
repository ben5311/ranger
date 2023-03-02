package ranger.cli;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.List;

public class RangerCLITest {

    private static final String YAML_PATH = "src/test/resources";
    private static final PrintStream SYS_OUT = System.out;
    private static final ByteArrayOutputStream FAKE_OUT = new ByteArrayOutputStream();

    private final RangerCLI testDataGenerator = new RangerCLI();
    private final List<File> newFiles = null;  //holds the within test newly created files

    @BeforeAll
    static void init() {
        System.setOut(new PrintStream(FAKE_OUT));
    }

    @AfterEach
    void cleanUp() {
        FAKE_OUT.reset();
        if (newFiles != null) {
            newFiles.forEach(File::delete);
        }
    }

    @AfterAll
    static void restore() {
        System.setOut(SYS_OUT);
    }


    //Test YAML generation

   /* @ParameterizedTest
    @ValueSource(strings = {"null", "test", "not_existing.yaml"})
    void testErrorFileNotFound(String filename) {
        InputConfig inputConfig = new InputConfig();
        inputConfig.yamlFile = filename;
        generatorService.generateYaml(inputConfig);
        assertThat(FAKE_OUT.toString(), containsString(Constants.ERROR_OPENING_YAML_FILE));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid_empty_file.yaml", "invalid_no_values.yaml", "invalid_no_format.yaml",
            "invalid_no_output.yaml", "invalid_wrong_format.yaml", "invalid_wrong_output.yaml",
            "invalid_not_existing_reference.yaml"})
    void testErrorWhileParsingYaml(String filename) {
        OutputConfig outputConfig = new OutputConfig();
        outputConfig.yamlFile = YAML_PATH + "/" + filename;
        generatorService.generateYaml(outputConfig);
        assertThat(FAKE_OUT.toString(), containsString("ERROR"));
    }

    *//**
     * Helper method that runs GeneratorService with configPath and then returns a List of all newly generated files.
     *
     * @param configPath path to forward to GeneratorService
     * @return {@code List<File>} containing all newly generated files
     *//*
    private List<File> generateAndGetCreatedFiles(String configPath) {
        OutputConfig outputConfig = new OutputConfig();
        outputConfig.yamlFile = configPath;
        File workingDir = new File(".");
        List<File> filesBefore = Arrays.asList(workingDir.listFiles());
        generatorService.generateYaml(outputConfig);
        List<File> newFiles = new ArrayList<>(Arrays.asList(workingDir.listFiles()));
        newFiles.removeAll(filesBefore);
        return newFiles;
    }

    //test with single output
    @Test
    void testConfigOneOutputCSV() throws IOException {
        //When
        newFiles = generateAndGetCreatedFiles(YAML_PATH+"/valid_single_csv.yaml");
        //Then
        assertThat(newFiles, is(iterableWithSize(1)));
        assertThat(newFiles.get(0).getName(), is(equalTo("EntityA.csv")));
        FileReader reader = new FileReader("EntityA.csv");
        CSVParser parser = CSV_FORMAT_DEFAULT.parse(reader);
        assertThat(parser.getHeaderNames(), is(iterableWithSize(3)));
        assertThat(parser.getHeaderNames(), containsInAnyOrder("id", "username", "income"));
        List<CSVRecord> records = parser.getRecords();
        assertThat(records.size(), is(equalTo(100)));
        parser.close();
    }

    @Test
    @SuppressWarnings("unchecked")
    void testConfigOneOutputJSON() throws IOException {
        //When
        newFiles = generateAndGetCreatedFiles(YAML_PATH+"/valid_single_json.yaml");
        //Then
        assertThat(newFiles, is(iterableWithSize(1)));
        assertThat(newFiles.get(0).getName(), is(equalTo("EntityA.json")));
        FileReader reader = new FileReader("EntityA.json");
        Gson gson = new Gson();
        List<Map<String, Object>> records = gson.fromJson(reader, List.class);
        assertThat(records.size(), is(equalTo(100)));
        reader.close();
    }

    //test with two outputs
    @Test
    void testConfigTwoOutputsCSV() throws IOException {
        //When
        newFiles = generateAndGetCreatedFiles(YAML_PATH+"/valid_list_csv.yaml");
        //Then
        assertThat(newFiles, is(iterableWithSize(2)));
        assertThat(newFiles.get(0).getName(), is(anyOf(equalTo("EntityA.csv"), equalTo("EntityB.csv"))));
        assertThat(newFiles.get(1).getName(), is(anyOf(equalTo("EntityA.csv"), equalTo("EntityB.csv"))));
        //read EntityA.csv
        FileReader readerA = new FileReader("EntityA.csv");
        CSVParser parserA = CSV_FORMAT_DEFAULT.parse(readerA);
        assertThat(parserA.getHeaderNames(), is(iterableWithSize(3)));
        assertThat(parserA.getHeaderNames(), containsInAnyOrder("id", "username", "income"));
        List<CSVRecord> recordsA = parserA.getRecords();
        assertThat(recordsA.size(), is(equalTo(100)));
        //read EntityB.csv
        FileReader readerB = new FileReader("EntityB.csv");
        CSVParser parserB = CSV_FORMAT_DEFAULT.parse(readerB);
        assertThat(parserB.getHeaderNames(), is(iterableWithSize(3)));
        assertThat(parserB.getHeaderNames(), containsInAnyOrder("id", "firstname", "balance"));
        List<CSVRecord> recordsB = parserB.getRecords();
        assertThat(recordsB.size(), is(equalTo(50)));
        parserA.close();
        parserB.close();
    }

    @Test
    @SuppressWarnings("unchecked")
    void testConfigTwoOutputsJSON() throws IOException {
        //When
        newFiles = generateAndGetCreatedFiles(YAML_PATH+"/valid_list_json.yaml");
        //Then
        assertThat(newFiles, is(iterableWithSize(2)));
        assertThat(newFiles.get(0).getName(), is(anyOf(equalTo("EntityA.json"), equalTo("EntityB.json"))));
        assertThat(newFiles.get(1).getName(), is(anyOf(equalTo("EntityA.json"), equalTo("EntityB.json"))));
        //read EntityA.json
        FileReader readerA = new FileReader("EntityA.json");
        Gson gson = new Gson();
        List<Map<String, Object>> recordsA = gson.fromJson(readerA, List.class);
        assertThat(recordsA.size(), is(equalTo(100)));
        //read EntityB.json
        FileReader readerB = new FileReader("EntityB.json");
        List<Map<String, Object>> recordsB = gson.fromJson(readerB, List.class);
        assertThat(recordsB.size(), is(equalTo(50)));
        readerA.close();
        readerB.close();
    }

    @Test
    @SuppressWarnings("unchecked")
    void testConfigTwoOutputsJSONWithSingleCount() throws IOException {
        //When
        newFiles = generateAndGetCreatedFiles(YAML_PATH+"/valid_list_json_single_count.yaml");
        //Then
        assertThat(newFiles, is(iterableWithSize(2)));
        assertThat(newFiles.get(0).getName(), is(anyOf(equalTo("EntityA.json"), equalTo("EntityB.json"))));
        assertThat(newFiles.get(1).getName(), is(anyOf(equalTo("EntityA.json"), equalTo("EntityB.json"))));
        //read EntityA.json
        FileReader readerA = new FileReader("EntityA.json");
        Gson gson = new Gson();
        List<Map<String, Object>> recordsA = gson.fromJson(readerA, List.class);
        assertThat(recordsA.size(), is(equalTo(100)));
        //read EntityB.json
        FileReader readerB = new FileReader("EntityB.json");
        List<Map<String, Object>> recordsB = gson.fromJson(readerB, List.class);
        assertThat(recordsB.size(), is(equalTo(100)));
        readerA.close();
        readerB.close();
    }*/

}
