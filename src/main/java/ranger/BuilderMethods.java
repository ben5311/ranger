package ranger;

import com.fasterxml.jackson.databind.ObjectMapper;
import ranger.core.*;
import ranger.core.arithmetic.*;
import ranger.core.csv.*;
import ranger.distribution.Distribution;
import ranger.distribution.NormalDistribution;
import ranger.distribution.UniformDistribution;
import ranger.parser.ConfigurationParser;
import ranger.parser.ValueExpressionParser;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static ranger.core.ExactWeightedValue.CountValuePair;
import static ranger.core.WeightedValue.WeightedValuePair;
import static ranger.util.UrlUtils.URLof;

/**
 * Set of helper methods to use with {@link ObjectGeneratorBuilder}.
 */
public class BuilderMethods {

    private BuilderMethods() {
    }

    //CONSTANT

    /**
     * Creates an instance of {@link ObjectGenerator} which always returns specified value.
     *
     * @param value Value to be returned by created constant object generator.
     * @param <T> Type this value would evaluate to.
     *
     * @return An instance of {@link ObjectGenerator}.
     */
    public static <T> ObjectGenerator<T> constant(T value) {
        return objectGeneratorOf( ConstantValue.of(value));
    }



    //RANDOM WITH DISCRETE VALUES
    
