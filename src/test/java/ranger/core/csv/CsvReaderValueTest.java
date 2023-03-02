package ranger.core.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;
import ranger.core.ValueException;
import ranger.util.UrlUtils;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CsvReaderValueTest {
    public static final String TEST_CSV_PATH = "src/test/resources/test.csv";
    public static final String[] FAKE_HEADER = {"c0", "c1", "c2"};
    public static final CSVParserSettings PARSER_SETTINGS_NO_HEADER = new CSVParserSettings(UrlUtils.URLof(TEST_CSV_PATH), ',', false);
    public static final CSVParserSettings PARSER_SETTINGS_WITH_HEADER = new CSVParserSettings(UrlUtils.URLof(TEST_CSV_PATH));

    @Test
    void testCsvReaderValueWithoutHeader() throws IOException {
        //Given
        CSVParser parser = CSVFormat.DEFAULT.withHeader(FAKE_HEADER).parse(new FileReader(TEST_CSV_PATH));
        List<CSVRecord> actualRecords = parser.getRecords();
        parser.close();
        //When
        CsvReaderValue csvReaderValue = new CsvReaderValue(PARSER_SETTINGS_NO_HEADER);
        //Then
        for (CSVRecord record : actualRecords) {
            Map<String, String> actualRecord = record.toMap();
            Map<String, String> generatedRecord = csvReaderValue.get();
            assertThat(generatedRecord, is(equalTo(actualRecord)));
            csvReaderValue.reset();
        }
    }

    @Test
    void testCsvReaderValueWithHeader() throws IOException {
        //Given
        CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(new FileReader(TEST_CSV_PATH));
        List<CSVRecord> actualRecords = parser.getRecords();
        parser.close();
        //When
        CsvReaderValue csvReaderValue = new CsvReaderValue(PARSER_SETTINGS_WITH_HEADER);
        //Then
        for (CSVRecord record : actualRecords) {
            Map<String, String> actualRecord = record.toMap();
            Map<String, String> generatedRecord = csvReaderValue.get();
            assertThat(generatedRecord, is(equalTo(actualRecord)));
            csvReaderValue.reset();
        }
    }

    @Test
    void testCsvReaderValueNoMoreRecords() {
        CsvReaderValue csvReaderValue = new CsvReaderValue(PARSER_SETTINGS_WITH_HEADER);
        for (int i = 0; i < 20; i++) {
            csvReaderValue.get();
            csvReaderValue.reset();
        }
        assertThrows(RuntimeException.class, csvReaderValue::get);    //No more CSV Records
    }

    @Test
    void testErrorConstructWithNullArgument() {
        assertThrows(ValueException.class, () -> new CsvReaderValue(null));
    }

}
