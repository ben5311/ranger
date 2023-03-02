package ranger.parser;

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import ranger.ObjectGenerator;
import ranger.core.AsciiTransformer;
import ranger.core.CaseTransformer;
import ranger.core.CircularRangeValueFactory;
import ranger.core.CircularValue;
import ranger.core.Composite;
import ranger.core.CompositeValue;
import ranger.core.ConstantValue;
import ranger.core.DiscreteValue;
import ranger.core.EmptyListValue;
import ranger.core.EmptyMapValue;
import ranger.core.ExactWeightedValue;
import ranger.core.ExactWeightedValue.CountValuePair;
import ranger.core.GetterTransformer;
import ranger.core.JsonTransformer;
import ranger.core.ListValue;
import ranger.core.MapperValue;
import ranger.core.NowDateValue;
import ranger.core.NowLocalDateTimeValue;
import ranger.core.NowLocalDateValue;
import ranger.core.NowValue;
import ranger.core.NullValue;
import ranger.core.RandomContentStringValue;
import ranger.core.RandomLengthListValue;
import ranger.core.Range;
import ranger.core.RangeValue;
import ranger.core.RangeValueFactory;
import ranger.core.RangeValueLong;
import ranger.core.StringTransformer;
import ranger.core.StringfTransformer;
import ranger.core.SwitchValue;
import ranger.core.Switchable;
import ranger.core.TimeFormatTransformer;
import ranger.core.UUIDValue;
import ranger.core.Value;
import ranger.core.ValueProxy;
import ranger.core.WeightedValue;
import ranger.core.WeightedValue.WeightedValuePair;
import ranger.core.XegerValue;
import ranger.core.arithmetic.AdditionValueByte;
import ranger.core.arithmetic.AdditionValueDouble;
import ranger.core.arithmetic.AdditionValueFloat;
import ranger.core.arithmetic.AdditionValueInteger;
import ranger.core.arithmetic.AdditionValueLong;
import ranger.core.arithmetic.AdditionValueShort;
import ranger.core.arithmetic.DivisionValueByte;
import ranger.core.arithmetic.DivisionValueDouble;
import ranger.core.arithmetic.DivisionValueFloat;
import ranger.core.arithmetic.DivisionValueInteger;
import ranger.core.arithmetic.DivisionValueLong;
import ranger.core.arithmetic.DivisionValueShort;
import ranger.core.arithmetic.MultiplicationValueByte;
import ranger.core.arithmetic.MultiplicationValueDouble;
import ranger.core.arithmetic.MultiplicationValueFloat;
import ranger.core.arithmetic.MultiplicationValueInteger;
import ranger.core.arithmetic.MultiplicationValueLong;
import ranger.core.arithmetic.MultiplicationValueShort;
import ranger.core.arithmetic.SubtractionValueByte;
import ranger.core.arithmetic.SubtractionValueDouble;
import ranger.core.arithmetic.SubtractionValueFloat;
import ranger.core.arithmetic.SubtractionValueInteger;
import ranger.core.arithmetic.SubtractionValueLong;
import ranger.core.arithmetic.SubtractionValueShort;
import ranger.core.csv.CSVParserSettings;
import ranger.core.csv.CircularCsvReaderValue;
import ranger.core.csv.CsvProxy;
import ranger.core.csv.CsvReaderValue;
import ranger.core.csv.RandomCsvReaderValue;
import ranger.core.csv.WeightedCsvReaderValue;
import ranger.distribution.Distribution;
import ranger.distribution.NormalDistribution;
import ranger.distribution.UniformDistribution;
import ranger.util.UrlUtils;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Parser for configuration value expressions.
 */
public class ValueExpressionParser extends BaseParser<Object> {

    public static final String SWITCH_ERROR = "Can only switch from random(), circular(), weighted(), exactly(), all csvX() and switch() sources";
    private static final String STRING_VALUE_DELIMITER = "stringValueDelimiter";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final Map<String, Value<?>> IMPORTED_YAMLS = new LinkedHashMap<>();    //static Map containing all imported YAMLs by their absolute file path.

    protected final Map<String, ValueProxy<?>> valueProxies;

    protected final RangeValueFactory rangeValueFactory;
    protected final CircularRangeValueFactory circularRangeValueFactory;

    private URL workingDirectoryUrl; //URL to the directory of loaded yaml file
    private String parentPath;  //full json path to the parent yaml element
    private String currentPath; //full json path to current yaml element

    /**
     * Constructs parser with initial <code>valueProxies</code>.
     *
     * @param valueProxies Map containing proxy values by name.
     */
    public ValueExpressionParser(Map<String, ValueProxy<?>> valueProxies) {
        this.valueProxies = valueProxies;
        this.rangeValueFactory = new RangeValueFactory();
        this.circularRangeValueFactory = new CircularRangeValueFactory();
    }

    /**
     * Sets working directory.
     *
     * @param workingDirectoryUrl URL to working directory. Must end with a slash '/'.
     */
    public void setWorkingDirectoryUrl(URL workingDirectoryUrl) {
        this.workingDirectoryUrl = Objects.requireNonNullElseGet(workingDirectoryUrl, () -> UrlUtils.URLof("."));
        if (!UrlUtils.isDirectory(this.workingDirectoryUrl)) { throw new IllegalArgumentException("workingDirectoryUrl must end with a '/' but was: '"+this.workingDirectoryUrl+"'"); }
    }

    /**
     * Sets current json path.
     *
     * @param currentPath json path to the current yaml element.
     */
    public void setCurrentPath(String currentPath) {
        this.currentPath = Objects.requireNonNull(currentPath);
        this.parentPath = stripOffLastReference(currentPath);
    }

    // See https://github.com/sirthias/parboiled/issues/175#issuecomment-1023455789
    public static MethodHandles.Lookup lookup() {
        return MethodHandles.lookup();
    }


    //GRAMMAR RULES

    /**
     * Whitespace definition.
     *
     * @return Whitespace definition rule.
     */
    public Rule whitespace() {
        return AnyOf(" \t");
    }

    /**
     * Newline definition.
     *
     * @return Newline definition rule.
     */
    public Rule newline() {
        return AnyOf("\r\n");
    }

    /**
     * Comma definition.
     *
     * @return Comma definition rule.
     */
    public Rule comma() {
        return Sequence(ZeroOrMore(whitespace()), ",", ZeroOrMore(whitespace()));
    }

    /**
     * Arrow definition.
     *
     * @return Arrow definition rule.
     */
    public Rule arrow() {
        return Sequence(ZeroOrMore(whitespace()), "=>", ZeroOrMore(whitespace()));
    }

    /**
     * Open parenthesis definition.
     *
     * @return Open parenthesis definition rule.
     */
    public Rule openParenthesis() {
        return Sequence(ZeroOrMore(whitespace()), "(", ZeroOrMore(whitespace()));
    }

    /**
     * Closed parenthesis definition.
     *
     * @return Closed parenthesis definition rule.
     */
    public Rule closedParenthesis() {
        return Sequence(ZeroOrMore(whitespace()), ")", ZeroOrMore(whitespace()));
    }

    /**
     * Open bracket definition.
     *
     * @return Open bracket definition rule.
     */
    public Rule openBracket() {
        return Sequence(ZeroOrMore(whitespace()), "[", ZeroOrMore(whitespace()));
    }

