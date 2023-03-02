package ranger.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Class that returns Gson instance that serializes dates into ISO-8601-Strings
 */
public class GsonSerializer {

    private static final String DATE_FORMAT_ISO_8601 = "yyyy-MM-dd'T'HH:mm:ssXXX";
    private static final SimpleDateFormat DATE_FORMATTER_ISO_8601 = new SimpleDateFormat(DATE_FORMAT_ISO_8601);
    private static final DateTimeFormatter LOCAL_DATE_TIME_FORMATTER_ISO_8601 = DateTimeFormatter.ofPattern(
            DATE_FORMAT_ISO_8601);
    private static final JsonSerializer<Date> dateSerializer =
            (src, typeOfSrc, context) -> new JsonPrimitive(DATE_FORMATTER_ISO_8601.format(src));
    private static final JsonSerializer<LocalDate> localDateSerializer =
            (src, typeOfSrc, context) -> new JsonPrimitive(src.format(DateTimeFormatter.ISO_DATE));
    private static final JsonSerializer<LocalDateTime> localDateTimeSerializer =
            (src, typeOfSrc, context) -> new JsonPrimitive(
                    ZonedDateTime.of(src, ZoneId.systemDefault()).format(LOCAL_DATE_TIME_FORMATTER_ISO_8601));

    public static Gson newGsonInstance() {
        return new GsonBuilder()
                .registerTypeAdapter(Date.class, dateSerializer)
                .registerTypeAdapter(LocalDate.class, localDateSerializer)
                .registerTypeAdapter(LocalDateTime.class, localDateTimeSerializer)
                .create();
    }

}
