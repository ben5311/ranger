package ranger.cli.writer;

import com.google.gson.Gson;
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

public class JsonWriterTest {

    @TempDir
    File tempdir;
    String testFilePath;
    String expectedFilePath;

    @BeforeEach
    void setUp() {
        File testFile = new File(tempdir, "test");
        testFilePath = testFile.getAbsolutePath();
        expectedFilePath = testFilePath + ".json";
    }

    @Test
    @SuppressWarnings("unchecked")
    void testWriteJsonFile() throws IOException {
        //Given
        Map<String, Object> testRecord = new HashMap<>();
        testRecord.put("id", "1");
        testRecord.put("name", "Max Mustermann");
        //When
        new JsonWriter(testFilePath, Charset.defaultCharset()).writeObject(testRecord).close();
        //Then
        File[] newFiles = tempdir.listFiles();
        assertThat(newFiles, is(arrayWithSize(1)));
        assertThat(newFiles[0].getAbsolutePath(), is(equalTo(expectedFilePath)));
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(expectedFilePath)) {
            List<Map<String, Object>> parsed = gson.fromJson(reader, List.class);
            assertThat(parsed, is(iterableWithSize(1)));
            Map<String, Object> parsedMap = parsed.get(0);
            assertThat(parsedMap, is(aMapWithSize(2)));
            assertThat(parsedMap.keySet(), containsInAnyOrder("id", "name"));
            assertThat(parsedMap.get("id"), is(equalTo("1")));
            assertThat(parsedMap.get("name"), is(equalTo("Max Mustermann")));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    void testAppendToJsonFile() throws IOException {
        //Given
        Map<String, Object> firstRecord = new HashMap<>();
        firstRecord.put("id", "1");
        firstRecord.put("name", "Max Mustermann");
        new JsonWriter(testFilePath, Charset.defaultCharset()).writeObject(firstRecord).close();
        Map<String, Object> secondRecord = new HashMap<>();
        secondRecord.put("id", "2");
        secondRecord.put("name", "Michael Schumacher");
        //When
        new JsonWriter(testFilePath, Charset.defaultCharset(), true).writeObject(secondRecord).close();
        //Then
        File[] newFiles = tempdir.listFiles();
        assertThat(newFiles, is(arrayWithSize(1)));
        assertThat(newFiles[0].getAbsolutePath(), is(equalTo(expectedFilePath)));
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(expectedFilePath)) {
            List<Map<String, Object>> parsed = gson.fromJson(reader, List.class);
            assertThat(parsed, is(iterableWithSize(2)));
            assertThat(parsed.get(0), is(aMapWithSize(2)));
            assertThat(parsed.get(0).keySet(), containsInAnyOrder("id", "name"));
            assertThat(parsed.get(0).get("id"), is(equalTo("1")));
            assertThat(parsed.get(0).get("name"), is(equalTo("Max Mustermann")));
            assertThat(parsed.get(1), is(aMapWithSize(2)));
            assertThat(parsed.get(1).keySet(), containsInAnyOrder("id", "name"));
            assertThat(parsed.get(1).get("id"), is(equalTo("2")));
            assertThat(parsed.get(1).get("name"), is(equalTo("Michael Schumacher")));
        }
    }


}