    /**
     * Closed bracket definition.
     *
     * @return Closed bracket definition rule.
     */
    public Rule closedBracket() {
        return Sequence(ZeroOrMore(whitespace()), "]", ZeroOrMore(whitespace()));
    }

    /**
     * Sign definition.
     *
     * @return Sign definition rule.
     */
    public Rule sign() {
        return AnyOf("+-");
    }

    /**
     * Letter definition.
     *
     * @return Letter definition rule.
     */
    public Rule letter() {
        return FirstOf('_', CharRange('a', 'z'), CharRange('A', 'Z'));
    }

    /**
     * Digit definition.
     *
     * @return Digit definition rule.
     */
    public Rule digit() {
        return CharRange('0', '9');
    }

    /**
     * Letter or digit definition.
     *
     * @return Letter or digit definition rule.
     */
    public Rule letterOrDigit() {
        return FirstOf(letter(), digit());
    }

    /**
     * Escape sequence definition.
     *
     * @return Escape sequence definition rule.
     */
    public Rule escape() {
        return Sequence('\\', ANY); //AnyOf("btnfr\"'\\"));
    }

    /**
     * Unsigned integer definition.
     *
     * @return Unsigned integer definition rule.
     */
    public Rule unsignedIntegerLiteral() {
        return OneOrMore(digit());
    }

    /**
     * Exponent definition.
     *
     * @return Exponent definition rule.
     */
    public Rule exponent() {
        return Sequence(AnyOf("eE"), Optional(sign()), unsignedIntegerLiteral());
    }

    /**
     * Null value definition.
     *
     * @return Null value definition rule.
     */
    public Rule nullValue() {
        return Sequence("null", openParenthesis(), closedParenthesis(), push(new NullValue()));
    }

    /**
     * Byte definition.
     *
     * @return Byte definition rule.
     */
    public Rule explicitByteLiteral() {
        return Sequence(function("byte", Sequence(Sequence(Optional(sign()), unsignedIntegerLiteral()), push(match()))),
                push(Byte.parseByte((String) pop())));
    }

    /**
     * Short definition.
     *
     * @return Short definition rule.
     */
    public Rule explicitShortLiteral() {
        return Sequence(
                function("short", Sequence(Sequence(Optional(sign()), unsignedIntegerLiteral()), push(match()))),
                push(Short.parseShort((String) pop())));
    }

    /**
     * Implicit integer definition.
     *
     * @return Implicit integer definition rule.
     */
    public Rule implicitIntegerLiteral() {
        return Sequence(Sequence(Optional(sign()), unsignedIntegerLiteral()), ACTION(tryParseInt()));
    }

    /**
     * Explicit integer definition.
     *
     * @return Explicit integer definition rule.
     */
    public Rule explicitIntegerLiteral() {
        return Sequence(function("int", Sequence(Sequence(Optional(sign()), unsignedIntegerLiteral()), push(match()))),
                push(Integer.parseInt((String) pop())));
    }

    /**
     * Integer definition.
     *
     * @return Integer definition rule.
     */
    public Rule integerLiteral() {
        return FirstOf(explicitIntegerLiteral(), implicitIntegerLiteral());
    }

    /**
     * Implicit integer definition.
     *
     * @return Implicit integer definition rule.
     */
    public Rule implicitLongLiteral() {
        return Sequence(Sequence(Optional(sign()), unsignedIntegerLiteral()), push(Long.parseLong(match())));
    }

    /**
     * Explicit integer definition.
     *
     * @return Explicit integer definition rule.
     */
    public Rule explicitLongLiteral() {
        return Sequence(function("long", Sequence(Sequence(Optional(sign()), unsignedIntegerLiteral()), push(match()))),
                push(Long.parseLong((String) pop())));
    }

    /**
     * Long definition.
     *
     * @return Long definition rule.
     */
    public Rule longLiteral() {
        return FirstOf(explicitLongLiteral(), implicitLongLiteral());
    }

    /**
     * Float definition.
     *
     * @return Float definition rule.
     */
    public Rule explicitFloatLiteral() {
        return Sequence(
                function("float", Sequence(Sequence(Optional(sign()),
                        FirstOf(Sequence(unsignedIntegerLiteral(), '.', unsignedIntegerLiteral(), Optional(exponent())),
                                Sequence('.', unsignedIntegerLiteral(), Optional(exponent())),
                                Sequence(unsignedIntegerLiteral(), Optional(exponent())))),
                        push(match()))),
                push(Float.parseFloat((String) pop())));
    }

    /**
     * Implicit double definition.
     *
     * @return Implicit double definition rule.
     */
    public Rule implicitDoubleLiteral() {
        return Sequence(
                Sequence(Optional(sign()),
                        FirstOf(Sequence(unsignedIntegerLiteral(), '.', unsignedIntegerLiteral(), Optional(exponent())),
                                Sequence('.', unsignedIntegerLiteral(), Optional(exponent())))),
                push(Double.parseDouble(match())));
    }

    /**
     * Explicit double definition.
     *
     * @return Explicit double definition rule.
     */
    public Rule explicitDoubleLiteral() {
        return Sequence(
                function("double", Sequence(Sequence(Optional(sign()),
                        FirstOf(Sequence(unsignedIntegerLiteral(), '.', unsignedIntegerLiteral(), Optional(exponent())),
                                Sequence('.', unsignedIntegerLiteral(), Optional(exponent())),
                                Sequence(unsignedIntegerLiteral(), Optional(exponent())))),
                        push(match()))),
                push(Double.parseDouble((String) pop())));
    }

    /**
     * Double definition.
     *
     * @return Double definition rule.
     */
    public Rule doubleLiteral() {
        return FirstOf(explicitDoubleLiteral(), implicitDoubleLiteral());
    }

    /**
     * Number definition.
     *
     * @return Number definition rule.
     */
    public Rule numberLiteral() {
        return FirstOf(explicitByteLiteral(), explicitShortLiteral(), explicitIntegerLiteral(), explicitLongLiteral(),
                explicitFloatLiteral(), explicitDoubleLiteral(), implicitDoubleLiteral(), implicitIntegerLiteral(),
                implicitLongLiteral());
    }

    /**
     * Number value definition.
     *
     * @return Number value definition rule.
     */
    public Rule numberLiteralValue() {
        return Sequence(numberLiteral(), push(ConstantValue.of(pop())));
    }

    /**
     * Date definition.
     *
     * @return Date definition rule.
     */
    public Rule dateLiteral() {
        return Sequence(function("date", stringLiteral()), push(parseDateValue((String) pop())));
    }

    /**
     * Date value definition.
     *
     * @return Date value definition rule.
     */
    public Rule dateLiteralValue() {
        return Sequence(dateLiteral(), push(ConstantValue.of((Date) pop())));
    }

    protected Date parseDateValue(String dateString) {
        try {
            return DATE_FORMAT.parse(dateString);
        } catch (java.text.ParseException e) {
            throw new ParseException("Error parsing date literal", e);
        }
    }

    /**
     * Boolean literal definition.
     *
     * @return Boolean literal definition rule.
     */
    public Rule booleanLiteral() {
        return Sequence(FirstOf(FirstOf("True", "true"), FirstOf("False", "false")),
                push(Boolean.parseBoolean(match())));
    }

