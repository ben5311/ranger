package ranger.core.csv;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ranger.util.UrlUtils.URLof;

public class CSVParserSettingsTest {

    private static final CSVParserSettings DEFAULT_SETTINGS = new CSVParserSettings(URLof("test.csv"), ',', true, "\n", true, '"', '#', true, null);

    @Test
    void testConstructCSVParserSettingsSingleArg() {
        CSVParserSettings parserSettings = new CSVParserSettings(URLof("test.csv"));
        assertThat(parserSettings, is(equalTo(DEFAULT_SETTINGS)));
    }

    @Test
    void testConstructCSVParserSettingsTwoArgs() {
        CSVParserSettings parserSettings = new CSVParserSettings(URLof("test.csv"), ',');
        assertThat(parserSettings, is(equalTo(DEFAULT_SETTINGS)));
    }

    @Test
    void testConstructCSVParserSettingsThreeArgs() {
        CSVParserSettings parserSettings = new CSVParserSettings(URLof("test.csv"), ',', true);
        assertThat(parserSettings, is(equalTo(DEFAULT_SETTINGS)));
    }

    @Test
    void testConstructCSVParserSettingsWithNullArgument() {
        assertThrows(IllegalArgumentException.class, () -> new CSVParserSettings(null));
        assertThrows(IllegalArgumentException.class, () -> new CSVParserSettings(null, ',', false));
        assertThrows(IllegalArgumentException.class, () -> new CSVParserSettings(URLof("test.csv"), ',', false, null, true, '"', '#', true, null));
    }

}
