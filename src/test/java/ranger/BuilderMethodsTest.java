package ranger;

import org.junit.jupiter.api.Test;
import ranger.core.*;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static ranger.BuilderMethods.*;

/**
 * This class tests if BuilderMethods creates the right Value with right arguments for each method.
 * It doesn't target testing the Value's function itself.
 */
class BuilderMethodsTest {

    @Test
    void testConstant() {
        ObjectGenerator<String> constant = constant("constant string");
        assertThat(constant.value, is(instanceOf(ConstantValue.class)));
        assertThat(constant.next(), is(equalTo("constant string")));
    }

    @Test
    void testRandom() {
        ObjectGenerator<Integer> random = random(1);
        assertThat(random.value, is(instanceOf(DiscreteValue.class)));
        assertThat(random.next(), is(equalTo(1)));
    }

    @Test
    void testCircular() {
        ObjectGenerator<Integer> circular = circular(1);
        assertThat(circular.value, is(instanceOf(CircularValue.class)));
        assertThat(circular.next(), is(equalTo(1)));
    }

    @Test
    void testRandomWithin() {
        ObjectGenerator<Integer> randomWithin = randomWithin(1, 2);
        assertThat(randomWithin.value, is(instanceOf(RangeValueInt.class)));
        assertThat(randomWithin.next(), is(equalTo(1)));
    }

    @Test
    void testCircularWithin() {
        ObjectGenerator<Integer> circularWithin = circularWithin(1, 2, 1);
        assertThat(circularWithin.value, is(instanceOf(CircularRangeValueInt.class)));
        assertThat(circularWithin.next(), is(oneOf(1, 2)));
    }

    @Test
    void testWeighted() {
        ObjectGenerator<String> weighted = weighted(weightPair("yes", 60));
        assertThat(weighted.value, is(instanceOf(WeightedValue.class)));
        assertThat(weighted.next(), is(equalTo("yes")));
    }

    @Test
    void testExactly() {
        ObjectGenerator<String> exactly = exactly(countPair("yes", 60));
        assertThat(exactly.value, is(instanceOf(ExactWeightedValue.class)));
        assertThat(exactly.next(), is(equalTo("yes")));
    }

    @Test
    void testUniform() {
        assertThat(uniform(), is(instanceOf(UniformDistribution.class)));
    }

    @Test
    void testNormal() {
        assertThat(normal(1, 0.5, 0, 2), is(instanceOf(NormalDistribution.class)));
    }

    @Test
    void testRandomContentString() {
        ObjectGenerator<String> randomContentString = randomContentString(constant(2), range('a', 'a'));
        assertThat(randomContentString.value, is(instanceOf(RandomContentStringValue.class)));
        assertThat(randomContentString.next(), is(equalTo("aa")));
    }


    @Test
    void testXeger() {
        ObjectGenerator<String> xegerString = xeger("abc");
        assertThat(xegerString.value, is(instanceOf(XegerValue.class)));
        assertThat(xegerString.next(), is(equalTo("abc")));
    }

    @Test
    void testNow() {
        assertThat(now().value, is(instanceOf(NowValue.class)));
    }

    @Test
    void testNowDate() {
        assertThat(nowDate().value, is(instanceOf(NowDateValue.class)));
    }

    @Test
    void testNowLocalDate() {
        assertThat(nowLocalDate().value, is(instanceOf(NowLocalDateValue.class)));
    }

    @Test
    void testNowLocalDateTime() {
        assertThat(nowLocalDateTime().value, is(instanceOf(NowLocalDateTimeValue.class)));
    }

    @Test
    void testAdd() {
        ObjectGenerator<Integer> add = add(Integer.class, constant(2.5), constant(2.5));
        assertThat(add.value, is(instanceOf(AdditionValueInteger.class)));
        assertThat(add.next(), is(equalTo(4)));
    }

    @Test
    void testSubtract() {
        ObjectGenerator<Integer> subtract = subtract(Integer.class, constant(2.5), constant(2.5));
        assertThat(subtract.value, is(instanceOf(SubtractionValueInteger.class)));
        assertThat(subtract.next(), is(equalTo(0)));
    }

    @Test
    void testMultiply() {
        ObjectGenerator<Integer> multiply = multiply(Integer.class, constant(2.5), constant(2.5));
        assertThat(multiply.value, is(instanceOf(MultiplicationValueInteger.class)));
        assertThat(multiply.next(), is(equalTo(4)));
    }