    /**
     * Boolean value definition.
     *
     * @return Boolean value definition rule.
     */
    public Rule booleanLiteralValue() {
        return Sequence(booleanLiteral(), push(ConstantValue.of(pop())));
    }

    /**
     * String definition.
     *
     * @return String definition rule.
     */
    public Rule stringLiteral() {
        return FirstOf(singleQuoteStringLiteral(), doubleQuoteStringLiteral());
    }

    /**
     * Character literal definition.
     *
     * @return Character literal definition rule.
     */
    public Rule charLiteral() {
        return Sequence(Sequence('\'', FirstOf(escape(), Sequence(TestNot(AnyOf("'\\")), ANY)), '\''),
                push(match().charAt(1) == '\\' ? match().charAt(2) : match().charAt(1)));
    }

    /**
     * Naked string definition.
     *
     * @return Naked string definition rule.
     * @throws ParseException if it looks like a malformed expression
     */
    public Rule nakedStringLiteral() {
        return Sequence(TestNot(AnyOf("\r\n\"'\\")), ZeroOrMore(ANY), push(getNakedStringLiteral(match())));
    }

    /**
     * throws exception if expression looks like a function but was not recognized by Parser
     */
    protected String getNakedStringLiteral(String expression) {
       if (expression.matches(".*\\w+\\(.*")) {
           throw new ParseException("'"+expression+"' is not a valid expression");
       }
       return expression;
    }

    /**
     * Single quote string definition.
     *
     * @return Single quote string definition rule.
     */
    public Rule singleQuoteStringLiteral() {
        return Sequence(Sequence("'", ZeroOrMore(FirstOf(escape(), Sequence(TestNot(AnyOf("\r\n'\\")), ANY))), "'"),
                push(replaceSpecialChars(match())));
    }

    /**
     * Double quote string definition.
     *
     * @return Double quote string definition rule.
     */
    public Rule doubleQuoteStringLiteral() {
        return Sequence(Sequence('"', ZeroOrMore(FirstOf(escape(), Sequence(TestNot(AnyOf("\r\n\"\\")), ANY))), '"'),
                push(replaceSpecialChars(match())));
    }

    /**
     * String value definition.
     *
     * @return String value definition rule.
     */
    public Rule stringLiteralValue() {
        return Sequence(FirstOf(stringLiteral(), nakedStringLiteral()), push(ConstantValue.of(pop())));
    }

    /**
     * Literal definition.
     *
     * @return Literal definition rule.
     */
    public Rule literalValue() {
        return FirstOf(nullValue(), numberLiteralValue(), booleanLiteralValue(), dateLiteralValue(), stringLiteralValue());
    }

    /**
     * Identifier definition.
     *
     * @return Identifier definition rule.
     */
    public Rule identifier() {
        return Sequence(Sequence(letter(), ZeroOrMore(letterOrDigit())), push(match()));
    }

    /**
     * Identifier definition which does not push match to value stack.
     *
     * @return Identifier definition rule.
     */
    public Rule identifierWithNoPush() {
        return Sequence(letter(), ZeroOrMore(letterOrDigit()));
    }

    /**
     * Number range definition.
     *
     * @return Number range definition rule.
     */
    public Rule numberRange() {
        return Sequence(Sequence(numberLiteral(), "..", numberLiteral()),
                push(createNumberRange((Number) pop(1), (Number) pop())));
    }

    /**
     * Date range definition.
     *
     * @return Number range definition rule.
     */
    public Rule dateRange() {
        return Sequence(Sequence(FirstOf(dateLiteral(), nowDateConstant()), "..", FirstOf(dateLiteral(), nowDateConstant())),
                push(new Range<>((Date) pop(1), (Date) pop())));
    }

    /**
     * Character range definition.
     *
     * @return Character range definition rule.
     */
    public Rule charRange() {
        return Sequence(Sequence(charLiteral(), "..", charLiteral()),
                push(new Range<>((Character) pop(1), (Character) pop())));
    }

    /**
     * Single Character range definition. Creates range containing single Character.
     *
     * @return Character range definition rule.
     */
    public Rule singleCharRange() {
        return Sequence(charLiteral(),
                push(new Range<>((Character) peek(), (Character) pop())));
    }

    /**
     * Function definition.
     *
     * @param functionName Name of a function.
     * @return Function definition rule.
     */
    protected Rule function(String functionName) {
        return function(functionName, fromStringLiteral(""));
    }

    /**
     * Function definition.
     *
     * @param functionArgument Function argument rule.
     * @return Function definition rule.
     */
    protected Rule function(Rule functionArgument) {
        return function("", functionArgument);
    }

    /**
     * Function definition.
     *
     * @param functionName Name of a function.
     * @param functionArgument Function argument rule.
     * @return Function definition rule.
     */
    protected Rule function(String functionName, Rule functionArgument) {
        return Sequence(functionName, openParenthesis(), functionArgument, closedParenthesis());
    }

    /**
     * List of items enclosed in brackets.
     *
     * @param rule Rule of a list item.
     * @return Bracket list definition rule.
     */
    protected Rule bracketList(Rule rule) {
        return Sequence(openBracket(), list(rule), closedBracket());
    }

    /**
     * List of items.
     *
     * @param rule Rule of a list item.
     * @return List definition rule.
     */
    protected Rule list(Rule rule) {
        return Sequence(Sequence(push("args"), Optional(rule, ZeroOrMore(comma(), rule))),
                push(getItemsUpToDelimiter("args")));
    }

    /**
     * Value reference definition.
     *
     * @return Value reference definition rule.
     */
    public Rule valueReference() {
        return Sequence('$', Sequence(Sequence(identifierWithNoPush(), ZeroOrMore('.', identifierWithNoPush())),
                push(getValueProxy(match()))));
    }

    /**
     * reference name definition.
     *
     * @return reference name definition rule.
     */
    public Rule referenceName() {
        return Sequence('$', Sequence(Sequence(identifierWithNoPush(), ZeroOrMore('.', identifierWithNoPush())),
                push(match())));
    }



    //DISTRIBUTIONS

    /**
     * Uniform distribution definition.
     *
     * @return Uniform distribution definition rule.
     */
    public Rule uniformDistribution() {
        return Sequence(function("uniform"), push(new UniformDistribution()));
    }

    /**
     * Normal distribution definition.
     *
     * @return Normal distribution definition rule.
     */
    public Rule normalDistribution() {
        return Sequence(function("normal", list(numberLiteral())), push(createNormalDistribution()));
    }

    /**
     * Distribution definition.
     *
     * @return Distribution definition rule.
     */
    public Rule distribution() {
        return FirstOf(uniformDistribution(), normalDistribution());
    }



    //RANDOM AND CIRCULAR

    /**
     * Discrete value definition.
     *
     * @return Discrete value definition rule.
     */
    public Rule discreteValue() {
        return Sequence(function("random", Sequence(bracketList(value()), Optional(comma(), distribution()))),
                push(createDiscreteValue()));
    }

    /**
     * Long range value definition.
     *
     * @return Long range value definition rule.
     */
    public Rule rangeValue() {
        return Sequence(
                function("random",
                        Sequence(FirstOf(numberRange(), charRange(), dateRange()), Optional(comma(), booleanLiteral(), Optional(comma(), distribution())))),
                push(createRangeValue()));
    }

