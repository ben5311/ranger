package ranger.core.csv;

import ranger.core.ValueException;

import java.io.IOException;

/**
 * Value that reads CSV file sequentially. It returns each record as Map with columns as properties: 'c0', 'c1', c2', ...
 * (or header key properties if given). Restarts at first record again after reaching last record.
 */
public class CircularCsvReaderValue extends CsvReaderValue {

    /**
     * Constructs CircularCsvReaderValue from parserSettings that reads the specified CSV file
     * record-by-record. It supplies each record as Map with columns as properties: 'c0', 'c1', c2', ...
     * (or header key properties if given). Jumps to first record again after reaching CSV's last record.
     *
     * @param parserSettings Settings for the CSV parser
     * @throws ValueException if an IO error occurs during parsing the CSV file
     */
    public CircularCsvReaderValue(CSVParserSettings parserSettings) {
        super(parserSettings);
    }

    @Override
    protected CircularCsvReaderValue clone() {
        return new CircularCsvReaderValue(this.parserSettings);
    }

    @Override
    protected void eval() {
        if(!iterator.hasNext()){
            try {
                refresh();
            } catch (IOException e) {
                throw new ValueException("Error parsing csv file", e);
            }
        }
        super.eval();
    }

    private void refresh() throws IOException {
        this.csvParser.close();
        this.csvParser = createCSVParser(parserSettings);
        this.iterator = csvParser.iterator();
        this.index = 0;
    }
}
