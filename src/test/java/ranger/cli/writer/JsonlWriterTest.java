package ranger.cli.writer;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class JsonlWriterTest {

    @TempDir
    File tempdir;
    String testFilePath;
    String expectedFilePath;

    @BeforeEach
    void setUp() {
        File testFile = new File(tempdir, "test");
        testFilePath = testFile.getAbsolutePath();
        expectedFilePath = testFilePath + ".jsonl";
    }

    @Test
    void testWriteJsonlFile() throws IOException {
        //Given
        Map<String, Object> testRecord = new HashMap<>();
        testRecord.put("id", "1");
        testRecord.put("name", "Max Mustermann");
        Gson gson = new Gson();
        //When
        new JsonlWriter(testFilePath, Charset.defaultCharset()).writeObject(testRecord).close();
        //Then
        File[] newFiles = tempdir.listFiles();
        assertThat(newFiles, is(arrayWithSize(1)));
        assertThat(newFiles[0].getAbsolutePath(), is(equalTo(expectedFilePath)));
        try (Stream<String> stream = Files.lines(Paths.get(expectedFilePath), StandardCharsets.UTF_8)) {
            List<String> lines = stream.collect(Collectors.toList());
            assertThat(lines, is(iterableWithSize(1)));
            assertThat(gson.fromJson(lines.get(0), Map.class), is(equalTo(testRecord)));
        }
    }

    @Test
    void testAppendToJsonlFile() throws IOException {
        //Given
        Map<String, Object> firstRecord = new HashMap<>();
        firstRecord.put("id", "1");
        firstRecord.put("name", "Max Mustermann");
        new JsonlWriter(testFilePath, Charset.defaultCharset()).writeObject(firstRecord).close();
        Map<String, Object> secondRecord = new HashMap<>();
        secondRecord.put("id", "2");
        secondRecord.put("name", "Michael Schumacher");
        Gson gson = new Gson();
        //When
        new JsonlWriter(testFilePath, Charset.defaultCharset(), true).writeObject(secondRecord).close();
        //Then
        File[] newFiles = tempdir.listFiles();
        assertThat(newFiles, is(arrayWithSize(1)));
        assertThat(newFiles[0].getAbsolutePath(), is(equalTo(expectedFilePath)));
        try (Stream<String> stream = Files.lines(Paths.get(expectedFilePath), StandardCharsets.UTF_8)) {
            List<String> lines = stream.collect(Collectors.toList());
            assertThat(lines, is(iterableWithSize(2)));
            assertThat(gson.fromJson(lines.get(0), Map.class), is(equalTo(firstRecord)));
            assertThat(gson.fromJson(lines.get(1), Map.class), is(equalTo(secondRecord)));
        }

    }


}
