package ranger.core.csv;

import ranger.core.ValueException;

import java.net.URL;
import java.util.Objects;

/**
 * Settings available for CSV parser.
 */
public class CSVParserSettings {

    private final URL url;
    private final char delimiter;
    private final String recordSeparator;
    private final boolean trim;
    private final Character quote;
    private final char commentMarker;
    private final boolean ignoreEmptyLines;
    private final String nullString;
    private boolean withHeader;

    /**
     * Creates settings with specified URL to the CSV file. Default values for other parameters:
     * <ul>
     * <li><code>delimiter</code> - <code>','</code></li>
     * <li><code>recordSeparator</code> - <code>"\n"</code></li>
     * <li><code>trim</code> - <code>true</code></li>
     * <li><code>quote</code> - <code>'"'</code></li>
     * <li><code>commentMarker</code> - <code>'#'</code></li>
     * <li><code>ignoreEmptyLines</code> - <code>true</code></li>
     * <li><code>nullString</code> - <code>null</code> (disabled)</li>
     * <li><code>withHeader</code> - <code>true</code> (enabled)</li>
     * </ul>
     *
     * @param url URL to the CSV file.
     * @throws ValueException if url is null
     */
    public CSVParserSettings(URL url) {
        this(url, ',');
    }

    /**
     * Creates settings with specified URL to the CSV file. Default values for other parameters:
     * <ul>
     * <li><code>delimiter</code> - <code>','</code></li>
     * <li><code>recordSeparator</code> - <code>"\n"</code></li>
     * <li><code>trim</code> - <code>true</code></li>
     * <li><code>quote</code> - <code>'"'</code></li>
     * <li><code>commentMarker</code> - <code>'#'</code></li>
     * <li><code>ignoreEmptyLines</code> - <code>true</code></li>
     * <li><code>nullString</code> - <code>null</code> (disabled)</li>
     * <li><code>withHeader</code> - <code>true</code> (enabled)</li>
     * </ul>
     *
     * @param url URL to the CSV file.
     * @throws ValueException if url is null
     */
    public CSVParserSettings(URL url, char delimiter) {
        this(url, delimiter, true);
    }

    /**
     * Creates settings with specified URL to the CSV file. Default values for other parameters:
     * <ul>
     * <li><code>delimiter</code> - <code>','</code></li>
     * <li><code>recordSeparator</code> - <code>"\n"</code></li>
     * <li><code>trim</code> - <code>true</code></li>
     * <li><code>quote</code> - <code>'"'</code></li>
     * <li><code>commentMarker</code> - <code>'#'</code></li>
     * <li><code>ignoreEmptyLines</code> - <code>true</code></li>
     * <li><code>nullString</code> - <code>null</code> (disabled)</li>
     * <li><code>withHeader</code> - <code>true</code> (enabled)</li>
     * </ul>
     *
     * @param url URL to the CSV file.
     * @param withHeader specifies if csv file's first record should be interpreted as header
     * @throws ValueException if url is null
     */
    public CSVParserSettings(URL url, char delimiter, boolean withHeader) {
        this(url, delimiter, withHeader, "\n", true, '"', '#', true, null);
    }

    /**
     * Creates settings with specified parameters.
     *
     * @param url URL to the CSV file.
     * @param delimiter Delimiter of columns within CSV file.
     * @param withHeader specifies if first record should be interpreted as header
     * @param recordSeparator Delimiter of records within CSV file.
     * @param trim True if each column value is to be trimmed for leading and trailing whitespace, otherwise
     *            <code>false</code>.
     * @param quote Character that will be stripped from beginning and end of each column if present. If set to
     *            <code>null</code>, no characters will be stripped (nothing will be used as quote character).
     * @param commentMarker Character to use as a comment marker, everything after it is considered comment.
     * @param ignoreEmptyLines True if empty lines are to be ignored, otherwise <code>false</code>.
     * @param nullString Converts string with given value to <code>null</code>. If set to <code>null</code>, no
     *            conversion will be done
     * @throws ValueException if url or recordSeparator are null or empty
     */
    public CSVParserSettings(URL url, char delimiter, boolean withHeader, String recordSeparator, boolean trim, Character quote, char commentMarker, boolean ignoreEmptyLines, String nullString) {
        if (url == null) {
            throw new ValueException("url cannot be null");
        }
        if (recordSeparator == null || recordSeparator.isEmpty()) {
            throw new ValueException("recordSeperator cannot be null nor empty");
        }
        this.url = url;
        this.delimiter = delimiter;
        this.recordSeparator = recordSeparator;
        this.trim = trim;
        this.quote = quote;
        this.commentMarker = commentMarker;
        this.ignoreEmptyLines = ignoreEmptyLines;
        this.nullString = nullString;
        this.withHeader = withHeader;
    }

    /**
     * Returns path to the CSV file.
     *
     * @return Path to the CSV file.
     */
    public URL getUrl() {
        return url;
    }

    /**
     * Returns column delimiter.
     *
     * @return Column delimiter.
     */
    public char getDelimiter() {
        return delimiter;
    }

    /**
     * Returns record separator.
     *
     * @return Record separator.
     */
    public String getRecordSeparator() {
        return recordSeparator;
    }

    /**
     * Indicates whether to trim column values or not.
     *
     * @return True if values are to be trimmed, otherwise false.
     */
    public boolean isTrim() {
        return trim;
    }

    /**
     * Returns quote character.
     *
     * @return Quote character, if null, no character will be used as quote.
     */
    public Character getQuote() {
        return quote;
    }

    /**
     * Returns comment marker.
     *
     * @return Comment marker.
     */
    public char getCommentMarker() {
        return commentMarker;
    }

    /**
     * Indicates whether to ignore empty lines or not.
     *
     * @return True if empty lines are ignored, otherwise false.
     */
    public boolean isIgnoreEmptyLines() {
        return ignoreEmptyLines;
    }

    /**
     * Returns null string.
     *
     * @return String which should be interpreted as null, if null, no string will be interpreted as null.
     */
    public String getNullString() {
        return nullString;
    }

    /**
     * Indicates whether to interpret the first csv record as header.
     *
     * @return True if empty lines are ignored, otherwise false.
     */
    public boolean isWithHeader() {
        return withHeader;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CSVParserSettings that = (CSVParserSettings) o;
        return delimiter == that.delimiter &&
                trim == that.trim &&
                commentMarker == that.commentMarker &&
                ignoreEmptyLines == that.ignoreEmptyLines &&
                withHeader == that.withHeader &&
                url.equals(that.url) &&
                recordSeparator.equals(that.recordSeparator) &&
                Objects.equals(quote, that.quote) &&
                Objects.equals(nullString, that.nullString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, delimiter, recordSeparator, trim, quote, commentMarker, ignoreEmptyLines, nullString, withHeader);
    }
}