    /**
     * Circular value definition.
     *
     * @return Circular value definition rule.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Rule circularValue() {
        return Sequence(function("circular", bracketList(value())), push(new CircularValue((List<?>) pop())));
    }

    /**
     * Circular range value definition.
     *
     * @return Circular range value definition rule.
     */
    public Rule circularRangeValue() {
        return Sequence(function("circular", Sequence(FirstOf(numberRange(), charRange(), dateRange()), comma(), numberLiteral())),
                push(circularRangeValueFactory.create((Range<?>) pop(1), (Number) pop())));
    }



    //WEIGHTED AND EXACTLY

    /**
     * Weighted value pair definition.
     *
     * @return Weighted value pair definition rule.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Rule weightedValuePair() {
        return Sequence(function(Sequence(value(), comma(), numberLiteral())),
                push(new WeightedValuePair((Value<?>) pop(1), ((Number) pop()).doubleValue())));
    }

    /**
     * Weighted value definition.
     *
     * @return Weighted value definition rule.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Rule weightedValue() {
        return Sequence(function("weighted", bracketList(weightedValuePair())), push(new WeightedValue((List<?>) pop())));
    }

    /**
     * Count value pair definition.
     *
     * @return Count value pair definition rule.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Rule countValuePair() {
        return Sequence(function(Sequence(value(), comma(), longLiteral())),
                push(new CountValuePair((Value<?>) pop(1), (Long) pop())));
    }

    /**
     * Exact weighted value definition.
     *
     * @return Weighted value definition rule.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Rule exactWeightedValue() {
        return Sequence(function("exactly", bracketList(countValuePair())),
                push(new ExactWeightedValue<>((List) pop())));
    }



    //RANDOM STRINGS

    /**
     * Random content value definition.
     *
     * @return Random content value definition rule.
     */
    public Rule randomContentStringValue() {
        return Sequence(function("randomContentString", Sequence(value(), Optional(comma(), bracketList(FirstOf(charRange(), singleCharRange()))))),
                push(createRandomContentStringValue()));
    }

    /**
     * Xeger value definition.
     *
     * @return Random content value definition rule.
     */
    @SuppressWarnings("unchecked")
    public Rule xegerValue() {
        return Sequence(function("xeger", value()),
                push(new XegerValue((Value<String>) pop())));
    }



    //TIME

    /**
     * Now definition.
     *
     * @return Now definition rule.
     */
    public Rule now() {
        return Sequence(function("now"), push(new NowValue()));
    }

    /**
     * Now date literal definition.
     *
     * @return Now date definition rule.
     */
    public Rule nowDateConstant() {
        return Sequence(function("nowDate"), push(new Date()));
    }

    /**
     * Now date definition.
     *
     * @return Now date definition rule.
     */
    public Rule nowDate() {
        return Sequence(function("nowDate"), push(new NowDateValue()));
    }

    /**
     * Now local date definition.
     *
     * @return Now local date definition rule.
     */
    public Rule nowLocalDate() {
        return Sequence(function("nowLocalDate"), push(new NowLocalDateValue()));
    }

    /**
     * Now local date time definition.
     *
     * @return Now local date time definition rule.
     */
    public Rule nowLocalDateTime() {
        return Sequence(function("nowLocalDateTime"), push(new NowLocalDateTimeValue()));
    }



    //ARITHMETIC OPERATIONS

    /**
     * Addition value definition.
     *
     * @return Addition value definition rule.
     */
    public Rule additionValue() {
        return Sequence(function("add", Sequence(stringLiteral(), comma(), value(), comma(), value())),
                push(createAdditionValue()));
    }

    /**
     * Subtraction value definition.
     *
     * @return Subtraction value definition rule.
     */
    public Rule subtractionValue() {
        return Sequence(function("subtract", Sequence(stringLiteral(), comma(), value(), comma(), value())),
                push(createSubtractionValue()));
    }

    /**
     * Multiplication value definition.
     *
     * @return Multiplication value definition rule.
     */
    public Rule multiplicationValue() {
        return Sequence(function("multiply", Sequence(stringLiteral(), comma(), value(), comma(), value())),
                push(createMultiplicationValue()));
    }

    /**
     * Division value definition.
     *
     * @return Division value definition rule.
     */
    public Rule divisionValue() {
        return Sequence(function("divide", Sequence(stringLiteral(), comma(), value(), comma(), value())),
                push(createDivisionValue()));
    }



    /**
     * UUID value definition.
     *
     * @return UUID value definition rule.
     */
    public Rule uuidValue() {
        return Sequence(function("uuid"), push(new UUIDValue()));
    }



    //COMPLEX VALUES

    /**
     * Merge value definition.
     *
     * @return Merge value definition rule.
     */
    @SuppressWarnings({ "unchecked"})
    public Rule mergeValue() {
        return Sequence(function("merge", bracketList(referenceName())), push(createMergedValue((List<String>) pop())));
    }

    /**
     * List value definition.
     *
     * @return List value definition rule.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Rule listValue() {
        return Sequence(function("list", bracketList(value())), push(new ListValue((List<?>) pop())));
    }

    /**
     * Random length list value definition.
     *
     * @return Random length list value definition rule.
     */
    public Rule randomLengthListValue() {
        return Sequence(function("list", Sequence(numberLiteral(), comma(), numberLiteral(), comma(), value(),
                Optional(comma(), distribution()))), push(createRandomLengthListValue()));
    }

    /**
     * Empty list value definition.
     *
     * @return Empty list value definition rule.
     */
    @SuppressWarnings({ "rawtypes" })
    public Rule emptyListValue() {
        return Sequence(function("emptyList"), push(new EmptyListValue()));
    }

    /**
     * Empty map value definition.
     *
     * @return Empty map value definition rule.
     */
    @SuppressWarnings({ "rawtypes" })
    public Rule emptyMapValue() {
        return Sequence(function("emptyMap"), push(new EmptyMapValue()));
    }



    //CSV

    /**
     * CSV value definition.
     *
     * @return CSV value definition rule.
     */
    public Rule csvReaderValue() {
        return Sequence(
                function("csv",
                        FirstOf(Sequence(stringLiteral(), comma(), charLiteral(), comma(), booleanLiteral(), comma(), stringLiteral(), comma(), booleanLiteral(), comma(), FirstOf(nullValue(), charLiteral()), comma(), charLiteral(), comma(), booleanLiteral(), comma(), FirstOf(nullValue(), stringLiteral())),
                                Sequence(stringLiteral(), comma(), charLiteral(), comma(), booleanLiteral()),
                                Sequence(stringLiteral(), comma(), charLiteral()),
                                stringLiteral())),
                push(createCsvReaderValue()));
    }

    /**
     * CSV value definition.
     *
     * @return CSV value definition rule.
     */
    public Rule csvCircularReaderValue() {
        return Sequence(
                function("csvCircular",
                        FirstOf(Sequence(stringLiteral(), comma(), charLiteral(), comma(), booleanLiteral(), comma(), stringLiteral(), comma(), booleanLiteral(), comma(), FirstOf(nullValue(), charLiteral()), comma(), charLiteral(), comma(), booleanLiteral(), comma(), FirstOf(nullValue(), stringLiteral())),
                                Sequence(stringLiteral(), comma(), charLiteral(), comma(), booleanLiteral()),
                                Sequence(stringLiteral(), comma(), charLiteral()),
                                stringLiteral())),
                push(createCircularCsvReaderValue()));
    }

