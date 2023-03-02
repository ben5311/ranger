package ranger.core.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.in;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ranger.core.csv.CsvReaderValueTest.*;

class RandomCsvReaderValueTest {    //uses constants from CsvReaderValueTest

    @Test
    void testRandomCsvReaderValueWithoutHeader() throws IOException {
        //Given
        CSVParser parser = CSVFormat.DEFAULT.withHeader(FAKE_HEADER).parse(new FileReader(TEST_CSV_PATH));
        List<Map<String, String>> actualRecords = new ArrayList<>();
        parser.getRecords().forEach(record -> actualRecords.add(record.toMap()));
        parser.close();
        //When
        RandomCsvReaderValue randomCsvReaderValue = new RandomCsvReaderValue(PARSER_SETTINGS_NO_HEADER);
        //Then
        for (int i = 0; i < 2*actualRecords.size(); i++) {  //test if RandomCsvReader is able to produce more Objects than CSV has entries
            Map<String, String> generatedRecord = randomCsvReaderValue.get();
            assertThat(generatedRecord, is(in(actualRecords)));
            randomCsvReaderValue.reset();
        }
    }

    @Test
    void testRandomCsvReaderValueWithHeader() throws IOException {
        //Given
        CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(new FileReader(TEST_CSV_PATH));
        List<Map<String, String>> actualRecords = new ArrayList<>();
        parser.getRecords().forEach(record -> actualRecords.add(record.toMap()));
        parser.close();
        //When
        RandomCsvReaderValue randomCsvReaderValue = new RandomCsvReaderValue(PARSER_SETTINGS_WITH_HEADER);
        //Then
        for (int i = 0; i < 2*actualRecords.size(); i++) {  //test if RandomCsvReader is able to produce more Objects than CSV has entries
            Map<String, String> generatedRecord = randomCsvReaderValue.get();
            assertThat(generatedRecord, is(in(actualRecords)));
            randomCsvReaderValue.reset();
        }
    }

    @Test
    void testErrorConstructWithNullArgument() {
        assertThrows(IllegalArgumentException.class, () -> new RandomCsvReaderValue((CSVParserSettings) null));
    }

}