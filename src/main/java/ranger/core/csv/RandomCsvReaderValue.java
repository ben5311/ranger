package ranger.core.csv;

import org.apache.commons.csv.CSVRecord;
import ranger.core.ValueException;
import ranger.distribution.Distribution;
import ranger.distribution.UniformDistribution;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Value that reads CSV file and returns random record. It returns a Map with columns as properties: 'c0', 'c1', c2', ...
 * (or header key properties if given).
 */
public class RandomCsvReaderValue extends CsvReaderValue {

    protected final List<List<String>> records;
    protected final Distribution distribution;


    /**
     * Constructs CsvReaderValue from parserSettings that reads the specified CSV file
     * and returns random record. It supplies each record as Map with columns as properties:
     * 'c0', 'c1', c2', ... (or header key properties if given).
     *
     * @param parserSettings Settings for the CSV parser.
     * @throws ValueException if an IO error occurs during parsing the CSV file
     */
    public RandomCsvReaderValue(CSVParserSettings parserSettings) {
        this(parserSettings, new UniformDistribution());
    }

    /**
     * Constructs CsvReaderValue from parserSettings that reads the specified CSV file
     * and returns random record. It supplies each record as Map with columns as properties:
     * 'c0', 'c1', c2', ... (or header key properties if given).
     * It chooses the random record with respect to specified distribution.
     *
     * @param parserSettings settings for the CSV parser.
     * @param distribution Distribution that generates the line number of next random record
     * @throws ValueException if an IO error occurs during parsing the CSV file
     */
    public RandomCsvReaderValue(CSVParserSettings parserSettings, Distribution distribution) {
        super(parserSettings);
        this.records = new ArrayList<>();
        while (iterator.hasNext()) {    //load all records into list
            CSVRecord next = iterator.next();
            List<String> list = new ArrayList<>(next.size());
            next.iterator().forEachRemaining(list::add);    //convert CSVRecord to list to save CSVRecords' redundant attributes
            records.add(list);
        }
        if (records.isEmpty()) { throw new ValueException(String.format("CSV file %s does not contain any record", parserSettings.getUrl().getPath())); }
        this.distribution = Objects.requireNonNull(distribution);
    }

    /*
    Copy constructor; Only clones distribution
     */
    protected RandomCsvReaderValue(RandomCsvReaderValue source) {
        super(source.parserSettings);
        this.val = source.val;
        this.evaluated = source.evaluated;
        this.records = source.records;
        this.distribution = source.distribution.clone();
    }

    @Override
    public int getSize() {
        return records.size();
    }

    @Override
    protected RandomCsvReaderValue clone() {
        return new RandomCsvReaderValue(this);
    }

    @Override
    protected void eval() {
        index = distribution.nextInt(records.size());
        List<String> nextRecord = records.get(index);
        super.eval(nextRecord);
    }

}
