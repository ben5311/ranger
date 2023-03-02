package ranger.cli.writer;

import org.apache.commons.lang3.NotImplementedException;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;

public abstract class OutputWriter implements Closeable, Flushable {

    public synchronized OutputWriter writeObject(Object o) throws IOException {
        throw new NotImplementedException("not implemented");
    }

    public synchronized OutputWriter writeObjects(Iterable<?> objects) throws IOException {
        for (Object object : objects) {
            this.writeObject(object);
        }
        return this;
    }

    public abstract void flush() throws IOException;

    public abstract void close() throws IOException;

}
