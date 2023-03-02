package ranger.cli.writer;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static ranger.cli.Constants.CSV_FORMAT_DEFAULT;

public class CsvWriterTest {

    @TempDir
    File tempdir;
    String testFilePath;
    String expectedFilePath;

    @BeforeEach
    void setUp() {
        File testFile = new File(tempdir, "test");
        testFilePath = testFile.getAbsolutePath();
        expectedFilePath = testFilePath + ".csv";
    }

    @Test
    void testWriteCsvFile() throws IOException {
        //Given
        Map<String, Object> testRecord = new HashMap<>();
        testRecord.put("id", "1");
        testRecord.put("name", "Max Mustermann");
        //When
        new CsvWriter(testFilePath, Charset.defaultCharset()).writeObject(testRecord).close();
        //Then
        File[] newFiles = tempdir.listFiles();
        assertThat(newFiles, is(arrayWithSize(1)));
        assertThat(newFiles[0].getAbsolutePath(), is(equalTo(expectedFilePath)));
        try (CSVParser parser = CSV_FORMAT_DEFAULT.parse(new FileReader(expectedFilePath))) {
            assertThat(parser.getHeaderNames(), is(iterableWithSize(2)));
            assertThat(parser.getHeaderNames(), containsInAnyOrder("id", "name"));
            List<CSVRecord> records = parser.getRecords();
            assertThat(records, is(iterableWithSize(1)));
            assertThat(records.get(0).get("id"), is(equalTo("1")));
            assertThat(records.get(0).get("name"), is(equalTo("Max Mustermann")));
        }
    }

    @Test
    void testAppendToCsvFile() throws IOException {
        //Given
        Map<String, Object> firstRecord = new HashMap<>();
        firstRecord.put("id", "1");
        firstRecord.put("name", "Max Mustermann");
        new CsvWriter(testFilePath, Charset.defaultCharset()).writeObject(firstRecord).close();
        Map<String, Object> secondRecord = new HashMap<>();
        secondRecord.put("id", "2");
        secondRecord.put("name", "Michael Schumacher");
        //When
        new CsvWriter(testFilePath, Charset.defaultCharset(), true).writeObject(secondRecord).close();
        //Then
        File[] newFiles = tempdir.listFiles();
        assertThat(newFiles, is(arrayWithSize(1)));
        assertThat(newFiles[0].getAbsolutePath(), is(equalTo(expectedFilePath)));
        try (CSVParser parser = CSV_FORMAT_DEFAULT.parse(new FileReader(expectedFilePath))) {
            assertThat(parser.getHeaderNames(), is(iterableWithSize(2)));
            assertThat(parser.getHeaderNames(), containsInAnyOrder("id", "name"));
            List<CSVRecord> records = parser.getRecords();
            assertThat(records, is(iterableWithSize(2)));
            assertThat(records.get(0).get("id"), is(equalTo("1")));
            assertThat(records.get(0).get("name"), is(equalTo("Max Mustermann")));
            assertThat(records.get(1).get("id"), is(equalTo("2")));
            assertThat(records.get(1).get("name"), is(equalTo("Michael Schumacher")));
        }
    }


}
