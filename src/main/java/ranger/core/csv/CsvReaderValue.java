package ranger.core.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import ranger.core.Composite;
import ranger.core.Switchable;
import ranger.core.ValueException;
import ranger.util.RangerConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Value that reads CSV file sequentially. It returns each record as Map with columns as properties: 'c0', 'c1', c2', ...
 * (or header key properties if given). Can only generate as many Maps as CSV contains records.
 */
public class CsvReaderValue extends Composite<String> implements Switchable<Map<String, String>> {

    protected final CSVParserSettings parserSettings;
    protected CSVParser csvParser;
    protected Iterator<CSVRecord> iterator;
    protected final List<String> headerKeys;
    protected final Map<String, CsvProxy> csvProxies;
    protected int index = -1;

    /**
     * Constructs CsvReaderValue from parserSettings that reads the specified CSV file
     * record-by-record. It supplies each record as Map with columns as properties: 'c0', 'c1', c2', ...
     * (or header key properties if given). Can only generate as many Maps as CSV contains records.
     *
     * @param parserSettings Settings for the CSV parser
     * @throws ValueException if you try to retrieve more records from it than contained in CSV file
     * @throws ValueException if an IO error occurs during parsing the CSV file
     */
    public CsvReaderValue(CSVParserSettings parserSettings) {
        if (parserSettings == null) {
            throw new ValueException("parserSettings cannot be null.");
        }
        this.parserSettings = parserSettings;
        CSVParser scout = createCSVParser(parserSettings);
        int columnCount = scout.iterator().next().size(); //read first line and check it's size
        try {
            scout.close();
        } catch (IOException e) {
            throw new ValueException("Error parsing csv file", e);
        }
        this.csvParser = createCSVParser(parserSettings);
        this.iterator = csvParser.iterator();
        this.csvProxies = new LinkedHashMap<>();
        if (!parserSettings.isWithHeader()) {
            this.headerKeys = new ArrayList<>(columnCount);
            for (int i = 0; i < columnCount; i++) {  //add c0,c1,...cn proxies
                String columnName = "c"+i;
                headerKeys.add(columnName);
                CsvProxy columnProxy = new CsvProxy(this, columnName);
                csvProxies.put(columnName, columnProxy);
                values.put(columnName, columnProxy);
            }
        } else {    //add header proxies
            this.headerKeys = new ArrayList<>(csvParser.getHeaderMap().keySet());
            for (int i = 0; i < headerKeys.size(); i++) {  //add proxies for c0,c1,...cn and header keys
                String columnName = headerKeys.get(i);
                CsvProxy columnProxy = new CsvProxy(this, columnName);
                csvProxies.put(columnName, columnProxy);
                csvProxies.put("c"+i, columnProxy);
                values.put(columnName, columnProxy);
            }
        }
    }

    public Map<String, CsvProxy> getCsvProxies() {
        return Collections.unmodifiableMap(csvProxies);
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public int getSize() {
        return Integer.MAX_VALUE;   //Return max value because we can not know how many entries the CSV file has
    }

    @Override
    public void reset() {
        evaluated = false;
    }

    @Override
    protected CsvReaderValue clone() {
        return new CsvReaderValue(
                this.parserSettings);    //Need to recreate whole CsvReaderValue because we cannot clone Iterator
    }

    @Override
    protected void eval() {
        if (!iterator.hasNext()) { throw new ValueException("No more CSV records available in file: '" + parserSettings.getUrl().getPath() + "'"); }
        index++;
        CSVRecord record = iterator.next();
        eval(record);
    }

    protected void eval(Iterable<String> record) {
        LinkedHashMap<String, String> evaluatedValues = new LinkedHashMap<>();
        Iterator<String> values = record.iterator();
        headerKeys.forEach(k -> {
            String value;
            if (values.hasNext()) {
                value = values.next();
            } else {
                value = null;
            }
            evaluatedValues.put(k, value);
            csvProxies.get(k).setValue(value);
        });
        val = Collections.unmodifiableMap(evaluatedValues);
    }

    static CSVParser createCSVParser(CSVParserSettings parserSettings) {
        CSVFormat csvFormat = CSVFormat.DEFAULT.withDelimiter(parserSettings.getDelimiter())
                .withRecordSeparator(parserSettings.getRecordSeparator())
                .withTrim(parserSettings.isTrim())
                .withQuote(parserSettings.getQuote())
                .withCommentMarker(parserSettings.getCommentMarker())
                .withIgnoreEmptyLines(parserSettings.isIgnoreEmptyLines())
                .withNullString(parserSettings.getNullString());
        if (parserSettings.isWithHeader()) {
            csvFormat = csvFormat.withFirstRecordAsHeader();
        }
        try {
            Reader reader = new BufferedReader(
                    new InputStreamReader(parserSettings.getUrl().openStream(), RangerConfig.getEncoding()));
            return new CSVParser(reader, csvFormat);
        } catch (Exception e) {
            throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
        }
    }
}
