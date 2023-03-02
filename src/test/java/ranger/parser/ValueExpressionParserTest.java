package ranger.parser;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.parboiled.Parboiled;
import org.parboiled.errors.ParserRuntimeException;
import org.parboiled.parserunners.ParseRunner;
import org.parboiled.parserunners.ReportingParseRunner;
import ranger.core.AsciiTransformer;
import ranger.core.CaseTransformer;
import ranger.core.CircularRangeValueChar;
import ranger.core.CircularRangeValueDate;
import ranger.core.CircularRangeValueInt;
import ranger.core.CircularValue;
import ranger.core.CompositeValue;
import ranger.core.ConstantValue;
import ranger.core.DiscreteValue;
import ranger.core.EmptyListValue;
import ranger.core.EmptyMapValue;
import ranger.core.ExactWeightedValue;
import ranger.core.GetterTransformer;
import ranger.core.JsonTransformer;
import ranger.core.ListValue;
import ranger.core.MapperValue;
import ranger.core.NowDateValue;
import ranger.core.NowLocalDateTimeValue;
import ranger.core.NowLocalDateValue;
import ranger.core.NowValue;
import ranger.core.RandomContentStringValue;
import ranger.core.RandomLengthListValue;
import ranger.core.RangeValueChar;
import ranger.core.RangeValueDate;
import ranger.core.RangeValueInt;
import ranger.core.StringTransformer;
import ranger.core.StringfTransformer;
import ranger.core.SwitchValue;
import ranger.core.TimeFormatTransformer;
import ranger.core.UUIDValue;
import ranger.core.Value;
import ranger.core.ValueProxy;
import ranger.core.WeightedValue;
import ranger.core.XegerValue;
import ranger.core.arithmetic.AdditionValueInteger;
import ranger.core.arithmetic.DivisionValueInteger;
import ranger.core.arithmetic.MultiplicationValueInteger;
import ranger.core.arithmetic.SubtractionValueInteger;
import ranger.core.csv.CircularCsvReaderValue;
import ranger.core.csv.CsvReaderValue;
import ranger.core.csv.RandomCsvReaderValue;
import ranger.core.csv.WeightedCsvReaderValue;
import ranger.distribution.NormalDistribution;
import ranger.distribution.UniformDistribution;
import ranger.util.UrlUtils;

import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ranger.parser.ValueExpressionParser.replaceSpecialChars;
import static ranger.parser.ValueExpressionParser.stripOffLastReference;


/**
 * This class tests if ValueExpressionParser creates the right Value with right arguments for each method.
 * It doesn't targets testing the Value's function itself.
 */
public class ValueExpressionParserTest {

    private final Map<String, ValueProxy<?>> valueProxies = new HashMap<>();
    private final ValueExpressionParser parser = Parboiled.createParser(ValueExpressionParser.class, valueProxies);
    private final ParseRunner<Value<?>> parseRunner = new ReportingParseRunner<>(parser.value());


    @BeforeEach
    void initParser() {
        valueProxies.clear();
        parser.setWorkingDirectoryUrl(null);
        parser.setCurrentPath("");
    }



    @Test
    void testErrorParseInvalidExpression() {
        assertThrows(ParserRuntimeException.class, () -> parseRunner.run("invalidFunction()").valueStack.pop());
    }

    @Test
    void testValueReference() {
        ValueProxy<?> proxy = new ValueProxy<>();
        valueProxies.put("test.Element", proxy);
        assertThat(parseRunner.run("$test.Element").valueStack.pop(), is(proxy));
    }


    //DISTRIBUTIONS

