package ranger.core.csv;

import ranger.core.Switchable;
import ranger.core.Value;

import java.util.Objects;

/**
 * Value that holds a reference to CsvReaderValue and
 * retrieves a new value from it on each eval(). For internal use only.
 */
@SuppressWarnings("rawtypes")
public class CsvProxy extends Value<String> implements Switchable {

    private final CsvReaderValue parentCsvReader;
    private final String columnName;

    CsvProxy(CsvReaderValue parentCsvReader, String columnName) {
        this.parentCsvReader = Objects.requireNonNull(parentCsvReader);
        this.columnName = Objects.requireNonNull(columnName);
    }

    /*
    Copy constructor
     */
    private CsvProxy(CsvProxy source) {
        super(source);
        this.columnName = source.columnName;
        this.parentCsvReader = (CsvReaderValue) source.parentCsvReader.getClone();
        this.parentCsvReader.csvProxies.put(columnName, this);
        int columnPos = parentCsvReader.headerKeys.indexOf(columnName);
        this.parentCsvReader.csvProxies.put("c"+columnPos, this);
    }

    void setValue(String value) {
        this.val = value;
    }

    @Override
    public void eval() {
        parentCsvReader.get(); //ensure next CSV record is parsed before getting sub element
    }

    @Override
    public void reset() {
        parentCsvReader.reset();
        super.reset();
    }

    @Override
    public int getIndex() {
        return parentCsvReader.getIndex();
    }

    @Override
    public int getSize() {
        return parentCsvReader.getSize();
    }

    @Override
    protected CsvProxy clone() {
        return new CsvProxy(this);
    }
}
