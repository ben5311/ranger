package ranger.core.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ranger.core.csv.CsvReaderValueTest.*;

class WeightedCsvReaderValueTest {  //uses constants from CsvReaderValueTest

    @ParameterizedTest
    @ValueSource(strings = {"c0", "income"})    //test with both cn syntax and head key
    void testWeightedCsvReaderValueWithHeader(String weightField) throws IOException {

        //Given
        CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(new FileReader(TEST_CSV_PATH));
        List<Map<String, String>> actualRecords = new ArrayList<>();
        parser.getRecords().forEach(record -> actualRecords.add(record.toMap()));
        parser.close();

        //When
        WeightedCsvReaderValue weightedCsvReaderValue = new WeightedCsvReaderValue(PARSER_SETTINGS_WITH_HEADER, weightField);   //weightField is field 'income'

        //Then
        int sampleQuantity = 10000*actualRecords.size();   //generate lots of samples so we get an accurate result
        Map<Map<String, String>, Integer> generatedCounts = new HashMap<>();    //list where each possible record is associated to the count it was generated
        for (int i = 0; i < sampleQuantity; i++) {
            Map<String, String> generatedRecord = weightedCsvReaderValue.get();
            assertThat(generatedRecord, is(in(actualRecords)));     //test if generated records are valid
            generatedCounts.put(generatedRecord, generatedCounts.getOrDefault(generatedRecord, 0) + 1);     //increment count for generated record
            weightedCsvReaderValue.reset();
        }
        //test if counts are realistic
        long incomeSum = actualRecords.stream().mapToLong(record -> Long.parseLong(record.get("income"))).sum();
        generatedCounts.forEach((record, count) -> {
            double relationship = Double.parseDouble(record.get("income")) * sampleQuantity / (incomeSum * count);
            double deviation = 1-relationship;
            assertThat("deviation of record's count is too high", deviation, is(closeTo(0.0, 0.10)));  //allow deviation of 10%
        });
    }


    @Test
    void testErrorConstructWithNullArgument() {
        assertThrows(IllegalArgumentException.class, () -> new WeightedCsvReaderValue(null, null));
        assertThrows(IllegalArgumentException.class, () -> new WeightedCsvReaderValue(null, "c0"));
        assertThrows(IllegalArgumentException.class, () -> new WeightedCsvReaderValue(PARSER_SETTINGS_WITH_HEADER, null));
    }

    @Test
    void testErrorConstructWithInvalidWeightFieldArgument() {
        assertThrows(IllegalArgumentException.class, () -> new WeightedCsvReaderValue(PARSER_SETTINGS_WITH_HEADER, ""));    //empty weightField
        assertThrows(IllegalArgumentException.class, () -> new WeightedCsvReaderValue(PARSER_SETTINGS_WITH_HEADER, "c"+FAKE_HEADER.length));  //column is out of bounds
        assertThrows(IllegalArgumentException.class, () -> new WeightedCsvReaderValue(PARSER_SETTINGS_WITH_HEADER, "country"));    //csv has no column named "country"
    }

}