    @Test
    void testUniformDistribution() {
        ParseRunner<Object> parseRunner = new ReportingParseRunner<>(parser.distribution());
        assertThat(parseRunner.run("uniform()").valueStack.pop(), is(instanceOf(UniformDistribution.class)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"normal()", "normal(0.5, 0.125, 0, 1)"})
    void testNormalDistribution(String expression) {
        ParseRunner<Object> parseRunner = new ReportingParseRunner<>(parser.distribution());
        assertThat(parseRunner.run(expression).valueStack.pop(), is(instanceOf(NormalDistribution.class)));
    }


    //RANDOM AND CIRCULAR

    @ParameterizedTest
    @ValueSource(strings = {"random([1])", "random([1], uniform())"})
    void testDiscreteValue(String expression) {
        Value<?> random = parseRunner.run(expression).valueStack.pop();
        assertThat(random, is(instanceOf(DiscreteValue.class)));
        assertThat(random.get(), is(equalTo(1)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"random(1..2)", "random(1..2, false, uniform())"})
    void testRangeValue(String expression) {
        Value<?> random = parseRunner.run(expression).valueStack.pop();
        assertThat(random, is(instanceOf(RangeValueInt.class)));
        assertThat(random.get(), is(equalTo(1)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"random('a'..'b')", "random('a'..'b', false, uniform())"})
    void testRangeValueChar(String expression) {
        Value<?> random = parseRunner.run(expression).valueStack.pop();
        assertThat(random, is(instanceOf(RangeValueChar.class)));
        assertThat(random.get(), is(oneOf('a', 'b')));
    }

    @ParameterizedTest
    @ValueSource(strings = {"random(date(\"2019-01-01\")..date(\"2019-01-02\"))", "random(date(\"2019-01-01\")..date(\"2019-01-02\"), false, uniform())"})
    void testRangeValueDate(String expression) {
        Value<?> random = parseRunner.run(expression).valueStack.pop();
        assertThat(random, is(instanceOf(RangeValueDate.class)));
        assertThat(new SimpleDateFormat("yyyy-MM-dd").format(random.get()), is(equalTo("2019-01-01")));
    }

    @Test
    void testCircularValue() {
        Value<?> circular = parseRunner.run("circular([1])").valueStack.pop();
        assertThat(circular, is(instanceOf(CircularValue.class)));
        assertThat(circular.get(), is(equalTo(1)));
    }

    @Test
    void testCircularRangeValue() {
        Value<?> circular = parseRunner.run("circular(1..2, 1)").valueStack.pop();
        assertThat(circular, is(instanceOf(CircularRangeValueInt.class)));
        assertThat(circular.get(), is(equalTo(1)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"circular('b'..'c', 1)", "circular('b'..'a', -1)"})
    void testCircularRangeValueChar(String expression) {
        Value<?> random = parseRunner.run(expression).valueStack.pop();
        assertThat(random, is(instanceOf(CircularRangeValueChar.class)));
        assertThat(random.get(), is(equalTo('b')));
    }

    @ParameterizedTest
    @ValueSource(strings = {"circular(date(\"2019-01-02\")..date(\"2019-01-03\"), 1)", "circular(date(\"2019-01-02\")..date(\"2019-01-01\"), -1)"})
    void testCircularRangeValueDate(String expression) {
        Value<?> random = parseRunner.run(expression).valueStack.pop();
        assertThat(random, is(instanceOf(CircularRangeValueDate.class)));
        assertThat(new SimpleDateFormat("yyyy-MM-dd").format(random.get()), is(equalTo("2019-01-02")));
    }


    //WEIGHTED AND EXACTLY

    @Test
    void testWeightedValue() {
        Value<?> weighted = parseRunner.run("weighted([(1, 100)])").valueStack.pop();
        assertThat(weighted, is(instanceOf(WeightedValue.class)));
        assertThat(weighted.get(), is(equalTo(1)));
    }

    @Test
    void testExactWeightedValue() {
        Value<?> exactly = parseRunner.run("exactly([(1, 100)])").valueStack.pop();
        assertThat(exactly, is(instanceOf(ExactWeightedValue.class)));
        assertThat(exactly.get(), is(equalTo(1)));
    }


    //RANDOM STRINGS

    @Test
    void testRandomContentStringValue() {
        Value<?> randomContentString = parseRunner.run("randomContentString(2, ['a'..'a'])").valueStack.pop();
        assertThat(randomContentString, is(instanceOf(RandomContentStringValue.class)));
        assertThat(randomContentString.get(), is(equalTo("aa")));
    }

    @Test
    void testXegerValue() {
        Value<?> xeger = parseRunner.run("xeger(\"abc\")").valueStack.pop();
        assertThat(xeger, is(instanceOf(XegerValue.class)));
        assertThat(xeger.get(), is(equalTo("abc")));
    }


    //TIME

    @Test
    void testNowValue() {
        assertThat(parseRunner.run("now()").valueStack.pop(), is(instanceOf(NowValue.class)));
    }

    @Test
    void testNowDateValue() {
        assertThat(parseRunner.run("nowDate()").valueStack.pop(), is(instanceOf(NowDateValue.class)));
    }

    @Test
    void testNowLocalDateValue() {
        assertThat(parseRunner.run("nowLocalDate()").valueStack.pop(), is(instanceOf(NowLocalDateValue.class)));
    }

    @Test
    void testNowLocalDateTimeValue() {
        assertThat(parseRunner.run("nowLocalDateTime()").valueStack.pop(), is(instanceOf(NowLocalDateTimeValue.class)));
    }


    //ARITHMETIC OPERATIONS

    @Test
    void testAdditionValue() {
        Value<?> add = parseRunner.run("add('int', 2.5, 2.5)").valueStack.pop();
        assertThat(add, is(instanceOf(AdditionValueInteger.class)));
        assertThat(add.get(), is(equalTo(4)));
    }

    @Test
    void testSubtractionValue() {
        Value<?> subtract = parseRunner.run("subtract('int', 2.5, 2.5)").valueStack.pop();
        assertThat(subtract, is(instanceOf(SubtractionValueInteger.class)));
        assertThat(subtract.get(), is(equalTo(0)));
    }

    @Test
    void testMultiplicationValue() {
        Value<?> multiply = parseRunner.run("multiply('int', 2.5, 2.5)").valueStack.pop();
        assertThat(multiply, is(instanceOf(MultiplicationValueInteger.class)));
        assertThat(multiply.get(), is(equalTo(4)));
    }

    @Test
    void testDivisionValue() {
        Value<?> division = parseRunner.run("divide('int', 2.5, 2.5)").valueStack.pop();
        assertThat(division, is(instanceOf(DivisionValueInteger.class)));
        assertThat(division.get(), is(equalTo(1)));
    }


    @Test
    void testUuidValue() {
        assertThat(parseRunner.run("uuid()").valueStack.pop(), is(instanceOf(UUIDValue.class)));
    }


    //COMPLEX VALUES

    /*
     * Creates environment with the two CompositeValues refA and refB:
     *
     *  A:
     *   id: "1"
     *  B:
     *   name: "Max Mustermann"
     *
     * Then calls the function merge([$A, $B]) and
     * checks whether the returned element contains both elements 'id' and 'name'.
     * Also, it checks if the references to the merged object has been added to
     * the valueProxies Map.
     *
     */
    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void testMergedValue() {
        //Given
        ValueProxy A_id = new ValueProxy<>(new ConstantValue<>("1"));
        ValueProxy B_name = new ValueProxy<>(new ConstantValue<>("Max Mustermann"));
        ValueProxy A = new ValueProxy<>(new CompositeValue(Collections.singletonMap("id", A_id)));
        ValueProxy B = new ValueProxy<>(new CompositeValue(Collections.singletonMap("name", B_name)));

        valueProxies.put("A", A);
        valueProxies.put("A.id", A_id);
        valueProxies.put("B", B);
        valueProxies.put("B.name", B_name);
        parser.setCurrentPath("test.Merged");       //store the MergedValue under test.Merged

        //When
        Value<?> merge = parseRunner.run("merge([$A, $B])").valueStack.pop();

        //Then
        assertThat(merge, is(instanceOf(CompositeValue.class)));
        Map<String, Object> mergedMap = ((CompositeValue) merge).get();
        assertThat(mergedMap.keySet(), containsInAnyOrder("id", "name"));
        assertThat(mergedMap.get("id"), is(equalTo("1")));
        assertThat(mergedMap.get("name"), is(equalTo("Max Mustermann")));
        assertThat(valueProxies.keySet(), hasItems("test.Merged.id", "test.Merged.name"));
        assertThat(valueProxies.get("test.Merged.id"), is(equalTo(A_id)));
        assertThat(valueProxies.get("test.Merged.name"), is(equalTo(B_name)));
    }

    @Test
    void testListValue() {
        Value<?> list = parseRunner.run("list([1, 2, 3])").valueStack.pop();
        assertThat(list, is(instanceOf(ListValue.class)));
        assertThat(list.get(), is(equalTo(Arrays.asList(1, 2, 3))));
    }

    @Test
    void testRandomLengthListValue() {
        Value<?> list = parseRunner.run("list(2, 3, 1)").valueStack.pop();
        assertThat(list, is(instanceOf(RandomLengthListValue.class)));
        assertThat(list.get(), is(oneOf(Arrays.asList(1, 1), Arrays.asList(1, 1, 1))));
    }

    @Test
    void testEmptyListValue() {
        assertThat(parseRunner.run("emptyList()").valueStack.pop(), is(instanceOf(EmptyListValue.class)));
    }

    @Test
    void testEmptyMapValue() {
        assertThat(parseRunner.run("emptyMap()").valueStack.pop(), is(instanceOf(EmptyMapValue.class)));
    }


    //CSV VALUE

    /*
     * Parses the functions csv(), csvCircular(), csvRandom() and csvWeighted with given arguments and
     * then verifies if it returns a CsvReaderValue. Also it verifies if
     * the entries returned by this CsvReaderValue are valid records of "test.csv".
     * Furthermore, it checks whether the valueProxies have been set correctly.
     */
    @ParameterizedTest
    @ValueSource(strings = {"csv(\"test.csv\", ',', false)", "csv(\"test.csv\", ',', false, \"\\\\n\", false, '\"', '#', true, null())",
                            "csvCircular(\"test.csv\", ',', false)", "csvCircular(\"test.csv\", ',', false, \"\\\\n\", false, '\"', '#', true, null())",
                            "csvRandom(\"test.csv\", ',', false)", "csvRandom(\"test.csv\", ',', false, \"\\\\n\", false, '\"', '#', true, null())", "csvRandom(\"test.csv\", ',', false, normal())", "csvRandom(\"test.csv\", ',', false, \"\\\\n\", false, '\"', '#', true, null(), normal())" })
    @SuppressWarnings({"rawtypes", "unchecked"})
    void testCreateCsvReaderValueWithoutHeader(String expression) throws IOException {
        //Given
        CSVParser csvParser = CSVFormat.DEFAULT.withHeader("c0", "c1", "c2").parse(new FileReader("src/test/resources/test.csv"));
        List<Map<String, String>> actualRecords = new ArrayList<>();
        csvParser.getRecords().forEach(record -> actualRecords.add(record.toMap()));
        csvParser.close();
        parser.setWorkingDirectoryUrl(UrlUtils.URLof("src/test/resources/"));
        parser.setCurrentPath("test.CSV");      //store the CsvReaderValue under test.CSV

        //When
        Value<?> result = parseRunner.run(expression).valueStack.pop();

        //Then
        if (expression.startsWith("csv("))             { assertThat(result, is(instanceOf(CsvReaderValue.class))); }
        else if (expression.startsWith("csvCircular")) { assertThat(result, is(instanceOf(CircularCsvReaderValue.class))); }
        else if (expression.startsWith("csvRandom"))   { assertThat(result, is(instanceOf(RandomCsvReaderValue.class))); }
        CsvReaderValue csvValue = (CsvReaderValue) result;
        assertThat(valueProxies.keySet(), hasItems("test.CSV.c0", "test.CSV.c1", "test.CSV.c2"));

        Map proxyRecord = new HashMap<>();  //record from valueProxies' contents
        for(int i = 0; i < 20; i++) {
            assertThat(csvValue.get(), is(in(actualRecords)));
            proxyRecord.put("c0", valueProxies.get("test.CSV.c0").get());
            proxyRecord.put("c1", valueProxies.get("test.CSV.c1").get());
            proxyRecord.put("c2", valueProxies.get("test.CSV.c2").get());
            assertThat((Map<String, String>) proxyRecord, is(in(actualRecords)));
            csvValue.reset();
        }

    }

    /*
     * Tests the same but with first record as header
     */
    @ParameterizedTest
    @ValueSource(strings = {"csv(\"test.csv\")", "csv(\"test.csv\", ',')", "csv(\"test.csv\", ',', true)", "csv(\"test.csv\", ',', true, \"\\\\n\", false, '\"', '#', true, null())",
                            "csvCircular(\"test.csv\")", "csvCircular(\"test.csv\", ',')", "csvCircular(\"test.csv\", ',', true)", "csvCircular(\"test.csv\", ',', true, \"\\\\n\", false, '\"', '#', true, null())",
                            "csvRandom(\"test.csv\")", "csvRandom(\"test.csv\", ',')", "csvRandom(\"test.csv\", ',', true)", "csvRandom(\"test.csv\", ',', true, \"\\\\n\", false, '\"', '#', true, null())", "csvRandom(\"test.csv\", normal())", "csvRandom(\"test.csv\", ',', normal())", "csvRandom(\"test.csv\", ',', true, normal())", "csvRandom(\"test.csv\", ',', true, \"\\\\n\", false, '\"', '#', true, null(), normal())",
                            "csvWeighted(\"test.csv\", \"c0\")", "csvWeighted(\"test.csv\", ',', \"c0\")", "csvWeighted(\"test.csv\", ',', true, \"c0\")", "csvWeighted(\"test.csv\", ',', true, \"\\\\n\", false, '\"', '#', true, null(), \"c0\")"})
    @SuppressWarnings({"rawtypes", "unchecked"})
    void testCreateCsvReaderValueWithHeader(String expression) throws IOException {
        //Given
        CSVParser csvParser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(new FileReader("src/test/resources/test.csv"));
        List<Map<String, String>> actualRecords = new ArrayList<>();
        csvParser.getRecords().forEach(record -> actualRecords.add(record.toMap()));
        csvParser.close();
        parser.setWorkingDirectoryUrl(UrlUtils.URLof("src/test/resources/"));
        parser.setCurrentPath("test.CSV");      //store the CsvReaderValue under test.CSV

        //When
        Value<?> result = parseRunner.run(expression).valueStack.pop();

        //Then
        if (expression.startsWith("csv("))             { assertThat(result, is(instanceOf(CsvReaderValue.class))); }
        else if (expression.startsWith("csvCircular")) { assertThat(result, is(instanceOf(CircularCsvReaderValue.class))); }
        else if (expression.startsWith("csvRandom"))   { assertThat(result, is(instanceOf(RandomCsvReaderValue.class))); }
        else if (expression.startsWith("csvWeighted")) { assertThat(result, is(instanceOf(WeightedCsvReaderValue.class))); }
        CsvReaderValue csvValue = (CsvReaderValue) result;
        assertThat(valueProxies.keySet(), hasItems("test.CSV.income", "test.CSV.id", "test.CSV.username"));

        Map proxyRecord = new HashMap<>();  //record from valueProxies' contents
        for(int i = 0; i < 20; i++) {
            assertThat(csvValue.get(), is(in(actualRecords)));
            proxyRecord.put("income", valueProxies.get("test.CSV.income").get());
            proxyRecord.put("id", valueProxies.get("test.CSV.id").get());
            proxyRecord.put("username", valueProxies.get("test.CSV.username").get());
            assertThat((Map<String, String>) proxyRecord, is(in(actualRecords)));
            csvValue.reset();
        }

    }


    //DEPENDENT VALUES

    @Test
    void testSwitchValue() {
        Value<?> switcher = parseRunner.run("switch(random([\"source\"]) => [\"target\"])").valueStack.pop();
        assertThat(switcher, is(instanceOf(SwitchValue.class)));
        assertThat(switcher.get(), is(equalTo("target")));
    }

    @Test
    void testMapValue() {
        Value<?> switcher = parseRunner.run("map(\"source\" => [\"source\":\"target\"])").valueStack.pop();
        assertThat(switcher, is(instanceOf(MapperValue.class)));
        assertThat(switcher.get(), is(equalTo("target")));
    }


    //IMPORT VALUE

    /*
     * Tries to import the given yaml config at 'src/test/resources/test-import.yaml':
     *
     * values:
     *   Entity:
     *     id: 1
     *     name: "Max Mustermann"
     * output: $Entity
     *
     * The test parses the expression import("test-import.yaml") and
     * checks if the returned value is a Map containing the keys 'id' and 'name'.
     * Also, it checks whether the references to the imported value have been added to
     * the valueProxies map.
     *
     */
    @Test
    void testCreateImportedValue() {
        //Given
        Map<String, Object> expectedMap = new LinkedHashMap<>();
        expectedMap.put("id", 1);
        expectedMap.put("name", "Max Mustermann");
        parser.setWorkingDirectoryUrl(UrlUtils.URLof("src/test/resources/config/"));
        parser.setCurrentPath("test.Imported"); //import the yaml to this path

        //When
        valueProxies.put("test.Imported", new ValueProxy<>());
        Value<?> value = parseRunner.run("import(\"test-import.yaml\")").valueStack.pop();

        //Then
        assertThat(value, is(instanceOf(CompositeValue.class)));
        assertThat(value.get(), is(equalTo(expectedMap)));
        assertThat(valueProxies.keySet(), hasItems("test.Imported.id", "test.Imported.name"));
        assertThat(valueProxies.get("test.Imported.id").getDelegate().get(), is(equalTo(1)));
        assertThat(valueProxies.get("test.Imported.name").getDelegate().get(), is(equalTo("Max Mustermann")));
    }


    //TRANSFORMER

    @Test
    void testLowerCaseTransformer() {
        Value<?> lower = parseRunner.run("lower(\"LOREM IPSUM\")").valueStack.pop();
        assertThat(lower, is(instanceOf(CaseTransformer.class)));
        assertThat(lower.get(), is(equalTo("lorem ipsum")));
    }

    @Test
    void testUpperCaseTransformer() {
        Value<?> upper = parseRunner.run("upper(\"lorem ipsum\")").valueStack.pop();
        assertThat(upper, is(instanceOf(CaseTransformer.class)));
        assertThat(upper.get(), is(equalTo("LOREM IPSUM")));
    }

    @Test
    void testAsciiTransformer() {
        Value<?> ascii = parseRunner.run("ascii(\"Alte Würzburger Straße\")").valueStack.pop();
        assertThat(ascii, is(instanceOf(AsciiTransformer.class)));
        assertThat(ascii.get(), is(equalTo("Alte Wuerzburger Strasse")));
    }

    @Test
    void testStringTransformer() {
        Value<?> string = parseRunner.run("string(\"test\")").valueStack.pop();
        assertThat(string, is(instanceOf(StringTransformer.class)));
        assertThat(string.get(), is(equalTo("test")));
    }

    @Test
    void testStringfTransformer() {
        Value<?> stringf = parseRunner.run("stringf(\"test\")").valueStack.pop();
        assertThat(stringf, is(instanceOf(StringfTransformer.class)));
        assertThat(stringf.get(), is(equalTo("test")));
    }

    @Test
    void testTimeFormatTransformer() {
        Value<?> time = parseRunner.run("time(\"--\", now())").valueStack.pop();
        assertThat(time, is(instanceOf(TimeFormatTransformer.class)));
        assertThat(time.get(), is(equalTo("--")));
    }

    @Test
    void testGetterTransformer() {
        valueProxies.put("source", new ValueProxy<>(new CompositeValue(Collections.singletonMap("value", ConstantValue.of("test")))));
        Value<?> get = parseRunner.run("get(\"value\", $source)").valueStack.pop();
        assertThat(get, is(instanceOf(GetterTransformer.class)));
        assertThat(get.get(), is(equalTo("test")));
    }

    @Test
    void testJsonTransformer() {
        Value<?> json = parseRunner.run("json(\"test\")").valueStack.pop();
        assertThat(json, is(instanceOf(JsonTransformer.class)));
        assertThat(json.get(), is(equalTo("\"test\"")));
    }




    //HELPERS

    @Test
    void testReplaceSpecialChars() {
        assertEquals(replaceSpecialChars("\"\\t\""), "\t");
        assertEquals(replaceSpecialChars("\"\\n\""), "\n");
        assertEquals(replaceSpecialChars("\"\\r\""), "\r");
        assertEquals(replaceSpecialChars("\"\\f\""), "\f");
    }

    @Test
    void testStripOffLastReference() {
        assertEquals(stripOffLastReference("test"), "");
        assertEquals(stripOffLastReference("test.sub"), "test");
        assertEquals(stripOffLastReference("test.sub.sub"), "test.sub");
        assertEquals(stripOffLastReference(""), "");
    }


}