    /**
     * CSV value definition.
     *
     * @return CSV value definition rule.
     */
    public Rule csvRandomReaderValue() {
        return Sequence(
                function("csvRandom",
                        FirstOf(Sequence(stringLiteral(), comma(), charLiteral(), comma(), booleanLiteral(), comma(), stringLiteral(), comma(), booleanLiteral(), comma(), FirstOf(nullValue(), charLiteral()), comma(), charLiteral(), comma(), booleanLiteral(), comma(), FirstOf(nullValue(), stringLiteral()), Optional(comma(), distribution())),
                                Sequence(stringLiteral(), comma(), charLiteral(), comma(), booleanLiteral(), Optional(comma(), distribution())),
                                Sequence(stringLiteral(), comma(), charLiteral(), Optional(comma(), distribution())),
                                Sequence(stringLiteral(), Optional(comma(), distribution())))),
                push(createRandomCsvReaderValue()));
    }

    /**
     * CSV value definition.
     *
     * @return CSV value definition rule.
     */
    public Rule csvWeightedReaderValue() {
        return Sequence(
                function("csvWeighted",
                        FirstOf(Sequence(stringLiteral(), comma(), charLiteral(), comma(), booleanLiteral(), comma(), stringLiteral(), comma(), booleanLiteral(), comma(), FirstOf(nullValue(), charLiteral()), comma(), charLiteral(), comma(), booleanLiteral(), comma(), FirstOf(nullValue(), stringLiteral()), comma(), stringLiteral()),
                                Sequence(stringLiteral(), comma(), charLiteral(), comma(), booleanLiteral(), comma(), stringLiteral()),
                                Sequence(stringLiteral(), comma(), charLiteral(), comma(), stringLiteral()),
                                Sequence(stringLiteral(), comma(), stringLiteral()))),
                push(createWeightedCsvReaderValue()));
    }



    //DEPENDENT VALUES

    /**
     * Switch value definition.
     *
     * @return Switch value definition rule.
     */
    @SuppressWarnings({"unchecked"})
    public Rule switchValue() {
        return Sequence(function("switch", Sequence(value(), arrow(), bracketList(value()))),
                push(createSwitchValue((Value<?>) pop(1), (List<Value<?>>) pop())));
    }

    /**
     * Key value pair definition
     * @return Key value pair definition rule
     */
    public Rule keyValuePair() {
        return Sequence(value(), ":", value(),
                push(new MapperValue.KeyValuePair<>((Value<?>) pop(1), (Value<?>) pop())));
    }

    /**
     * Mapper value definition.
     *
     * @return Mapper value definition rule.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Rule mapValue() {
        return Sequence(function("map", Sequence(value(), arrow(), bracketList(keyValuePair()))),
                push(new MapperValue((Value<?>) pop(1), (List<MapperValue.KeyValuePair<?>>) pop())));
    }



    //IMPORT YAML

    /**
     * Import value definition.
     *
     * @return Import value definition rule.
     */
    public Rule importValue() {
        return Sequence(function("import", Sequence(stringLiteral(), Optional(comma(), stringLiteral()))), push(createImportedValue()));
    }



    //All Functions Rule


    /**
     * Generator definition.
     *
     * @return Generator definition rule.
     */
    public Rule generator() {
        return FirstOf(discreteValue(), rangeValue(), uuidValue(), circularValue(), circularRangeValue(),
                importValue(), mergeValue(), listValue(), emptyListValue(), emptyMapValue(),
                randomLengthListValue(), weightedValue(), exactWeightedValue(), switchValue(), mapValue(),
                randomContentStringValue(), xegerValue(), now(), nowDate(), nowLocalDate(), nowLocalDateTime(), additionValue(),
                subtractionValue(), multiplicationValue(), divisionValue(), csvReaderValue(),
                csvCircularReaderValue(), csvRandomReaderValue(), csvWeightedReaderValue());
    }



    //TRANSFORMERS


    /**
     * Case transformer definition.
     *
     * @return Case transformer definition rule.
     */
    @SuppressWarnings("unchecked")
    public Rule caseTransformer() {
        return FirstOf(Sequence(function("lower", value()), push(new CaseTransformer((Value<String>) pop(), false))),
                Sequence(function("upper", value()), push(new CaseTransformer((Value<String>) pop(), true))));
    }

    /**
     * Ascii transformer definition.
     *
     * @return Ascii transformer definition rule.
     */
    @SuppressWarnings("unchecked")
    public Rule asciiTransformer() {
        return Sequence(function("ascii", value()), push(new AsciiTransformer((Value<String>) pop())));
    }

    /**
     * String transformer definition.
     *
     * @return String transformer definition rule.
     */
    public Rule stringTransformer() {
        return Sequence(Sequence("string", openParenthesis(), value(), push(STRING_VALUE_DELIMITER),
                ZeroOrMore(comma(), value()), closedParenthesis()), push(getStringValue()));
    }

    /**
     * Stringf transformer definition.
     *
     * @return String transformer definition rule.
     */
    public Rule stringfTransformer() {
        return Sequence(Sequence("stringf", openParenthesis(), value(), push(STRING_VALUE_DELIMITER),
                ZeroOrMore(comma(), value()), closedParenthesis()), push(getStringfValue()));
    }

    /**
     * Time format transformer definition.
     *
     * @return Time format transformer definition rule.
     */
    @SuppressWarnings({ "unchecked" })
    public Rule timeFormatTransformer() {
        return Sequence(function("time", Sequence(value(), comma(), value())),
                push(new TimeFormatTransformer((Value<String>) pop(1), (Value<?>) pop())));
    }

    /**
     * Getter transformer definition.
     *
     * @return Getter transformer definition rule.
     */
    public Rule getterTransformer() {
        return Sequence(function("get", Sequence(stringLiteral(), comma(), value())),
                push(new GetterTransformer<>((String) pop(1), Object.class, (Value<?>) pop())));
    }

    /**
     * JSON transformer definition.
     *
     * @return JSON transformer definition rule.
     */
    public Rule jsonTransformer() {
        return Sequence(function("json", value()), push(new JsonTransformer((Value<?>) pop())));
    }



    //CLONE


    /**
     * clone Value definition.
     *
     * @return clone Value definition rule.
     */
    public Rule cloneValue() {
        return Sequence("$$", Sequence(Sequence(identifierWithNoPush(), ZeroOrMore('.', identifierWithNoPush())),
                push(createClonedValue(match()))));
    }




    //All Transformers Rule

    /**
     * Transformer definition.
     *
     * @return Transformer definition rule.
     */
    public Rule transformer() {
        return FirstOf(caseTransformer(), asciiTransformer(), stringTransformer(), stringfTransformer(), jsonTransformer(), timeFormatTransformer(), getterTransformer());
    }



    //All Values Rule

    /**
     * Value definition.
     *
     * @return Value definition rule.
     */
    public Rule value() {
        return FirstOf(valueReference(), cloneValue(), generator(), transformer(), literalValue());
    }






    //HELPER METHODS//




    //DISTRIBUTIONS, RANDOM AND CIRCULAR