    @Test
    void testDivide() {
        ObjectGenerator<Integer> divide = divide(Integer.class, constant(2.5), constant(2.5));
        assertThat(divide.value, is(instanceOf(DivisionValueInteger.class)));
        assertThat(divide.next(), is(equalTo(1)));
    }

    @Test
    void testUuid() {
        assertThat(uuid().value, is(instanceOf(UUIDValue.class)));
    }

    @Test
    void testList() {
        ObjectGenerator<List<Integer>> list = list(1, 2, 3);
        assertThat(list.value, is(instanceOf(ListValue.class)));
        assertThat(list.next(), is(equalTo(Arrays.asList(1, 2, 3))));
    }

    @Test
    void testRandomLengthList() {
        ObjectGenerator<List<Integer>> list = list(2, 3, constant(1));
        assertThat(list.value, is(instanceOf(RandomLengthListValue.class)));
        assertThat(list.next(), is(oneOf(Arrays.asList(1, 1), Arrays.asList(1, 1, 1))));
    }

    @Test
    void testEmptyList() {
        assertThat(emptyList().value, is(instanceOf(EmptyListValue.class)));
    }

    @Test
    void testEmptyMap() {
        assertThat(emptyMap().value, is(instanceOf(EmptyMapValue.class)));
    }

    @Test
    void testCsv() {
        String path = "src/test/resources/test.csv";
        ObjectGenerator<Map<String, String>> csv = csv(path);
        ObjectGenerator<Map<String, String>> csvCircular = csvCircular(path);
        ObjectGenerator<Map<String, String>> csvRandom = csvRandom(path);
        ObjectGenerator<Map<String, String>> csvWeighted = csvWeighted(path, "c0");
        assertThat(csv.value, is(instanceOf(CsvReaderValue.class)));
        assertThat(csvCircular.value, is(instanceOf(CircularCsvReaderValue.class)));
        assertThat(csvRandom.value, is(instanceOf(RandomCsvReaderValue.class)));
        assertThat(csvWeighted.value, is(instanceOf(WeightedCsvReaderValue.class)));
    }


    @Test
    void testSwitcher() {
        ObjectGenerator<String> switcher = switcher(random("source"), "target");
        assertThat(switcher.value, is(instanceOf(SwitchValue.class)));
        assertThat(switcher.next(), is(equalTo("target")));
    }

    @Test
    void testMap() {
        ObjectGenerator<String> map = map(constant("source"), Collections.singletonMap("source", "target"));
        assertThat(map.value, is(instanceOf(MapperValue.class)));
        assertThat(map.next(), is(equalTo("target")));
    }

    @Test
    void testLower() {
        ObjectGenerator<String> lower = lower(constant("LOREM IPSUM"));
        assertThat(lower.value, is(instanceOf(CaseTransformer.class)));
        assertThat(lower.next(), is(equalTo("lorem ipsum")));
    }

    @Test
    void testUpper() {
        ObjectGenerator<String> upper = upper(constant("lorem ipsum"));
        assertThat(upper.value, is(instanceOf(CaseTransformer.class)));
        assertThat(upper.next(), is(equalTo("LOREM IPSUM")));
    }

    @Test
    void testAscii() {
        ObjectGenerator<String> ascii = ascii(constant("Alte Würzburger Straße"));
        assertThat(ascii.value, is(instanceOf(AsciiTransformer.class)));
        assertThat(ascii.next(), is(equalTo("Alte Wuerzburger Strasse")));
    }

    @Test
    void testString() {
        ObjectGenerator<String> string = string("test");
        assertThat(string.value, is(instanceOf(StringTransformer.class)));
        assertThat(string.next(), is(equalTo("test")));
    }

    @Test
    void testStringf() {
        ObjectGenerator<String> stringf = stringf("test");
        assertThat(stringf.value, is(instanceOf(StringfTransformer.class)));
        assertThat(stringf.next(), is(equalTo("test")));
    }

    @Test
    void testTime() {
        ObjectGenerator<String> time = time("--", now());
        assertThat(time.value, is(instanceOf(TimeFormatTransformer.class)));
        assertThat(time.next(), is(equalTo("--")));
    }

    @Test
    void testGet() {
        ObjectGenerator<String> get = get("value", String.class, new ObjectGeneratorBuilder().prop("value", "test").build());
        assertThat(get.value, is(instanceOf(GetterTransformer.class)));
        assertThat(get.next(), is(equalTo("test")));
    }

    @Test
    void testJson() {
        ObjectGenerator<String> json = json(constant("test"));
        assertThat(json.value, is(instanceOf(JsonTransformer.class)));
        assertThat(json.next(), is(equalTo("\"test\"")));
    }
    
}