    /**
     * Creates an instance of {@link ObjectGenerator} which generates uniformly distributed values from specified list.
     *
     * @param values List of values.
     * @param <T> Type instance of {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator} which generates uniformly distributed values from specified list.
     */
    @SafeVarargs
    public static <T> ObjectGenerator<T> random(T... values) {
        return random(Arrays.asList(values));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates uniformly distributed values from specified list.
     *
     * @param values List of values.
     * @param <T> Type instance of {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator} which generates uniformly distributed values from specified list.
     */
    public static <T> ObjectGenerator<T> random(List<T> values) {
        return objectGeneratorOf(new DiscreteValue<>(valuesOf(values)));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates values distributed by specified distribution from
     * specified list.
     *
     * @param distribution Distribution to use.
     * @param values List of values.
     * @param <T> Type instance of {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator} which generates values distributed by specified distribution from
     *         specified list.
     */
    @SafeVarargs
    public static <T> ObjectGenerator<T> random(Distribution distribution, T... values) {
        return random(distribution, Arrays.asList(values));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates values distributed by specified distribution from
     * specified list.
     *
     * @param distribution Distribution to use.
     * @param values List of values.
     * @param <T> Type instance of {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator} which generates values distributed by specified distribution from
     *         specified list.
     */
    public static <T> ObjectGenerator<T> random(Distribution distribution, List<T> values) {
        return objectGeneratorOf(new DiscreteValue<>(valuesOf(values), distribution));
    }


    //CIRCULAR WITH DISCRETE VALUES

    /**
     * Creates an instance of {@link ObjectGenerator} which generates values in order they are specified. When values
     * are depleted, it starts again from the beginning of the list.
     *
     * @param values List of values.
     * @param <T> Type instance of {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator} which generates values in order they are specified.
     */
    @SafeVarargs
    public static <T> ObjectGenerator<T> circular(T... values) {
        return circular(Arrays.asList(values));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates values in order they are specified. When values
     * are depleted, it starts again from the beginning of the list.
     *
     * @param values List of values.
     * @param <T> Type instance of {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator} which generates values in order they are specified.
     */
    public static <T> ObjectGenerator<T> circular(List<T> values) {
        return objectGeneratorOf(new CircularValue<>(valuesOf(values)));
    }


    //RANDOM WITH RANGE
    
    /**
     * Creates an instance of {@link ObjectGenerator} which generates uniformly distributed values within specified
     * range.
     *
     * @param beginning beginning of range
     * @param end end of range
     * @param <T> Type instance of {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator} which generates uniformly distributed values within specified
     *         range.
     */
    public static <T extends Comparable<T>> ObjectGenerator<T> randomWithin(T beginning, T end) {
        return randomWithin(beginning, end, RangeValue.defaultUseEdgeCases());
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates uniformly distributed values within specified
     * range and can force generating edge cases first.
     *
     * @param beginning beginning of range
     * @param end end of range
     * @param useEdgeCases Determines whether to generate use cases first or not.
     * @param <T> Type instance of {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator} which generates uniformly distributed values within specified
     *         range.
     */
    public static <T extends Comparable<T>> ObjectGenerator<T> randomWithin(T beginning, T end, boolean useEdgeCases) {
        return randomWithin(beginning, end, useEdgeCases, RangeValue.defaultDistrbution());
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates values distributed by specified distribution
     * within specified range and can force generating edge cases first.
     *
     * @param beginning beginning of range
     * @param end end of range
     * @param useEdgeCases Determines whether to generate use cases first or not.
     * @param distribution Distribution to use.
     * @param <T> Type instance of {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator} which generates values distributed by specified distribution
     *         within specified range.
     */
    @SuppressWarnings({"unchecked"})
    public static <T extends Comparable<T>> ObjectGenerator<T> randomWithin(T beginning, T end, boolean useEdgeCases, Distribution distribution) {
        Range<T> range = new Range<>(beginning, end);
        if (beginning instanceof LocalDate) {
            return (ObjectGenerator<T>) objectGeneratorOf(new RangeValueLocalDate((LocalDate) beginning, (LocalDate) end,
                    useEdgeCases, distribution));
        }
        if (beginning instanceof LocalDateTime) {
            return (ObjectGenerator<T>) objectGeneratorOf(new RangeValueLocalDateTime((LocalDateTime) beginning,
                    (LocalDateTime) end, useEdgeCases, distribution));
        }
        if (beginning instanceof Byte) {
            return (ObjectGenerator<T>) objectGeneratorOf(new RangeValueByte((Range<Byte>) range, useEdgeCases, distribution));
        }
        if (beginning instanceof Short) {
            return (ObjectGenerator<T>) objectGeneratorOf(new RangeValueShort((Range<Short>) range, useEdgeCases, distribution));
        }
        if (beginning instanceof Integer) {
            return (ObjectGenerator<T>) objectGeneratorOf(new RangeValueInt((Range<Integer>) range, useEdgeCases, distribution));
        }
        if (beginning instanceof Long) {
            return (ObjectGenerator<T>) objectGeneratorOf(new RangeValueLong((Range<Long>) range, useEdgeCases, distribution));
        }
        if (beginning instanceof Float) {
            return (ObjectGenerator<T>) objectGeneratorOf(new RangeValueFloat((Range<Float>) range, useEdgeCases, distribution));
        }
        if (beginning instanceof Double) {
            return (ObjectGenerator<T>) objectGeneratorOf(new RangeValueDouble((Range<Double>) range, useEdgeCases, distribution));
        }
        if (beginning instanceof Date) {
            return (ObjectGenerator<T>) objectGeneratorOf(new RangeValueDate((Range<Date>) range, useEdgeCases, distribution));
        }
        if(beginning instanceof Character) {
            return (ObjectGenerator<T>) objectGeneratorOf(new RangeValueChar((Range<Character>) range, useEdgeCases, distribution));
        }
        throw new IllegalArgumentException("Type: " + beginning.getClass().getName() + " not supported.");
    }

    
    //CIRCULAR WITH RANGE
    
    /**
     * Creates an instance of {@link ObjectGenerator} which generates values in sequence within specified range with
     * specified step. When values from the range are depleted, it starts again from the beginning of the range.
     *
     * @param beginning beginning of range
     * @param end end of range
     * @param step the step.
     * @param <T> Type instance of {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator} which generates values in sequence within specified range with
     *         specified step.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Comparable<T>> ObjectGenerator<T> circularWithin(T beginning, T end, T step) {
        Range<T> range = new Range<>(beginning, end);
        if (beginning instanceof Byte) {
            return (ObjectGenerator<T>) objectGeneratorOf(new CircularRangeValueByte((Range<Byte>) range, (Byte) step));
        }
        if (beginning instanceof Short) {
            return (ObjectGenerator<T>) objectGeneratorOf(new CircularRangeValueShort((Range<Short>) range, (Short) step));
        }
        if (beginning instanceof Integer) {
            return (ObjectGenerator<T>) objectGeneratorOf(new CircularRangeValueInt((Range<Integer>) range, (Integer) step));
        }
        if (beginning instanceof Long) {
            return (ObjectGenerator<T>) objectGeneratorOf(new CircularRangeValueLong((Range<Long>) range, (Long) step));
        }
        if (beginning instanceof Float) {
            return (ObjectGenerator<T>) objectGeneratorOf(new CircularRangeValueFloat((Range<Float>) range, (Float) step));
        }
        if (beginning instanceof Double) {
            return (ObjectGenerator<T>) objectGeneratorOf(new CircularRangeValueDouble((Range<Double>) range, (Double) step));
        }
        throw new IllegalArgumentException("Type: " + beginning.getClass().getName() + " not supported.");
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates Characters in sequence within specified range with
     * specified step. When values from the range are depleted, it starts again from the beginning of the range.
     *
     * @param beginning beginning of range
     * @param end end of range
     * @param step the step
     * @return An instance of {@link ObjectGenerator} which generates Characters in sequence within specified range with
     *         specified step.
     */
    public static ObjectGenerator<Character> circularWithin(char beginning, char end, int step) {
        return objectGeneratorOf(new CircularRangeValueChar(new Range<>(beginning, end), step));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates Dates in sequence within specified range with
     * specified step. When values from the range are depleted, it starts again from the beginning of the range.
     *
     * @param beginning beginning of range
     * @param end end of range
     * @param step the step (number of days)
     * @return An instance of {@link ObjectGenerator} which generates Dates in sequence within specified range with
     *         specified step.
     */
    public static ObjectGenerator<Date> circularWithin(Date beginning, Date end, int step) {
        return objectGeneratorOf(new CircularRangeValueDate(new Range<>(beginning, end), step));
    }


    //WEIGHTED AND EXACTLY

    /**
     * Creates an instance of {@link ObjectGenerator} which generates specified values distributed by their weights.
     *
     * @param pairs List of values with corresponding weights.
     * @param <T> Type instance of {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator} which generates specified values distributed by their weights.
     */
    @SafeVarargs
    public static <T> ObjectGenerator<T> weighted(WeightedValuePair<T>... pairs) {
        return weighted(Arrays.asList(pairs));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates specified values distributed by their weights.
     *
     * @param pairs List of values with corresponding weights.
     * @param <T> Type instance of {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator} which generates specified values distributed by their weights.
     */
    public static <T> ObjectGenerator<T> weighted(List<WeightedValuePair<T>> pairs) {
        return objectGeneratorOf(new WeightedValue<>(pairs));
    }

    /**
     * Returns WeightPair for use with weighted()
     * @param value the value
     * @param weight of value
     */
    public static <T> WeightedValuePair<T> weightPair(T value, double weight) {
        return new WeightedValuePair<>(valueOf(value), weight);
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates each value exactly specified number of times.
     * Values are generated using weighted distribution until depleted. When all values are generated specified number
     * of times and {@link ObjectGenerator#next()} is invoked,
     * {@link ranger.core.ExactWeightedValue.ExactWeightedValueDepletedException
     * ExactWeightedValueDepletedException} is thrown.
     *
     * @param pairs List of values with corresponding count.
     * @param <T> Type instance of {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator} which generates each value exactly specified number of times.
     */
    @SafeVarargs
    public static <T> ObjectGenerator<T> exactly(CountValuePair<T>... pairs) {
        return exactly(Arrays.asList(pairs));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates each value exactly specified number of times.
     * Values are generated using weighted distribution until depleted. When all values are generated specified number
     * of times and {@link ObjectGenerator#next()} is invoked,
     * {@link ranger.core.ExactWeightedValue.ExactWeightedValueDepletedException
     * ExactWeightedValueDepletedException} is thrown.
     *
     * @param pairs List of values with corresponding counts.
     * @param <T> Type instance of {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator} which generates each value exactly specified number of times.
     */
    public static <T> ObjectGenerator<T> exactly(List<CountValuePair<T>> pairs) {
        return objectGeneratorOf(new ExactWeightedValue<>(pairs));
    }

    /**
     * Returns CountPair for use with exactly()
     * @param value the value
     * @param count count of value
     */
    public static <T> CountValuePair<T> countPair(T value, long count) {
        return new CountValuePair<>(valueOf(value), count);
    }



    //DISTRIBUTIONS

    /**
     * Returns uniform distribution
     * @return new uniform distribution
     */
    public static UniformDistribution uniform() {
        return new UniformDistribution();
    }

    /**
     * Returns Normal distribution with <code>mean=0.5</code>, <code>standardDeviation=0.125</code>,
     * <code>lower=0</code> and <code>upper=1</code>.
     */
    public static NormalDistribution normal() {
        return new NormalDistribution();
    }


    /**
     * Returns Normal distribution with specified <code>mean</code>, <code>standardDeviation</code>,
     * <code>lower</code> and <code>upper</code>.
     * @param mean Mean of Normal distribution.
     * @param standardDeviation Standard deviation of Normal distribution.
     * @param lower Lower bound, no values lower than this will be generated.
     * @param upper Upper bound, no values higher than this will be generated.
     */
    public static NormalDistribution normal(double mean, double standardDeviation, double lower, double upper) {
        return new NormalDistribution(mean, standardDeviation, lower, upper);
    }


    //RANDOM CONTENT STRING

    /**
     * Creates an instance of {@link ObjectGenerator} which generates random string values of length to which
     * <code>lengthGenerator</code> evaluates to. Each generation can evaluate to different string length, based on
     * value generated by <code>lengthGenerator</code>. String will contain following characters [A-Za-z0-9]. Uniform
     * distribution is used to select characters from character ranges.
     *
     * @param lengthGenerator Value that returns integer which represents length of generated string. It should never
     *            generate length that is less than 1.
     * @return An instance of {@link ObjectGenerator} which generates random string values of specified length.
     */
    public static ObjectGenerator<String> randomContentString(ObjectGenerator<Integer> lengthGenerator) {
        return objectGeneratorOf(new RandomContentStringValue(lengthGenerator.value));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates random string values of length to which
     * <code>lengthGenerator</code> evaluates to. Each generation can evaluate to different string length, based on
     * value generated by <code>lengthGenerator</code>. String will contain specified character ranges. Uniform
     * distribution is used to select characters from character ranges.
     *
     * @param lengthValue Value that returns integer which represents length of generated string. It should never
     *            generate length that is less than 1.
     * @param ranges List of ranges from which characters are taken with uniform distribution.
     * @return An instance of {@link ObjectGenerator} which generates random string values of specified length.
     */
    @SafeVarargs
    public static ObjectGenerator<String> randomContentString(ObjectGenerator<Integer> lengthValue, Range<Character>... ranges) {
        return randomContentString(lengthValue, Arrays.asList(ranges));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates random string values of length to which
     * <code>lengthGenerator</code> evaluates to. Each generation can evaluate to different string length, based on
     * value generated by <code>lengthGenerator</code>. String will contain specified character ranges. Uniform
     * distribution is used to select characters from character ranges.
     *
     * @param lengthValue Value that returns integer which represents length of generated string. It should never
     *            generate length that is less than 1.
     * @param ranges List of ranges from which characters are taken with uniform distribution.
     * @return An instance of {@link ObjectGenerator} which generates random string values of specified length.
     */
    public static ObjectGenerator<String> randomContentString(ObjectGenerator<Integer> lengthValue, List<Range<Character>> ranges) {
        return objectGeneratorOf(new RandomContentStringValue(lengthValue.value, ranges));
    }

    /**
     * Creates char range
     * @param beginning first char in range
     * @param end last char in range
     */
    public static Range<Character> range(char beginning, char end) {
        return new Range<>(beginning, end);
    }



    //XEGER

    /**
     * Returns ObjectGenerator that generates a random String that matches given regexPattern (reverse regex)
     * @param regexPattern regex pattern you want the generated String to match
     * @return instance of ObjectGenerator that generates a random String that matches given regexPattern
     */
    public static ObjectGenerator<String> xeger(String regexPattern) {
        return objectGeneratorOf(new XegerValue(ConstantValue.of(regexPattern)));
    }

    /**
     * Returns ObjectGenerator that generates a random String that matches regexPattern retrieved by
     * regexPatternGenerator (reverse regex)
     * @param regexPatternGenerator ObjectGenerator that produces the regex pattern you want the generated String to match
     * @return instance of ObjectGenerator that generates a random String that matches given regexPattern
     */
    public static ObjectGenerator<String> xeger(ObjectGenerator<String> regexPatternGenerator) {
        return objectGeneratorOf(new XegerValue(regexPatternGenerator.value));
    }



    //DATE AND TIME

    /**
     * Creates an instance of {@link ObjectGenerator} which generates current time in milliseconds.
     */
    public static ObjectGenerator<Long> now() {
        return objectGeneratorOf(new NowValue());
    }

    /**
     * Returns an instance of {@link ObjectGenerator} which generates current date-time as {@link Date} object.
     */
    public static ObjectGenerator<Date> nowDate() {
        return objectGeneratorOf(new NowDateValue());
    }

    /**
     * Returns an instance of {@link ObjectGenerator} which generates current date as {@link LocalDate} object.
     */
    public static ObjectGenerator<LocalDate> nowLocalDate() {
        return objectGeneratorOf(new NowLocalDateValue());
    }

    /**
     * Returns an instance of {@link ObjectGenerator} which generates current date-time as {@link LocalDateTime} object.
     */
    public static ObjectGenerator<LocalDateTime> nowLocalDateTime() {
        return objectGeneratorOf(new NowLocalDateTimeValue());
    }



    //ARITHMETIC OPERATIONS

    /**
     * Creates an instance of {@link ObjectGenerator} which adds up values of given object generators.
     *
     * @param type Type object generator will return.
     * @param summand1 First object generator for addition.
     * @param summand2 Second object generator for addition.
     * @param <T> Type object generator will return.
     * @return An instance of {@link ObjectGenerator} which adds up values of given object generators.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Number> ObjectGenerator<T> add(Class<T> type, ObjectGenerator<? extends Number> summand1, ObjectGenerator<? extends Number> summand2) {
        if (type.equals(Byte.class)) {
            return (ObjectGenerator<T>) objectGeneratorOf(new AdditionValueByte(summand1.value, summand2.value));
        } else if (type.equals(Short.class)) {
            return (ObjectGenerator<T>) objectGeneratorOf(new AdditionValueShort(summand1.value, summand2.value));
        } else if (type.equals(Integer.class)) {
            return (ObjectGenerator<T>) objectGeneratorOf(new AdditionValueInteger(summand1.value, summand2.value));
        } else if (type.equals(Long.class)) {
            return (ObjectGenerator<T>) objectGeneratorOf(new AdditionValueLong(summand1.value, summand2.value));
        } else if (type.equals(Float.class)) {
            return (ObjectGenerator<T>) objectGeneratorOf(new AdditionValueFloat(summand1.value, summand2.value));
        } else if (type.equals(Double.class)) {
            return (ObjectGenerator<T>) objectGeneratorOf(new AdditionValueDouble(summand1.value, summand2.value));
        } else {
            throw new IllegalArgumentException("Type: " + type.getName() + " not supported.");
        }
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which subtracts values of given object generators.
     *
     * @param type Type object generator will return.
     * @param minuend Object generator which will be used as minuend.
     * @param subtrahend Object generator which will be used as subtrahend
     * @param <T> Type object generator will return.
     * @return An instance of {@link ObjectGenerator} which subtracts values of given object generators.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Number> ObjectGenerator<T> subtract(Class<T> type, ObjectGenerator<? extends Number> minuend, ObjectGenerator<? extends Number> subtrahend) {
        if (type.equals(Byte.class)) {
            return (ObjectGenerator<T>) objectGeneratorOf(new SubtractionValueByte(minuend.value, subtrahend.value));
        } else if (type.equals(Short.class)) {
            return (ObjectGenerator<T>) objectGeneratorOf(new SubtractionValueShort(minuend.value, subtrahend.value));
        } else if (type.equals(Integer.class)) {
            return (ObjectGenerator<T>) objectGeneratorOf(new SubtractionValueInteger(minuend.value, subtrahend.value));
        } else if (type.equals(Long.class)) {
            return (ObjectGenerator<T>) objectGeneratorOf(new SubtractionValueLong(minuend.value, subtrahend.value));
        } else if (type.equals(Float.class)) {
            return (ObjectGenerator<T>) objectGeneratorOf(new SubtractionValueFloat(minuend.value, subtrahend.value));
        } else if (type.equals(Double.class)) {
            return (ObjectGenerator<T>) objectGeneratorOf(new SubtractionValueDouble(minuend.value, subtrahend.value));
        } else {
            throw new IllegalArgumentException("Type: " + type.getName() + " not supported.");
        }
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which multiplies values of given object generators.
     *
     * @param type Type object generator will return.
     * @param factor1 First object generator for multiplication.
     * @param factor2 Second object generator for multiplication.
     * @param <T> Type object generator will return.
     * @return An instance of {@link ObjectGenerator} which multiplies values of given object generators.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Number> ObjectGenerator<T> multiply(Class<T> type, ObjectGenerator<? extends Number> factor1, ObjectGenerator<? extends Number> factor2) {
        if (type.equals(Byte.class)) {
            return (ObjectGenerator<T>) objectGeneratorOf(new MultiplicationValueByte(factor1.value, factor2.value));
        } else if (type.equals(Short.class)) {
            return (ObjectGenerator<T>) objectGeneratorOf(new MultiplicationValueShort(factor1.value, factor2.value));
        } else if (type.equals(Integer.class)) {
            return (ObjectGenerator<T>) objectGeneratorOf(new MultiplicationValueInteger(factor1.value, factor2.value));
        } else if (type.equals(Long.class)) {
            return (ObjectGenerator<T>) objectGeneratorOf(new MultiplicationValueLong(factor1.value, factor2.value));
        } else if (type.equals(Float.class)) {
            return (ObjectGenerator<T>) objectGeneratorOf(new MultiplicationValueFloat(factor1.value, factor2.value));
        } else if (type.equals(Double.class)) {
            return (ObjectGenerator<T>) objectGeneratorOf(new MultiplicationValueDouble(factor1.value, factor2.value));
        } else {
            throw new IllegalArgumentException("Type: " + type.getName() + " not supported.");
        }
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which divides values of given object generators.
     *
     * @param type Type object generator will return.
     * @param dividend Object generator which will be used as dividend.
     * @param divisor Object generator which will be used as divisor.
     * @param <T> Type object generator will return.
     * @return An instance of {@link ObjectGenerator} which divides values of given object generators.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Number> ObjectGenerator<T> divide(Class<T> type, ObjectGenerator<? extends Number> dividend, ObjectGenerator<? extends Number> divisor) {
        if (type.equals(Byte.class)) {
            return (ObjectGenerator<T>) objectGeneratorOf(new DivisionValueByte(dividend.value, divisor.value));
        } else if (type.equals(Short.class)) {
            return (ObjectGenerator<T>) objectGeneratorOf(new DivisionValueShort(dividend.value, divisor.value));
        } else if (type.equals(Integer.class)) {
            return (ObjectGenerator<T>) objectGeneratorOf(new DivisionValueInteger(dividend.value, divisor.value));
        } else if (type.equals(Long.class)) {
            return (ObjectGenerator<T>) objectGeneratorOf(new DivisionValueLong(dividend.value, divisor.value));
        } else if (type.equals(Float.class)) {
            return (ObjectGenerator<T>) objectGeneratorOf(new DivisionValueFloat(dividend.value, divisor.value));
        } else if (type.equals(Double.class)) {
            return (ObjectGenerator<T>) objectGeneratorOf(new DivisionValueDouble(dividend.value, divisor.value));
        } else {
            throw new IllegalArgumentException("Type: " + type.getName() + " not supported.");
        }
    }


    /**
     * Creates an instance of {@link ObjectGenerator} which generates UUIDs.
     *
     * @return An instance of {@link ObjectGenerator} which generates UUIDS.
     */
    public static ObjectGenerator<String> uuid() {
        return objectGeneratorOf(new UUIDValue());
    }




    //LIST

    /**
     * Creates an instance of {@link ObjectGenerator} which generates list containing all values specified.
     *
     * @param values List of values.
     * @param <T> Type instance of {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator} which generates list containing all values specified.
     */
    @SafeVarargs
    public static <T> ObjectGenerator<List<T>> list(T... values) {
        return list(Arrays.asList(values));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates list containing all values specified.
     *
     * @param values List of values.
     * @param <T> Type instance of {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator} which generates list containing all values specified.
     */
    public static <T> ObjectGenerator<List<T>> list(List<T> values) {
        return objectGeneratorOf(new ListValue<>(valuesOf(values)));
    }


    //RANDOM LENGTH LIST

    /**
     * Creates an instance of {@link ObjectGenerator} which generates random length list containing all values
     * specified.
     *
     * @param minLength Minimum length for result list.
     * @param maxLength Maximum length for result list.
     * @param elementGenerator Values generator.
     * @param <T> Type instance of {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator} which generates list containing generated elements with size
     *         between minLength and maxLength.
     */
    public static <T> ObjectGenerator<List<T>> list(int minLength, int maxLength, ObjectGenerator<T> elementGenerator) {
        return objectGeneratorOf(new RandomLengthListValue<>(minLength, maxLength, elementGenerator.value));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates random length list containing all values
     * specified.
     *
     * @param minLength Minimum length for result list.
     * @param maxLength Maximum length for result list.
     * @param elementGenerator Values generator.
     * @param distribution Distribution to use for number of items in list.
     * @param <T> Type instance of {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator} which generates list containing generated elements with size
     *         between minLength and maxLength.
     */
    public static <T> ObjectGenerator<List<T>> list(int minLength, int maxLength, ObjectGenerator<T> elementGenerator, Distribution distribution) {
        return objectGeneratorOf(new RandomLengthListValue<>(minLength, maxLength, elementGenerator.value, distribution));
    }


    /**
     * Creates an instance of {@link ObjectGenerator} which generates empty list.
     *
     * @param <T> Type instance of {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator} which generates empty list.
     */
    public static <T> ObjectGenerator<List<T>> emptyList() {
        return objectGeneratorOf(new EmptyListValue<>());
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates empty map.
     *
     * @param <K> Key type of map which {@link ObjectGenerator} will generate.
     * @param <V> Value type of map which {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator} which generates empty map.
     */
    public static <K, V> ObjectGenerator<Map<K, V>> emptyMap() {
        return objectGeneratorOf(new EmptyMapValue<>());
    }




    //CSV

    /**
     * Creates an instance of {@link ObjectGenerator} which reads CSV file and returns new record on each iteration
     * until CSV file is depleted.
     *
     * @param path Path to the CSV file.
     * @return An instance of {@link ObjectGenerator} which reads CSV file.
     * @throws ValueException if you try to retrieve more records from it than contained in CSV file
     * @throws ValueException if an IO error occurs during parsing the CSV file
     */
    public static ObjectGenerator<Map<String, String>> csv(String path) {
        return objectGeneratorOf(new CsvReaderValue(new CSVParserSettings(URLof(path))));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which reads CSV file and returns new record on each iteration
     * until CSV file is depleted.
     *
     * @param path Path to the CSV file.
     * @param delimiter Delimiter of columns within CSV file.
     * @return An instance of {@link ObjectGenerator} which reads CSV file.
     * @throws ValueException if you try to retrieve more records from it than contained in CSV file
     * @throws ValueException if an IO error occurs during parsing the CSV file
     */
    public static ObjectGenerator<Map<String, String>> csv(String path, char delimiter) {
        return objectGeneratorOf(new CsvReaderValue(new CSVParserSettings(URLof(path), delimiter)));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which reads CSV file and returns new record on each iteration
     * until CSV file is depleted.
     *
     * @param path Path to the CSV file.
     * @param delimiter Delimiter of columns within CSV file.
     * @param withHeader Specifies if the first record should be interpreted as header.
     * @return An instance of {@link ObjectGenerator} which reads CSV file.
     * @throws ValueException if you try to retrieve more records from it than contained in CSV file
     * @throws ValueException if an IO error occurs during parsing the CSV file
     */
    public static ObjectGenerator<Map<String, String>> csv(String path, char delimiter, boolean withHeader) {
        return objectGeneratorOf(new CsvReaderValue(new CSVParserSettings(URLof(path), delimiter, withHeader)));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which reads CSV file and returns new record on each iteration
     * until CSV file is depleted.
     *
     * @param path Path to the CSV file.
     * @param delimiter Delimiter of columns within CSV file.
     * @param withHeader Specifies if the first record should be interpreted as header.
     * @param recordSeparator Delimiter of records within CSV file.
     * @param trim True if each column value is to be trimmed for leading and trailing whitespace, otherwise
     *            <code>false</code>.
     * @param quote Character that will be stripped from beginning and end of each column if present. If set to
     *            <code>null</code>, no characters will be stripped (nothing will be used as quote character).
     * @param commentMarker Character to use as a comment marker, everything after it is considered comment.
     * @param ignoreEmptyLines True if empty lines are to be ignored, otherwise <code>false</code>.
     * @param nullString Converts string with given value to <code>null</code>. If set to <code>null</code>, no
     *            conversion will be done.
     * @return An instance of {@link ObjectGenerator} which reads CSV file.
     * @throws ValueException if you try to retrieve more records from it than contained in CSV file
     * @throws ValueException if an IO error occurs during parsing the CSV file
     */
    public static ObjectGenerator<Map<String, String>> csv(String path, char delimiter, boolean withHeader, String recordSeparator, boolean trim, Character quote, char commentMarker, boolean ignoreEmptyLines, String nullString) {
        return objectGeneratorOf(new CsvReaderValue(new CSVParserSettings(URLof(path), delimiter, withHeader, recordSeparator, trim, quote, commentMarker, ignoreEmptyLines, nullString)));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which reads CSV file and returns next record on each iteration
     *
     * @param path Path to the CSV file.
     * @return An instance of {@link ObjectGenerator} which reads CSV file.
     * @throws ValueException if an IO error occurs during parsing the CSV file
     */
    public static ObjectGenerator<Map<String, String>> csvCircular(String path) {
        return objectGeneratorOf(new CircularCsvReaderValue(new CSVParserSettings(URLof(path))));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which reads CSV file and returns next record on each iteration
     *
     * @param path Path to the CSV file.
     * @param delimiter Delimiter of columns within CSV file.
     * @return An instance of {@link ObjectGenerator} which reads CSV file.
     * @throws ValueException if an IO error occurs during parsing the CSV file
     */
    public static ObjectGenerator<Map<String, String>> csvCircular(String path, char delimiter) {
        return objectGeneratorOf(new CircularCsvReaderValue(new CSVParserSettings(URLof(path), delimiter)));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which reads CSV file and returns next record on each iteration
     *
     * @param path Path to the CSV file.
     * @param delimiter Delimiter of columns within CSV file.
     * @param withHeader Specifies if the first record should be interpreted as header.
     * @return An instance of {@link ObjectGenerator} which reads CSV file.
     * @throws ValueException if an IO error occurs during parsing the CSV file
     */
    public static ObjectGenerator<Map<String, String>> csvCircular(String path, char delimiter, boolean withHeader) {
        return objectGeneratorOf(new CircularCsvReaderValue(new CSVParserSettings(URLof(path), delimiter, withHeader)));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which reads CSV file and returns next record on each iteration
     *
     * @param path Path to the CSV file.
     * @param delimiter Delimiter of columns within CSV file.
     * @param withHeader Specifies if the first record should be interpreted as header.
     * @param recordSeparator Delimiter of records within CSV file.
     * @param trim True if each column value is to be trimmed for leading and trailing whitespace, otherwise
     *            <code>false</code>.
     * @param quote Character that will be stripped from beginning and end of each column if present. If set to
     *            <code>null</code>, no characters will be stripped (nothing will be used as quote character).
     * @param commentMarker Character to use as a comment marker, everything after it is considered comment.
     * @param ignoreEmptyLines True if empty lines are to be ignored, otherwise <code>false</code>.
     * @param nullString Converts string with given value to <code>null</code>. If set to <code>null</code>, no
     *            conversion will be done.
     * @return An instance of {@link ObjectGenerator} which reads CSV file.
     * @throws ValueException if an IO error occurs during parsing the CSV file
     */
    public static ObjectGenerator<Map<String, String>> csvCircular(String path, char delimiter, boolean withHeader, String recordSeparator, boolean trim, Character quote, char commentMarker, boolean ignoreEmptyLines, String nullString) {
        return objectGeneratorOf(new CircularCsvReaderValue(new CSVParserSettings(URLof(path), delimiter, withHeader, recordSeparator, trim, quote, commentMarker, ignoreEmptyLines, nullString)));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which reads CSV file and returns random record on each iteration
     *
     * @param path Path to the CSV file.
     * @return An instance of {@link ObjectGenerator} which reads CSV file.
     * @throws ValueException if an IO error occurs during parsing the CSV file
     */
    public static ObjectGenerator<Map<String, String>> csvRandom(String path) {
        return objectGeneratorOf(new RandomCsvReaderValue(new CSVParserSettings(URLof(path))));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which reads CSV file and returns random record on each iteration
     *
     * @param path Path to the CSV file.
     * @param delimiter Delimiter of columns within CSV file.
     * @return An instance of {@link ObjectGenerator} which reads CSV file.
     * @throws ValueException if an IO error occurs during parsing the CSV file
     */
    public static ObjectGenerator<Map<String, String>> csvRandom(String path, char delimiter) {
        return objectGeneratorOf(new RandomCsvReaderValue(new CSVParserSettings(URLof(path), delimiter)));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which reads CSV file and returns random record on each iteration
     *
     * @param path Path to the CSV file.
     * @param delimiter Delimiter of columns within CSV file.
     * @param withHeader Specifies if the first record should be interpreted as header.
     * @return An instance of {@link ObjectGenerator} which reads CSV file.
     * @throws ValueException if an IO error occurs during parsing the CSV file
     */
    public static ObjectGenerator<Map<String, String>> csvRandom(String path, char delimiter, boolean withHeader) {
        return objectGeneratorOf(new RandomCsvReaderValue(new CSVParserSettings(URLof(path), delimiter, withHeader)));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which reads CSV file and returns random record on each iteration
     *
     * @param path Path to the CSV file.
     * @param delimiter Delimiter of columns within CSV file.
     * @param withHeader Specifies if the first record should be interpreted as header.
     * @param recordSeparator Delimiter of records within CSV file.
     * @param trim True if each column value is to be trimmed for leading and trailing whitespace, otherwise
     *            <code>false</code>.
     * @param quote Character that will be stripped from beginning and end of each column if present. If set to
     *            <code>null</code>, no characters will be stripped (nothing will be used as quote character).
     * @param commentMarker Character to use as a comment marker, everything after it is considered comment.
     * @param ignoreEmptyLines True if empty lines are to be ignored, otherwise <code>false</code>.
     * @param nullString Converts string with given value to <code>null</code>. If set to <code>null</code>, no
     *            conversion will be done.
     * @return An instance of {@link ObjectGenerator} which reads CSV file.
     * @throws ValueException if an IO error occurs during parsing the CSV file
     */
    public static ObjectGenerator<Map<String, String>> csvRandom(String path, char delimiter, boolean withHeader, String recordSeparator, boolean trim, Character quote, char commentMarker, boolean ignoreEmptyLines, String nullString) {
        return objectGeneratorOf(new RandomCsvReaderValue(new CSVParserSettings(URLof(path), delimiter, withHeader, recordSeparator, trim, quote, commentMarker, ignoreEmptyLines, nullString)));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which reads CSV file and returns random record on each iteration
     * with respect to record's weight
     *
     * @param path Path to the CSV file.
     * @param weightField The csv column containing the weight values
     * @return An instance of {@link ObjectGenerator} which reads CSV file.
     * @throws ValueException if CSV file does not contain weightField or if weightField's value is not a number
     * @throws ValueException if an IO error occurs during parsing the CSV file
     */
    public static ObjectGenerator<Map<String, String>> csvWeighted(String path, String weightField) {
        return objectGeneratorOf(new WeightedCsvReaderValue(new CSVParserSettings(URLof(path)), weightField));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which reads CSV file and returns random record on each iteration
     * with respect to record's weight
     *
     * @param path Path to the CSV file.
     * @param delimiter Delimiter of columns within CSV file.
     * @param weightField The csv column containing the weight values
     * @return An instance of {@link ObjectGenerator} which reads CSV file.
     * @throws ValueException if CSV file does not contain weightField or if weightField's value is not a number
     * @throws ValueException if an IO error occurs during parsing the CSV file
     */
    public static ObjectGenerator<Map<String, String>> csvWeighted(String path, char delimiter, String weightField) {
        return objectGeneratorOf(new WeightedCsvReaderValue(new CSVParserSettings(URLof(path), delimiter), weightField));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which reads CSV file and returns random record on each iteration
     * with respect to record's weight
     *
     * @param path Path to the CSV file.
     * @param delimiter Delimiter of columns within CSV file.
     * @param withHeader Specifies if the first record should be interpreted as header.
     * @param weightField The csv column containing the weight values
     * @return An instance of {@link ObjectGenerator} which reads CSV file.
     * @throws ValueException if CSV file does not contain weightField or if weightField's value is not a number
     * @throws ValueException if an IO error occurs during parsing the CSV file
     */
    public static ObjectGenerator<Map<String, String>> csvWeighted(String path, char delimiter, boolean withHeader, String weightField) {
        return objectGeneratorOf(new WeightedCsvReaderValue(new CSVParserSettings(URLof(path), delimiter, withHeader), weightField));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which reads CSV file and returns random record on each iteration
     * with respect to record's weight
     *
     * @param path Path to the CSV file.
     * @param delimiter Delimiter of columns within CSV file
     * @param withHeader Specifies if the first record should be interpreted as header.
     * @param recordSeparator Delimiter of records within CSV file.
     * @param trim True if each column value is to be trimmed for leading and trailing whitespace, otherwise
     *            <code>false</code>.
     * @param quote Character that will be stripped from beginning and end of each column if present. If set to
     *            <code>null</code>, no characters will be stripped (nothing will be used as quote character).
     * @param commentMarker Character to use as a comment marker, everything after it is considered comment.
     * @param ignoreEmptyLines True if empty lines are to be ignored, otherwise <code>false</code>.
     * @param nullString Converts string with given value to <code>null</code>. If set to <code>null</code>, no
     *            conversion will be done.
     * @param weightField The csv column containing the weight values
     * @return An instance of {@link ObjectGenerator} which reads CSV file.
     * @throws ValueException if CSV file does not contain weightField or if weightField's value is not a number
     * @throws ValueException if an IO error occurs during parsing the CSV file
     */
    public static ObjectGenerator<Map<String, String>> csvWeighted(String path, char delimiter, boolean withHeader, String recordSeparator, boolean trim, Character quote, char commentMarker, boolean ignoreEmptyLines, String nullString, String weightField) {
        return objectGeneratorOf(new WeightedCsvReaderValue(new CSVParserSettings(URLof(path), delimiter, withHeader, recordSeparator, trim, quote, commentMarker, ignoreEmptyLines, nullString), weightField));
    }



    //DEPENDENT VALUES

    /**
     * Creates an instance of {@link ObjectGenerator} which selects an ObjectGenerator from values that relates to sourceGenerator's randomly selected value.
     * @param sourceGenerator The ObjectGenerator containing the source Value.
     * @param values The ObjectGenerators to select from.
     * @return Switcher ObjectGenerator.
     * @throws ValueException if values' count differs from sourceGenerator's size
     */
    public static <T> ObjectGenerator<T> switcher(ObjectGenerator<?> sourceGenerator, Object... values) {
        return switcher(sourceGenerator, Arrays.asList(values));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which selects an ObjectGenerator from values that relates to sourceGenerator's randomly selected value.
     * @param sourceGenerator The ObjectGenerator containing the source Value.
     * @param values The ObjectGenerators to select from.
     * @return Switcher ObjectGenerator.
     * @throws ValueException if values' count differs from sourceGenerator's size
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> ObjectGenerator<T> switcher(ObjectGenerator<?> sourceGenerator, List<Object> values) {
        Value<?> source = sourceGenerator.getValue();
        if (source == null) { throw new IllegalArgumentException("source cannot be null"); }
        if (!(source instanceof Switchable)) { throw new IllegalArgumentException(ValueExpressionParser.SWITCH_ERROR); }
        return (ObjectGenerator<T>) objectGeneratorOf(new SwitchValue((Switchable<?>) source, valuesOfRaw(values)));
    }

    @SuppressWarnings("unchecked")
    public static <T> ObjectGenerator<T> map(ObjectGenerator<?> sourceGenerator, Map<?, ?> map) {
        List<MapperValue.KeyValuePair<Object>> keyValuePairs = new ArrayList<>();
        map.forEach((k, v) -> {
            Value<Object> key = ConstantValue.of(k);
            Value<Object> value = v instanceof ObjectGenerator ? ((ObjectGenerator<Object>) v).value : ConstantValue.of(v);
            keyValuePairs.add(new MapperValue.KeyValuePair<>(key, value));
        });
        return (ObjectGenerator<T>) objectGeneratorOf(new MapperValue<>(sourceGenerator.value, keyValuePairs));
    }



    //TRANSFORMER

    /**
     * Creates an instance of {@link ObjectGenerator} which generates lower case version of source {@link ObjectGenerator}'s String
     *
     * @param source ObjectGenerator that generates Strings
     * @return An instance of {@link ObjectGenerator} which generates lower case representation of source.
     */
    public static ObjectGenerator<String> lower(ObjectGenerator<String> source) {
        return objectGeneratorOf(new CaseTransformer(source.value, false));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates upper case version of source {@link ObjectGenerator}'s String
     *
     * @param source ObjectGenerator that generates Strings
     * @return An instance of {@link ObjectGenerator} which generates upper case representation of source.
     */
    public static ObjectGenerator<String> upper(ObjectGenerator<String> source) {
        return objectGeneratorOf(new CaseTransformer(source.value, true));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates ascii representation of source {@link ObjectGenerator}'s String
     *
     * @param source ObjectGenerator that generates Strings
     * @return An instance of {@link ObjectGenerator} which generates ascii representation of source.
     */
    public static ObjectGenerator<String> ascii(ObjectGenerator<String> source) {
        return objectGeneratorOf(new AsciiTransformer(source.value));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates a formatted string using the specified format
     * string and objects. Placeholder for value is defined as '{}', first placeholder uses first value, second, second
     * value, and so on.
     *
     * @param format Format string,
     * @param values List of values.
     * @return An instance of {@link ObjectGenerator} which generates formated strings.
     */
    public static ObjectGenerator<String> string(String format, Object... values) {
        return string(format, Arrays.asList(values));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates a formatted string using the specified format
     * string and objects. Placeholder for value is defined as '{}', first placeholder uses first value, second, second
     * value, and so on.
     *
     * @param format Format string,
     * @param values List of values.
     * @return An instance of {@link ObjectGenerator} which generates formated strings.
     */
    public static ObjectGenerator<String> string(String format, List<Object> values) {
        return objectGeneratorOf(new StringTransformer(ConstantValue.of(format), valuesOfRaw(values)));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates a formatted string using the specified format
     * string and objects. Syntax is printf() like.
     *
     * @param format Format string in printf syntax,
     * @param values List of values.
     * @return An instance of {@link ObjectGenerator} which generates formated strings.
     */
    public static ObjectGenerator<String> stringf(String format, Object... values) {
        return stringf(format, Arrays.asList(values));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates a formatted string using the specified format
     * string and objects. Syntax is printf() like.
     *
     * @param format Format string in printf syntax,
     * @param values List of values.
     * @return An instance of {@link ObjectGenerator} which generates formated strings.
     */
    public static ObjectGenerator<String> stringf(String format, List<Object> values) {
        return objectGeneratorOf(new StringfTransformer(ConstantValue.of(format), valuesOfRaw(values)));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which converts specified instance of {@link ObjectGenerator} to
     * time format. For format options, see {@link java.time.format.DateTimeFormatter DateTimeFormatter}.
     *
     * @param format Format string.
     * @param generator Instance of {@link ObjectGenerator} which value will be formated to string. It must return
     *            {@link Long}, {@link Date}, {@link LocalDate} or {@link LocalDateTime}.
     * @param <T> Type of value count pair contains.
     * @return An instance of {@link ObjectGenerator} which converts specified instance of {@link ObjectGenerator} to
     *         time format.
     */
    public static <T> ObjectGenerator<String> time(String format, ObjectGenerator<T> generator) {
        return objectGeneratorOf(new TimeFormatTransformer(ConstantValue.of(format), generator.value));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which extracts property with given name and type from specified
     * instance of {@link ObjectGenerator}.
     *
     * @param keyName Name of the key.
     * @param keyType Type of the key.
     * @param generator Instance of {@link ObjectGenerator} from which value will be extracted.
     * @param <T> Type object generator will return.
     * @return An instance of {@link ObjectGenerator} which extracts property.
     */
    public static <T> ObjectGenerator<T> get(String keyName, Class<T> keyType, ObjectGenerator<?> generator) {
        return objectGeneratorOf(new GetterTransformer<>(keyName, keyType, generator.value));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which converts specified instance of {@link ObjectGenerator} to
     * JSON. Uses default instance of {@link ObjectMapper} to converted to JSON.
     *
     * @param generator Instance of {@link ObjectGenerator} which value will be converted to JSON.
     * @return An instance of {@link ObjectGenerator} which converts specified instance of {@link ObjectGenerator} to
     *         JSON.
     */
    public static ObjectGenerator<String> json(ObjectGenerator<?> generator) {
        return objectGeneratorOf(new JsonTransformer(generator.value));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which converts specified instance of {@link ObjectGenerator} to
     * JSON.
     *
     * @param generator Instance of {@link ObjectGenerator} which value will be converted to JSON.
     * @param objectMapper Object mapper to use when converting value to JSON.
     * @return An instance of {@link ObjectGenerator} which converts specified instance of {@link ObjectGenerator} to
     *         JSON.
     */
    public static ObjectGenerator<String> json(ObjectGenerator<?> generator, ObjectMapper objectMapper) {
        return objectGeneratorOf(new JsonTransformer(generator.value, objectMapper));
    }




    //IMPORT YAML

    /**
     * Imports a yaml config to use it as ObjectGenerator itself or in another ObjectGenerator
     * @param configPath path to the yaml file you want to import
     * @return an {@code ObjectGenerator<Object>} that generates objects as defined in the specified yaml file
     * @throws ranger.parser.ConfigException if any error occurs during parsing YAML config
     */
    public static <T> ObjectGenerator<T> importYaml(String configPath) {
        return importYaml(configPath, "$.");
    }

    /**
     * Imports a yaml config to use it as ObjectGenerator itself or in another ObjectGenerator
     * @param configPath path to the yaml file you want to import
     * @param yamlRoot path to yaml root which contains the fields "values" and "output" (default: {@code "$."})
     * @return an {@code ObjectGenerator<Object>} that generates objects as defined in the specified yaml file
     * @throws ranger.parser.ConfigException if any error occurs during parsing YAML config
     */
    @SuppressWarnings("unchecked")
    public static <T> ObjectGenerator<T> importYaml(String configPath, String yamlRoot) {
        try {
            return (ObjectGenerator<T>) new ConfigurationParser(configPath, yamlRoot).build();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }

    }




    //HELPER FUNCTIONS

    /**
     * Constructs ObjectGenerator of given Value
     */
    private static <T> ObjectGenerator<T> objectGeneratorOf(Value<T> value) {
        return new ObjectGenerator<>(value);
    }

    /**
     * Retrieves Value out of ObjectGenerator
     */
    @SuppressWarnings({ "unchecked" })
    private static <T> Value<T> valueOf(T object) {
        return object instanceof ObjectGenerator ? ((ObjectGenerator<T>) object).value : ConstantValue.of(object);
    }

    /**
     * Retrieves Values out of ObjectGenerators
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static List<Value<?>> valuesOfRaw(List<Object> objects) {
        return (List) valuesOf(objects);
    }

    /**
     * Retrieves Values out of ObjectGenerators
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static <T> List<Value<T>> valuesOf(List<T> objects) {
        List<Value<T>> result = new ArrayList<>();
        for (T object : objects) {
            if (object instanceof ObjectGenerator) {
                result.add(((ObjectGenerator) object).value);
            } else {
                result.add(ConstantValue.of(object));
            }
        }
        return result;
    }


}
