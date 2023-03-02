package ranger.core.csv;

import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import ranger.core.ValueException;

import java.util.ArrayList;
import java.util.List;

/**
 * Value that reads CSV file and returns random record with respect to each record's weight.
 * It returns a Map with columns as properties: 'c0', 'c1', c2', ... (or header key properties if given).
 */
public class WeightedCsvReaderValue extends RandomCsvReaderValue {

    private final List<Pair<Integer, Double>> weightList;
    private final EnumeratedDistribution<Integer> enumeratedDistribution;

    /**
     * Constructs WeightedCsvReaderValue that reads CSV file and returns random record with respect to each record's weight.
     * It returns Map with columns as properties 'c0', 'c1', c2', ... (or header properties if given)
     * @param parserSettings settings for the CSV parser
     * @param weightField CSV column that contains the weight for each record (must be either a header key or key in c0, c1, .. cn syntax
     * @throws ValueException if CSV file does not contain weightField or if weightField's value is not a number
     * @throws ValueException if an IO error occurs during parsing the CSV file
     */
    public WeightedCsvReaderValue(CSVParserSettings parserSettings, String weightField) {
        super(parserSettings);
        if (weightField == null || weightField.isEmpty()) { throw new ValueException("weightField must not be null nor empty"); }
        int weightFieldColumn;
        if (weightField.matches("c[0-9]+")) {   //weightField is positional key
            int column = Integer.parseInt(weightField.substring(1));
            if (column >= records.get(0).size()) { throw new ValueException(String.format("CSV file '%s' does not contain field '%s'", parserSettings.getUrl().getPath(), weightField)); }
            weightFieldColumn = column;   //key is index
        } else {                                      //weightField is header key
            if (!headerKeys.contains(weightField)) { throw new ValueException(String.format("CSV file '%s' does not contain field '%s'. Check again or use c0,c1..cn syntax", parserSettings.getUrl().getPath(), weightField)); }
            weightFieldColumn = headerKeys.indexOf(weightField);  //key is header field
        }
        this.weightList = new ArrayList<>(records.size());
        for (int i = 0; i < records.size(); i++) {
            List<String> record = records.get(i);
            String weightValue = record.get(weightFieldColumn).replace(',', '.'); //replace decimal divisor
            try {
                double weight = Double.parseDouble(weightValue);
                weightList.add(new Pair<>(i, weight));
            } catch (NumberFormatException n) {
                throw new ValueException(String.format("weightField '%s' contains illegal value in line %d in file '%s': '%s' (weightField's values must be numeric)", weightField, parserSettings.isWithHeader() ? i+2 : i+1, parserSettings.getUrl().getPath(), weightValue));
            }
        }
        this.enumeratedDistribution = new EnumeratedDistribution<>(weightList);
    }

    /*
    Copy constructor; Only clones distribution
     */
    private WeightedCsvReaderValue(WeightedCsvReaderValue source) {
        super(source);
        this.weightList = source.weightList;
        this.enumeratedDistribution = new EnumeratedDistribution<>(this.weightList);
    }

    @Override
    protected RandomCsvReaderValue clone() {
        return new WeightedCsvReaderValue(this);
    }

    @Override
    protected void eval() {
        index = enumeratedDistribution.sample();
        List<String> nextRecord = records.get(index);
        super.eval(nextRecord);
    }

}