    /**
     * Creates normal distribution.
     *
     * @return Instance of {@link NormalDistribution}.
     * @throws ParseException if called with incorrect number of arguments
     */
    @SuppressWarnings({ "unchecked"})
    protected NormalDistribution createNormalDistribution() {
        List<Number> args = (List<Number>) pop();
        if (args.isEmpty()) {
            return new NormalDistribution();
        }
        if (args.size() != 4) {
            throw new ParseException("Normal distribution must have following parameters:"
                    + " mean, standard deviation, lower bound and upper bound.");
        }
        return new NormalDistribution(args.get(0).doubleValue(), args.get(1).doubleValue(), args.get(2).doubleValue(),
                args.get(3).doubleValue());
    }

    /**
     * Creates discrete value.
     *
     * @return Instance of {@link DiscreteValue}.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected DiscreteValue<?> createDiscreteValue() {
        return peek() instanceof Distribution ? new DiscreteValue((List<Value<?>>) pop(1), (Distribution) pop())
                : new DiscreteValue((List<Value<?>>) pop());
    }

    /**
     * Creates appropriate number range depending on number types.
     *
     * @param beginning Beginning of the range.
     * @param end End of the range.
     * @return An instance of {@link Range}.
     */
    protected Range<?> createNumberRange(Number beginning, Number end) {
        if (beginning instanceof Double || end instanceof Double) {
            return new Range<>(beginning.doubleValue(), end.doubleValue());
        }
        if (beginning instanceof Float || end instanceof Float) {
            return new Range<>(beginning.floatValue(), end.floatValue());
        }
        if (beginning instanceof Long || end instanceof Long) {
            return new Range<>(beginning.longValue(), end.longValue());
        }
        if (beginning instanceof Integer || end instanceof Integer) {
            return new Range<>(beginning.intValue(), end.intValue());
        }
        if (beginning instanceof Short || end instanceof Short) {
            return new Range<>(beginning.shortValue(), end.shortValue());
        }
        if (beginning instanceof Byte || end instanceof Byte) {
            return new Range<>(beginning.byteValue(), end.byteValue());
        }
        throw new ParseException("Unsupported number type: " + beginning.getClass().getName());
    }

    /**
     * Creates long range value.
     *
     * @return Instance of {@link RangeValueLong}.
     */
    protected RangeValue<?> createRangeValue() {
        Distribution dist = peek() instanceof Distribution ? (Distribution) pop() : null;
        Boolean useEdgeCases = peek() instanceof Boolean ? (Boolean) pop() : null;
        Range<?> range = (Range<?>) pop();
        return rangeValueFactory.create(range, useEdgeCases, dist);
    }



    //RANDOM STRING


    /**
     * Creates random content value.
     *
     * @return Instance of {@link RandomContentStringValue}.
     */
    @SuppressWarnings({ "unchecked" })
    protected RandomContentStringValue createRandomContentStringValue() {
        return peek() instanceof List ? new RandomContentStringValue((Value<Integer>) pop(1), (List<Range<Character>>) pop())
                : new RandomContentStringValue((Value<Integer>) pop());
    }



    //ARITHMETIC OPERATIONS


    /**
     * Creates addition value.
     *
     * @return An addition value.
     */
    @SuppressWarnings("unchecked")
    protected Value<?> createAdditionValue() {
        String type = (String) pop(2);
        Value<? extends Number> summand1 = (Value<? extends Number>) pop(1);
        Value<? extends Number> summand2 = (Value<? extends Number>) pop();
        switch (type) {
            case "byte":
                return new AdditionValueByte(summand1, summand2);
            case "short":
                return new AdditionValueShort(summand1, summand2);
            case "int":
                return new AdditionValueInteger(summand1, summand2);
            case "long":
                return new AdditionValueLong(summand1, summand2);
            case "float":
                return new AdditionValueFloat(summand1, summand2);
            case "double":
                return new AdditionValueDouble(summand1, summand2);
            default:
                throw new ParseException("Unsupported type for addition value. Type: " + type);
        }
    }

    /**
     * Creates subtraction value.
     *
     * @return An subtraction value.
     */
    @SuppressWarnings("unchecked")
    protected Value<?> createSubtractionValue() {
        String type = (String) pop(2);
        Value<? extends Number> minuend = (Value<? extends Number>) pop(1);
        Value<? extends Number> subtrahend = (Value<? extends Number>) pop();
        switch (type) {
            case "byte":
                return new SubtractionValueByte(minuend, subtrahend);
            case "short":
                return new SubtractionValueShort(minuend, subtrahend);
            case "int":
                return new SubtractionValueInteger(minuend, subtrahend);
            case "long":
                return new SubtractionValueLong(minuend, subtrahend);
            case "float":
                return new SubtractionValueFloat(minuend, subtrahend);
            case "double":
                return new SubtractionValueDouble(minuend, subtrahend);
            default:
                throw new ParseException("Unsupported type for subtraction value. Type: " + type);
        }
    }

    /**
     * Creates multiplication value.
     *
     * @return An multiplication value.
     */
    @SuppressWarnings("unchecked")
    protected Value<?> createMultiplicationValue() {
        String type = (String) pop(2);
        Value<? extends Number> factor1 = (Value<? extends Number>) pop(1);
        Value<? extends Number> factor2 = (Value<? extends Number>) pop();
        switch (type) {
            case "byte":
                return new MultiplicationValueByte(factor1, factor2);
            case "short":
                return new MultiplicationValueShort(factor1, factor2);
            case "int":
                return new MultiplicationValueInteger(factor1, factor2);
            case "long":
                return new MultiplicationValueLong(factor1, factor2);
            case "float":
                return new MultiplicationValueFloat(factor1, factor2);
            case "double":
                return new MultiplicationValueDouble(factor1, factor2);
            default:
                throw new ParseException("Unsupported type for multplication value. Type: " + type);
        }
    }

    /**
     * Creates division value.
     *
     * @return An division value.
     */
    @SuppressWarnings("unchecked")
    protected Value<?> createDivisionValue() {
        String type = (String) pop(2);
        Value<? extends Number> dividend = (Value<? extends Number>) pop(1);
        Value<? extends Number> divisor = (Value<? extends Number>) pop();
        switch (type) {
            case "byte":
                return new DivisionValueByte(dividend, divisor);
            case "short":
                return new DivisionValueShort(dividend, divisor);
            case "int":
                return new DivisionValueInteger(dividend, divisor);
            case "long":
                return new DivisionValueLong(dividend, divisor);
            case "float":
                return new DivisionValueFloat(dividend, divisor);
            case "double":
                return new DivisionValueDouble(dividend, divisor);
            default:
                throw new ParseException("Unsupported type for division value. Type: " + type);
        }
    }



    //COMPLEX VALUES

    /**
     * Creates a merged Value out of given references
     * @return Instance of {@link CompositeValue}
     * @throws ParseException if an object referenced in referenceNames is not a CompositeValue
     */
    protected CompositeValue createMergedValue(List<String> referenceNames) {
        List<Composite<?>> composites = new ArrayList<>();
        for (String ref : referenceNames) {
            ValueProxy<?> valueProxy = getValueProxy(ref);
            if (!(valueProxy.getDelegate() instanceof Composite)) {
                throw new ParseException("Can only merge composite objects but not simple values");
            }
            Composite<?> composite = (Composite<?>) valueProxy.getDelegate();
            composites.add(composite);
            for (String key : valueProxies.keySet().toArray(new String[]{})) {
                if (key.startsWith(ref+".")) {
                    valueProxies.put(key.replaceFirst(ref, currentPath), valueProxies.get(key)); //add reference for merged object
                }
            }
        }
        return new CompositeValue(composites);
    }


