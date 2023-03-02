package ranger.util;

import java.io.Flushable;

public class ErrorUtil {

    public static void tryFlush(Flushable flushable) {
        try {
            flushable.flush();
        } catch (Exception ignored) {

        }
    }

    public static void tryClose(AutoCloseable closeable) {
        try {
            closeable.close();
        } catch (Exception ignored) {

        }
    }

}
