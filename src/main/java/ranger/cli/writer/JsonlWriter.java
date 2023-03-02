package ranger.cli.writer;

import com.google.gson.Gson;
import ranger.util.GsonSerializer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Objects;

import static ranger.cli.Constants.EXTENSION_JSONL;

/**
 * Writer designed to write multiple Objects into new line delimited JSON file.
 */
public class JsonlWriter extends OutputWriter {

    protected final Gson gson;
    protected final Writer writer;

    public JsonlWriter(String filename, Charset charset) throws IOException {
        this(filename, charset, false);
    }

    public JsonlWriter(String filename, Charset charset, boolean append) throws IOException {
        Objects.requireNonNull(filename);
        this.gson = GsonSerializer.newGsonInstance();
        File file = new File(filename + EXTENSION_JSONL);
        if (!file.exists() && file.toPath().getParent() != null) {
            Files.createDirectories(file.toPath().getParent());
        }
        this.writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, append), charset));
    }

    public JsonlWriter(Writer writer) {
        this.gson = GsonSerializer.newGsonInstance();
        this.writer = writer;
    }

    @Override
    public synchronized JsonlWriter writeObject(Object object) throws IOException {
        Objects.requireNonNull(object);
        writer.write(gson.toJson(object) + System.lineSeparator());
        return this;
    }

    @Override
    public void flush() throws IOException {
        writer.flush();
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }

}