    /**
     * Creates random length list value.
     *
     * @return Instance of {@link RandomLengthListValue}.
     */
    protected RandomLengthListValue<?> createRandomLengthListValue() {
        Distribution dist = peek() instanceof Distribution ? (Distribution) pop() : null;
        Value<?> elementGenerator = (Value<?>) pop();
        int maxLength = (int) pop();
        int minLength = (int) pop();
        if (dist == null) {
            return new RandomLengthListValue<>(minLength, maxLength, elementGenerator);
        } else {
            return new RandomLengthListValue<>(minLength, maxLength, elementGenerator, dist);
        }
    }



    //CSV

    /**
     * Creates sequential CSV value.
     *
     * @return An CSV value.
     */
    protected CsvReaderValue createCsvReaderValue() {
        CSVParserSettings parserSettings = getCsvParserSettings();
        CsvReaderValue parsedValue = new CsvReaderValue(parserSettings);
        addCsvValueProxies(currentPath, parsedValue.getCsvProxies());
        return parsedValue;
    }

    /**
     * Creates circular CSV value.
     *
     * @return An CSV value.
     */
    protected CircularCsvReaderValue createCircularCsvReaderValue() {
        CSVParserSettings parserSettings = getCsvParserSettings();
        CircularCsvReaderValue parsedValue = new CircularCsvReaderValue(parserSettings);
        addCsvValueProxies(currentPath, parsedValue.getCsvProxies());
        return parsedValue;
    }

    /**
     * Creates random CSV value.
     *
     * @return An CSV value.
     */
    protected RandomCsvReaderValue createRandomCsvReaderValue() {
        Distribution distribution = new UniformDistribution();
        if (peek() instanceof Distribution) { distribution = (Distribution) pop(); }
        CSVParserSettings parserSettings = getCsvParserSettings();
        RandomCsvReaderValue parsedValue = new RandomCsvReaderValue(parserSettings, distribution);
        addCsvValueProxies(currentPath, parsedValue.getCsvProxies());
        return parsedValue;
    }

    /**
     * Creates weighted CSV value.
     *
     * @return An CSV value.
     */
    protected WeightedCsvReaderValue createWeightedCsvReaderValue() {
        String weightField = (String) pop();
        CSVParserSettings parserSettings = getCsvParserSettings();
        WeightedCsvReaderValue parsedValue = new WeightedCsvReaderValue(parserSettings, weightField);
        addCsvValueProxies(currentPath, parsedValue.getCsvProxies());
        return parsedValue;
    }

    /**
     * Adds proxies for CSV columns
     */
    protected void addCsvValueProxies(String currentPath, Map<String, CsvProxy> csvProxies) {
        csvProxies.forEach((key, csvProxy) -> this.valueProxies.put(currentPath + "." + key, new ValueProxy<>(csvProxy)));
    }

    /**
     * Parses the arguments from csv()- and csvCircular()-method.
     * @return Instance of CSVParserSettings.
     */
    protected CSVParserSettings getCsvParserSettings() {
        CSVParserSettings parserSettings;
        String csvPath = (String) pop(getContext().getValueStack().size()-1);
        URL csvUrl;
        if (!new File(csvPath).isAbsolute()) {   //prepend working directory to relative path
            csvUrl = UrlUtils.resolve(workingDirectoryUrl, csvPath);
        } else {
            csvUrl = UrlUtils.URLof(csvPath);
        }
        switch (getContext().getValueStack().size()) {
            case 0:
                parserSettings = new CSVParserSettings(csvUrl);
                break;
            case 1:
                parserSettings = new CSVParserSettings(csvUrl, (char) pop());
                break;
            case 2:
                parserSettings = new CSVParserSettings(csvUrl, (char) pop(1), (boolean) pop());
                break;
            case 8:
                parserSettings = new CSVParserSettings(csvUrl, (char) pop(7), (boolean) pop(6), (String) pop(5), (boolean) pop(4), peek(3) instanceof NullValue ? null : (Character) pop(3), (char) pop(2), (boolean) pop(1), peek(0) instanceof NullValue ? null : (String) pop(0));
                break;
            default:
                throw new ParseException("Unsupported number of parameters, should not happen ever.");
        }
        return parserSettings;
    }



    //DEPENDENT VALUES

    /**
     * Creates Switch value that selects a value of it's arguments that relates to source's randomly selected value
     * @param source source object of type Switchable
     * @param arguments the arguments to select of
     * @return Instance of Switchable
     * @throws ParseException if source is null or not declared prior to this SwitchValue or if source is not a Switchable
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected SwitchValue<?> createSwitchValue(Value<?> source, List<Value<?>> arguments) {
        while (source instanceof ValueProxy) {  //unwrap value out of ValueProxy
            source = ((ValueProxy<?>) source).getDelegate();
        }
        if (source == null) { throw new ParseException("Reference for switch() must be initialized first."); }
        if (!(source instanceof Switchable)) { throw new ParseException(SWITCH_ERROR); }
        return new SwitchValue((Switchable<?>) source, arguments);
    }



    //TRANSFORMER

    /**
     * Constructs {@link StringTransformer}.
     *
     * @return Instance of {@link StringTransformer}.
     */
    @SuppressWarnings({ "unchecked" })
    protected Value<String> getStringValue() {
        List<Value<?>> values = getItemsUpToDelimiter(STRING_VALUE_DELIMITER);
        Value<String> formatValue = (Value<String>) pop();
        return new StringTransformer(formatValue, values);
    }

    /**
     * Constructs {@link StringfTransformer}.
     *
     * @return Instance of {@link StringfTransformer}.
     */
    @SuppressWarnings({ "unchecked" })
    protected Value<String> getStringfValue() {
        List<Value<?>> values = getItemsUpToDelimiter(STRING_VALUE_DELIMITER);
        Value<String> formatValue = (Value<String>) pop();
        return new StringfTransformer(formatValue, values);
    }



    //IMPORT YAML

    /**
     * Parses the supplied arguments and then calls createImportedValue(configPath, yamlRoot)
     */
    protected Value<?> createImportedValue() {
        if (getContext().getValueStack().size() == 1) {     //function was called with one argument, so set yamlRoot to default
            return createImportedValue((String) pop(), "$.");
        } else {    //function was called with two arguments
            return createImportedValue((String) pop(1), (String) pop());
        }
    }

