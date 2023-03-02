package ranger.core.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;
import ranger.core.ValueException;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ranger.core.csv.CsvReaderValueTest.*;

public class CircularCsvReaderValueTest {   //uses constants from CsvReaderValueTest

    @Test
    void testCircularCsvReaderValueWithoutHeader() throws IOException {
        //Given
        CSVParser parser = CSVFormat.DEFAULT.withHeader(FAKE_HEADER).parse(new FileReader(TEST_CSV_PATH));
        List<CSVRecord> actualRecords = parser.getRecords();
        parser.close();
        //When
        CircularCsvReaderValue circularCsvReaderValue = new CircularCsvReaderValue(PARSER_SETTINGS_NO_HEADER);
        //Then
        for (CSVRecord record : actualRecords) {
            Map<String, String> actualRecord = record.toMap();
            Map<String, String> generatedRecord = circularCsvReaderValue.get();
            assertThat(generatedRecord, is(equalTo(actualRecord)));
            circularCsvReaderValue.reset();
        }
        Map<String, String> nextRecord = circularCsvReaderValue.get();                           //CsvReaderValue would throw an Exception here
        assertThat(nextRecord, is(equalTo(actualRecords.get(0).toMap())));     //check if we get the first record again
    }

    @Test
    void testCircularCsvReaderValueWithHeader() throws IOException {
        //Given
        CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(new FileReader(TEST_CSV_PATH));
        List<CSVRecord> actualRecords = parser.getRecords();
        parser.close();
        //When
        CircularCsvReaderValue circularCsvReaderValue = new CircularCsvReaderValue(PARSER_SETTINGS_WITH_HEADER);
        //Then
        for (CSVRecord record : actualRecords) {
            Map<String, String> actualRecord = record.toMap();
            Map<String, String> generatedRecord = circularCsvReaderValue.get();
            assertThat(generatedRecord, is(equalTo(actualRecord)));
            circularCsvReaderValue.reset();
        }
        Map<String, String> nextRecord = circularCsvReaderValue.get();                           //CsvReaderValue would throw an Exception here
        assertThat(nextRecord, is(equalTo(actualRecords.get(0).toMap())));     //check if we get the first record again
    }

    @Test
    void testErrorConstructWithNullArgument() {
        assertThrows(ValueException.class, () -> new CircularCsvReaderValue(null));
    }


}
