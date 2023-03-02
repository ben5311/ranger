package ranger.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class RangerConfig {
    private static Charset encoding = StandardCharsets.UTF_8;

    public static Charset getEncoding() {
        return encoding;
    }

    public static void setEncoding(Charset encoding) {
        RangerConfig.encoding = encoding;
    }
}