    /**
     * Imports a yaml config and returns a Value object containing all elements specified in the given config
     * @return a {@code Value} that generates objects as defined in the specified yaml file
     * @throws ParseException if any exception occurs while reading or parsing the config file
     */
    protected Value<?> createImportedValue(String configPath, String yamlRoot) {
        String fullyQualifiedPath;   //convert path to it's unique representation for IMPORTED_YAMLS map
        if (configPath.startsWith(":") || new File(configPath).isAbsolute()) {  //path is already unique
            fullyQualifiedPath = configPath;
        } else {    //path is relative so prepend working directory
            fullyQualifiedPath = UrlUtils.resolve(workingDirectoryUrl, configPath).getPath();
        }
        if (!IMPORTED_YAMLS.containsKey(fullyQualifiedPath)) {     //if YAML was not imported before, parse this yaml and add it to IMPORTED_YAMLs
            try {
                URL configUrl;
                if (configPath.startsWith(":") || new File(configPath).isAbsolute()) {   //resolve predefined yaml paths and absolute paths
                    configUrl = UrlUtils.URLof(configPath);
                } else {    //path is relative so prepend working directory URL
                    configUrl = UrlUtils.resolve(workingDirectoryUrl, configPath);
                }
                ObjectGenerator<?> generator = new ConfigurationParser(configUrl, yamlRoot).build();
                Value<?> value = generator.getValue();
                while (value instanceof ValueProxy) {  //unwrap value if wrapped in proxy
                    value = ((ValueProxy<?>) value).getDelegate();
                }
                IMPORTED_YAMLS.put(fullyQualifiedPath, value);
            } catch (RuntimeException e) {  //rethrow unchecked RuntimeExceptions
                throw e;
            } catch (Exception e) {  //wrap and rethrow checked Exceptions
                throw new ParseException(e);
            }
        }
        Value<?> importedValue = IMPORTED_YAMLS.get(fullyQualifiedPath);
        addSubValueProxies(importedValue);
        return importedValue;
    }

    /**
     * Add inner attributes from composite to valueProxies.
     */
    @SuppressWarnings("DuplicatedCode")
    private <T> void addSubValueProxies(Value<T> composite) {
        if (composite instanceof CsvReaderValue) {              //add values's inner proxy values
            CsvReaderValue csvValue = (CsvReaderValue) composite;
            addCsvValueProxies(currentPath, csvValue.getCsvProxies());
        } else if (composite instanceof CompositeValue) {               //add values's inner proxy values
            CompositeValue compositeValue = (CompositeValue) composite;
            Map<String, Value<?>> values = compositeValue.getValues();
            for (String key : values.keySet()) {
                ValueProxy<?> subProxy = values.get(key) instanceof ValueProxy ? (ValueProxy<?>) values.get(key) : new ValueProxy<>(values.get(key));
                addValueProxiesRecursively(currentPath+"."+key, subProxy);
            }
        }
    }

    /**
     * Add valueProxy itself and all it's inner attributes to valueProxies.
     */
    @SuppressWarnings("DuplicatedCode")
    private void addValueProxiesRecursively(String currentPath, ValueProxy<?> valueProxy) {
        if (valueProxy == null) { return; }
        Value<?> delegate = valueProxy.getDelegate();
        if (delegate instanceof ValueProxy) {    //demultiplex nested ValueProxies
            addValueProxiesRecursively(currentPath, (ValueProxy<?>) delegate);
        } else {
            this.valueProxies.put(currentPath, valueProxy);
            if (delegate instanceof CsvReaderValue) {
                CsvReaderValue csvValue = (CsvReaderValue) delegate;
                addCsvValueProxies(currentPath, csvValue.getCsvProxies());
            } else if (delegate instanceof CompositeValue) {
                CompositeValue compositeValue = (CompositeValue) delegate;
                Map<String, Value<?>> values = compositeValue.getValues();
                for (String key : values.keySet()) {
                    ValueProxy<?> subProxy = values.get(key) instanceof ValueProxy ? (ValueProxy<?>) values.get(key) : new ValueProxy<>(values.get(key));
                    addValueProxiesRecursively(currentPath+"."+key, subProxy);
                }
            }
        }
    }



    //CLONE

    /**
     * Creates a cloned Value out of given reference
     * @return Instance of {@link Value}
     */
    protected Value<?> createClonedValue(String reference) {
        // clone the proxy and all sub elements
        ValueProxy<?> valueProxy = (ValueProxy<?>) getValueProxy(reference).getClone();
        Value<?> delegate = valueProxy.getDelegate();
        addSubValueProxies(delegate);   //add inner attributes to valueProxies if delegate contains any
        return delegate;
    }





    //OTHER HELPERS

    /**
     * Tries to parse matched string to Integer.
     *
     * @return True if matched string is parsed to Integer, otherwise false.
     */
    protected boolean tryParseInt() {
        String match = match();
        int i;
        try {
            i = Integer.parseInt(match);
            push(i);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Replaces special characters like "\n" and "\t" with their concrete value and trims off
     * {@code '} and {@code "} quote characters from beginning and end of the string
     *
     * @param s String to be replaced.
     * @return Replaced string.
     */
    protected static String replaceSpecialChars(String s) {
        StringBuilder sb = new StringBuilder(s);
        sb.deleteCharAt(0).deleteCharAt(sb.length()-1); //trim quotes
        for (int i = 0; i < sb.length(); i++) {
            if (i > 0 && sb.charAt(i-1) == '\\') {  //this char is escaped, so continue with next char
                continue;
            }
            if (sb.charAt(i) == '\\' && i+1 < sb.length()) {   //this char is an escaped character like "\n"
                char escaped = sb.charAt(i+1);
                String replacement = null;
                switch(escaped) {
                    case 't':   //not a character class but a special character that needs to be replaced
                        replacement = "\t";
                        break;
                    case 'n':
                        replacement = "\n";
                        break;
                    case 'r':
                        replacement = "\r";
                        break;
                    case 'f':
                        replacement = "\f";
                        break;
                }
                if (replacement != null) {
                    sb.replace(i, i+2, replacement);
                }
            }
        }
        return  sb.toString();
    }

    /**
     * Returns or creates new value proxy for given name.
     *
     * @param name Name of the value proxy.
     * @return Proxy value.
     */
    protected ValueProxy<?> getValueProxy(String name) {
        String parent = parentPath;
        while (parent != null) {
            String testName;
            if (parent.isEmpty()) {
                testName = name;
                parent = null;
            } else {
                testName = parent + "." + name;
                parent = stripOffLastReference(parent);
            }
            if (valueProxies.containsKey(testName) && !testName.equals(currentPath)) {
                return valueProxies.get(testName);
            }
        }
        throw new InvalidReferenceNameException(name);
    }

    /**
     * Strips off the last reference from name.
     *
     * @param name Name from which to strip off the last reference.
     * @return Name with stripped off last reference.
     */
    protected static String stripOffLastReference(String name) {
        if (!name.contains(".")) {
            return "";
        } else {
            return name.substring(0, name.lastIndexOf('.'));
        }
    }

    /**
     * Collects all items up to specified delimiter.
     *
     * @param delimiter Delimiter up to which to collect all the items.
     * @return List of items up to specified delimiter.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected List getItemsUpToDelimiter(String delimiter) {
        List result = new ArrayList<>();
        while (true) {
            Object val = pop();
            if (val instanceof String && val.equals(delimiter)) {
                break;
            } else {
                result.add(val);
            }
        }
        Collections.reverse(result);
        return result;
    }

}

/**
 * Signals that an error occurred during parsing yaml entries;
 */
class ParseException extends RuntimeException {

    public ParseException() {
        super();
    }

    public ParseException(String message) {
        super(message);
    }

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParseException(Throwable cause) {
        super(cause);
    }
}
