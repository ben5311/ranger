package ranger.cli.writer;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;

/**
 * Writer designed to write multiple Objects into JSON file that can be imported via Elasticsearch's BULK API
 * json objects are separated by new lines
 */
public class ElasticJsonWriter extends JsonlWriter {

    private final String indexName;

    public ElasticJsonWriter(String filename, Charset charset, String indexName) throws IOException {
        this(filename, charset, false, indexName);
    }

    public ElasticJsonWriter(String filename, Charset charset, boolean append, String indexName) throws IOException {
        super(filename, charset, append);
        this.indexName = indexName;
    }

    public ElasticJsonWriter(Writer writer, String indexName) {
        super(writer);
        this.indexName = indexName;
    }

    @Override
    public synchronized JsonlWriter writeObject(Object object) throws IOException {
        writer.write("{\"index\":{\"_index\":\"" + indexName + "\"}}" + System.lineSeparator());
        return super.writeObject(object);
    }

}
