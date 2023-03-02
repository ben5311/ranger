# Java API
**Table of Contents**

- [Overview](#overview)
- [ObjectGeneratorBuilder](#objectgeneratorbuilder)
  - [Object mapping](#object-mapping)
- [Builder methods](#builder-methods)
  - [Constant](#constant)
  - [Random](#random)
    - [Random with discrete values](#random-with-discrete-values)
    - [Random with range](#random-with-range)
  - [Circular](#circular)
    - [Circular with discrete values](#circular-with-discrete-values)
    - [Circular with range](#circular-with-range)
  - [Weighted discrete values](#weighted-discrete-values)
  - [Exact weighted discrete values](#exact-weighted-discrete-values)
  - [Distributions](#distributions)
    - [Uniform distribution](#uniform-distribution)
    - [Normal distribution](#normal-distribution)
  - [Random content string](#random-content-string)
  - [Xeger regex generator](#xeger-regex-generator)
  - [Now methods](#now-methods)
  - [Arithmetic methods](#arithmetic-methods)
  - [UUID](#uuid)
  - [List](#list)
  - [Random Length List](#random-length-list)
  - [Empty list](#empty-list)
  - [Empty map](#empty-map)
  - [CSV functions](#csv-functions)
    - [CSV Sequential](#csv-sequential)
    - [CSV Circular](#csv-circular)
    - [CSV Random](#csv-random)
    - [CSV Weighted](#csv-weighted)
  - [Dependent Values](#dependent-values)
    - [Switch](#switch)
    - [Map](#map)
  - [Transformer](#transformer)
    - [Case transformer](#case-transformer)
    - [Ascii transformer](#ascii-transformer)
    - [String transformer](#string-transformer)
    - [Stringf transformer](#stringf-transformer)
    - [Time transformer](#time-transformer)
  - [Getter Transformer](#getter-transformer)
  - [JSON transformer](#json-transformer)
  - [Clone ObjectGenerator](#clone-objectgenerator)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Overview

Central type of Java API is `ObjectGenerator` with its two simple methods: `T next()` and `List<T> generate(int numberOfObjects)`. \
`T next()` evaluates next value and `List<T> generate(int numberOfObjects)` evaluates next `numberOfObjects` values and puts them into list.

You can construct plain (single value) `ObjectGenerator`s with helper methods in `BuilderMethods` class.
You can then combine several plain `ObjectGenerator`s with `ObjectGeneratorBuilder` to a complex `ObjectGenerator`
(containing multiple other `ObjectGenerator`s  as attributes).

# ObjectGeneratorBuilder

`ObjectGeneratorBuilder` has method `ObjectGeneratorBuilder prop(String property, V value)` which will add the given
Value with key ```property``` under `ObjectGeneratorBuilder`. 
Value can be any type of object. Depending on whether value is another `ObjectGenerator` or any other object, `prop` method will have different behavior.

If value is of type `ObjectGenerator`, it will be evaluated newly on every generation.
```java
ObjectGenerator<Integer> intGenerator = randomWithin(1, 7);
ObjectGenerator<Map<String, Object>> generator = new ObjectGeneratorBuilder().prop("dice", intGenerator).build();
```

This will result in possible value sequence: 2, 4, 1, 1, 5, 2, 6, 5, 6, 3, 1 for property `dice`.

If value is any other object, it's value will be used as constant value.
```java
ObjectGenerator<Map<String, Object>> generator = new ObjectGeneratorBuilder().prop("dice", 4).build();
```

This will result in generator always generating value 4 for property `dice`. 

It is possible to daisy-chain multiple calls of `prop()` method.

ObjectGeneratorBuilder returns complex ObjectGenerator on `build()` that always generates a `Map<String, Object>` containing previously added properties.

### Object mapping

With a simple modification you can automatically turn generated `Map<String, Object>` into instances of your model class. A simple example is following model class

```java
class User {
    public String username;
    public int age;
}
```

with this ObjectGenerator

```java
ObjectGenerator<User> user = new ObjectGeneratorBuilder()
        .prop("username", random("max123", "james007", "x_taeke_x"))
        .prop("age", randomWithin(14, 90))
        .build(User.class);
```

As we invoke `build()` method with `User.class` argument, Ranger tries to convert it's properties to an instance of class User.

You can find additional information about the object mapping by searching for usage of `ObjectMapper` from `com.fasterxml.jackson`.

# Builder methods

Below is given a list of helper methods for construction of plain `ObjectGenerator`. Methods are provided by `BuilderMethods` class. \
For easy usage, you should static import all methods from BuilderMethods: ```import static ranger.BuilderMethods.*;```

Almost all methods support all Java primitive number types (byte, short, int, long, float, double) as arguments, there is no need for limiting only on int, long or double types if other types are more suitable in particular case.

## Constant

Creates constant object generator which will always return same value.

```java
public static ObjectGenerator<T> constant(T value)
```

Examples:

```java
ObjectGenerator<String> names = constant("Peter");

ObjectGenerator<Integer> age = constant(28);
```

`names` generator will always return `"Peter"`.
`age` generator will always return `28`.

Adding a constant Value to `ObjectGeneratorBuilder` is redundant because any non `ObjectGenerator`
object will automatically be converted to constant value.
But there are some cases where you have to explicitly supply an `ObjectGenerator` as argument 
(e.g. length when using `randomContentString()`), then you can supply a constant value with `constant()`.

## Random

There are two different random ObjectGenerators: random with discrete values and random with range.

### Random with discrete values

`random()` generates random value from list of possible values. It has optional `distribution` argument which can be set 
(default `distribution` is `UniformDistribution`). \
Elements of the list can be of any type and it does not need to be same type for 
all the elements, although there are probably rare use cases where different types within the list will be needed. \
There are several parameter variations:

```java
public static ObjectGenerator<T> random(T... values)
public static ObjectGenerator<T> random(Distribution distribution, T... values)
public static ObjectGenerator<T> random(List<T> values)
public static ObjectGenerator<T> random(Distribution distribution, List<T> values)
```

Examples:

```java
ObjectGenerator<String> names = random("Mike", "Peter", "Adam", "Mathew");
ObjectGenerator<String> names = random(new UniformDistribution(), "a", "b", "c", "d");

List<String> valueList = Arrays.asList("Mike", "Peter", "Adam", "Mathew");
ObjectGenerator<String> names = random(valueList);
ObjectGenerator<String> names = random(new UniformDistribution(), valueList);
```

Any variation would create `ObjectGenerator` which can generate possible sequence:
```
"Peter", "Peter", "Mathew", "Adam", "Mathew", "Peter", "Mike", "Mike", "Adam", ...
```

You can also use `random()` with mixed type values and with ObjectGenerator values:
```java
ObjectGenerator<Integer> number = randomWithin(1, 10);
ObjectGenerator<String> text = circular("first", "second");
ObjectGenerator<?> value = random("Mike", number, text);
```

`value` ObjectGenerator will generate something like
```
4, "Mike", 7, "first", "second", "Mike", "Mike", 9, "first", ...
```

### Random with range

`randomWithin()` generates random value within specified range. It has optional `useEdgeCases` and `distribution` arguments.
Default value for `useEdgeCases` is `false` and default `distribution` is `UniformDistribution`.
There are several parameter variations:

```java
public static ObjectGenerator<T> randomWithin(T begin, T end)
public static ObjectGenerator<T> randomWithin(T begin, T end, boolean useEdgeCases)
public static ObjectGenerator<T> randomWithin(T begin, T end, boolean useEdgeCases, Distribution distribution)
```

Examples:

```java
ObjectGenerator<Integer> age = randomWithin(1, 100);
ObjectGenerator<Integer> age = randomWithin(1, 100, false);
ObjectGenerator<Integer> age = randomWithin(1, 100, false, new UniformDistribution());
```

Any variation would create `ObjectGenerator` which can generate possible sequence:
```
1, 36, 17, 87, 43, 55, 91, 83, 2, 21, 76
```

The range is inclusive only at beginning: **[a, b)**. So the last number in range is b-1 (with Integer range) and b will never be generated.
So if you want to include b you should higher the right edge by one step. \
If you want to ensure that your result always contains both edge cases (`a` and `b-1`) you can set `useEdgeCases` to `true`. That will cause that the first generated value is always
`a`, the second value `b-1` and after that random numbers within **[a, b)** follow. 

```java
ObjectGenerator<Integer> age = randomWithin(1, 100, true);
```

This code would generate sequence that always has first two elements 1 and 99 as those are the edge cases. After that, any random value would be picked.

You can use `randomWithin()` with all numeric data types (int, float, long, double, etc.), with Date, 
LocalDate, LocalDateTime and with chars. \
This example with chars

```java
ObjectGenerator<Character> letter = randomWithin('a', 'z');
```

will generate a random lowercase letter. \
The given range contains all characters between a and b in the **unicode table** with a and b inclusive: **[a, b]**. Verify you char range if the produced chars look different than expected.

## Circular

There are two different circular ObjectGenerators: circular with discrete values and circular with range.

### Circular with discrete values

`circular()` generates values in the order they are specified until the end. Then starts again from beginning.
There are two parameter variations:

```java
public static ObjectGenerator<T> circular(T... values)
public static ObjectGenerator<T> circular(List<T> values)
```

Examples:

```java
ObjectGenerator<String> serverIpAddress = circular("10.10.0.1", "10.10.0.2", "10.10.0.3", "10.10.0.4");

List<String> ipAddresses = Arrays.asList("10.10.0.1", "10.10.0.2", "10.10.0.3", "10.10.0.4");
ObjectGenerator<String> serverIpAddress = circular(ipAddresses);
```

Any variation would create `ObjectGenerator` which will generate following sequence:
```
"10.10.0.1", "10.10.0.2", "10.10.0.3", "10.10.0.4", "10.10.0.1", "10.10.0.2", "10.10.0.3", "10.10.0.4", "10.10.0.1", ...
```

You can also use discrete circular with mixed type values and with ObjectGenerator values:
```java
ObjectGenerator<Integer> number = randomWithin(1, 10);
ObjectGenerator<String> text = circular("first", "second");
ObjectGenerator<?> value = circular("Mike", number, text);
```

`value` ObjectGenerator will generate these exact sequence of values
```
"Mike", 3, "first", "Mike", 6, "second", "Mike", 1, "first", ...
```
except that the numbers are random.

### Circular with range

`circularWithin()` generates values from the beginning of the range to the end using step as increment. When end is reached, values are generated again from the beginning.
Currently supports all numeric data types (int, float, long, double, etc.), chars and Date. Usage:

```java
public static ObjectGenerator<T> circularWithin(T beginning, T end, T step)
```

Examples:
```java
ObjectGenerator<Double> temperature = circularWithin(12d, 25d, 0.2d);
ObjectGenerator<Integer> dayOfYear = circularWithin(1, 365, 1);
```

First generator would generate following sequence:
```
12.0, 12.2, 12.4, 12.6, 12.8, 13.0, ..., 24.6, 24.8, 25.0, 12.0, 12.2, ...
```
*(Results could be inaccurate because of Java's floating point arithmetic)*

And second would generate:
```
1, 2, 3, 4, 5, 6, ..., 363, 364, 365, 1, 2, 3, ...
```

Usage of `circularWithin()` with chars:

```java
public static ObjectGenerator<T> circularWithin(char beginning, char end, int step)
```

Example:
```java
ObjectGenerator<Character> letter = circularWithin('a', 'z', 1);
```

would generate:
```
a, b, c, d, e ..., x, y, z, a, b ...
```
Because chars are derived from unicode table, incrementing by 1 means that Ranger adds 1 to the first char's unicode position on every iteration and then 
returns the char on this position. So double check your char range if the result does not look like expected.

Usage of `circularWithin()` with Date:

```java
public static ObjectGenerator<T> circularWithin(Date beginning, Date end, int step)
```

Example:
```java
ObjectGenerator<Date> date = circularWithin(new Date(119, 0, 1), new Date(119, 11, 31), 1);
```

would generate every day of 2019 as Date object starting from 2019-01-01 in natural order.

## Weighted discrete values

`weighted()` generates values with probability based on their weights.

For using `weighted()`, you must wrap your values into WeightedValuePairs using

```java
public static WeightedValuePair<T> weightPair(T value, double weight)
```

Example:

```java
WeightedValuePair<String> weightPair = weightPair("Stephen", 11.5d)
```

then you can supply your WeightedValuePairs to `weighted()`:

```java
public static ObjectGenerator<T> weighted(WeightedValuePair<T>... pairs)
public static ObjectGenerator<T> weighted(List<WeightedValuePair<T>> pairs)
```

Examples:

```java
ObjectGenerator<String> names = weighted(weightPair("Stephen", 11.5d), weightPair("George", 50), weightPair("Charles", 38.5));

List<WeightedValuePair<String>> weightPairs = Arrays.asList(weightPair("Stephen", 11.5d), weightPair("George", 50), weightPair("Charles", 38.5));
ObjectGenerator<String> names = weighted(weightPairs);
```

Any variation would create `ObjectGenerator` which can generate possible sequence:

```
"Stephen", "George", "Charles", "George", "Charles", "George", "George", "Stepen", "Charles", ...
```

Where probability for name "George" is 50%, for "Charles" 38.5% and for "Stephen" 11.5%. However, weights do not need to sum up to 100, this example has it just for purpose of calculating the probability easily.

## Exact weighted discrete values

Having `weighted()` is great, at least for some use cases. But there are times where you will need to be precise, 
you cannot go with `weighted()`, especially when working with small numbers (< 1 000 000). \
`exactly()` gives you precision, at the cost of limited number of objects.

For using  `exactly()`, you must wrap your values into CountValuePairs using

```java
public static CountValuePair<T> countPair(T value, long count)
```

Example:

```java
CountValuePair<String> countPair = countPair("Stephen", 11);
```

then you can supply your CountValuePairs to `exactly()`:

```java
public static ObjectGenerator<T> exactly(CountValuePair<T>.... pairs)
public static ObjectGenerator<T> exactly(List<CountValuePair<T>> pairs)
```

Examples:

```java
ObjectGenerator<String> names = exactly(countPair("Stephen", 11), countPair("George", 50), countPair("Charles", 39));

List<CountValuePair<String>> countPairs = Arrays.asList(countPair("Stephen", 11), countPair("George", 50), countPair("Charles", 39));
ObjectGenerator<String> names = exactly(countPairs);
```

Values will be generated by probability specified by count. Count in this case needs to be of long type. \
If 100 elements are generated in this case, "George" would be generated exactly 50, "Charles" 39 and "Stephen" 11 times.
If generation of more than 100 elements is attempted, exception will be thrown.
If less than 100 elements are generated, they will follow weighted distribution. \
In order to provide precision, exact weighted distribution discards particular value from possible generation if value reached its quota. That is the reason that there is a limitation to number of generated values.


## Distributions

You can use Distributions with several functions like `random()` and random length`list()`.
Currently only two distributions are supported: [Uniform](#uniform-distribution) and [Normal](#normal-distribution) distribution.

### Uniform distribution

Uniform distribution can be simply used by stating `uniform()`. Usually it is also the default Distribution when Distribution argument is omitted.
With Uniform distribution all values have the same probability.

```java
public static UniformDistribution uniform()
```

### Normal distribution

There are two methods for creating normal distributions: 

```java
public static NormalDistribution normal()
public static NormalDistribution normal(double mean, double standardDeviation, double lowerBound, double upperBound)
```

With first method default values `mean=0.5`, `standardDeviation=0.125`, `lowerBound=0` and `upperBound=1` are used.

`mean` is the mean of your normal distribution, `standardDeviation` the standardDeviation and the bounds
represent the range of values to be generated. 

Example:

```java
ObjectGenerator<Byte> age1 = randomWithin((byte) 1, (byte) 100, true, normal());
ObjectGenerator<Double> age2 = randomWithin(1.0, 100.0, false, normal(0, 1, -4, 4));
```

With `age2` example the **normal distribution** only
generates values in between -4 and 4, while the most values will be around 0. \
But this doesn't mean that `age2` **ObjectGenerator** will only generate values between -4 and 4. Instead it scales the mean, 
standard deviation and the range of it's normal distribution to it's own range (`1..100`).
That will lead to `age2` generating values around 50 most of the time (scaled mean) with an approximate standard deviation
of 11 (scaled standard deviation). 

In your own interest you should simplify handling of `normal()` distribution by passing the same range to distribution as to your
`randomWithin()` ObjectGenerator when possible. Then you can better understand what effect changing mean and standardDeviation has.


## Random content string

`randomContentString()` generates random string of specified length with optional character ranges. If ranges not specified, 
string will contain only characters from following ranges: `'A'-'Z'`, `'a'-'z'` and `'0'-'9'`. Length needs to be specified as 
an object generator which can evaluate to different number each time. Uniform distribution is used to select characters from 
character ranges.

There are several parameter variations:

```java
public static ObjectGenerator<String> randomContentString(ObjectGenerator<Integer> lengthGenerator)
public static ObjectGenerator<String> randomContentString(ObjectGenerator<Integer> lengthValue, Range<Character>... ranges)
public static ObjectGenerator<String> randomContentString(ObjectGenerator<Integer> lengthValue, List<Range<Character>> ranges)
```

The two last methods expect char ranges as type `Range<Character>`. You can easily create it with

```java
public static Range<Character> range(char beginning, char end)
```

Examples:

```java
ObjectGenerator<String> randomString1 = randomContentString(constant(5));
ObjectGenerator<String> randomString2 = randomContentString(constant(8), range('A', 'F'), range('0', '9'));
List<Range<Character>> ranges = Arrays.asList(range('A', 'F'), range('0', '9'));
ObjectGenerator<String> randomString2 = randomContentString(constant(8), ranges);
ObjectGenerator<String> randomString3 = randomContentString(randomWithin(5, 10), range('A', 'Z'), range('0', '9'));
```

`randomString1` will generate strings of length 5 with characters from ranges: `'A'-'Z'`, `'a'-'z'` and `'0'-'9'`.
```
"Ldsfa", "3Jdf0", "AOSyu", "qr4Qe", "sf23c", "sdFfi", "320fS", ...
```

`randomString2` will generate strings of length 8 from specified range of characters.
```
"EF893232", "2E49D0AB", "BE129E15", "938FFC1C", "BB8A43ED", "829D1CA2", ...
```

`randomString3` will generate strings of length from 5 to 10 and with characters from ranges: `'A'-'Z'` and `'0'-'9'`.
```
"FASDFO23", "32421", "DFDSAF", "FDSFIAH98Q", "IUEK92", "NVISHDF82", ...
```

## Xeger regex generator

Besides ```randomContentString()``` you can generate random Strings in a more advanced way 
with ```xeger()```. There are two parameter variations:
 
```java
public static ObjectGenerator<String> xeger(String regexPattern)
public static ObjectGenerator<String> xeger(ObjectGenerator<String> regexPatternGenerator)
```
 
Xeger parses your regexPattern (or the pattern retrieved by regexPatternGenerator) and then generates
a random String that matches your pattern. 
A simple example is:

```java
ObjectGenerator<String> germanIban = xeger("DE[0-9]{20}");
```

that produces Strings like
```
"DE83726583748374589378", "DE04837264756294738477", "DE88226374004827383492", ...
```

Let's have a look at a more complex example:

```java
ObjectGenerator<String> email = xeger("[a-z]{6,12}(\\.[a-z\\d]{2,4})?@[a-z]{4,10}\\.(com|de|gb|pl|info)");
```

which could generate
```
honabi@dpcbu.info
joxglp.45@kgfpw.info
tsqwgj.ua@ychh.pl
vozfzk.aoyg@bvbnl.de
scbgdjk@ntck.pl
nlcgpi.6g@fbjf.info
```

It uses Java regex syntax explained here: https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html. \
However, not everything is available. Everything below is supported:

| Construct | Matches |
| --- | --- |
| | |
| **Characters** | |
| `x` | `The character x ` |
| `\\` | `The backslash character ` |
| `\t` | `The tab character ('\u0009') ` |
| `\n` | `The newline (line feed) character ('\u000A') ` |
| `\r` | `The carriage-return character ('\u000D') ` |
| `\f` | `The form-feed character ('\u000C') ` |
| | |
| **Character classes** | |
| `[abc]` | `a, b, or c (simple class) ` |
| `[^abc]` | `Any character except a, b, or c (negation) ` |
| `[a-zA-Z]` | `a through z or A through Z, inclusive (range) ` |
| `[a-z&&[d-f]]` | `d, e, or f                       (intersection)` (Using empty intersection (like `[a-c&&[d-f]]`) in a part of your regex will lead to a **complete empty String**!) |
| `[a-z&&[^bc]]` | `a through z, except for b and c: [ad-z] (subtraction)` |
| `[a-z&&[^m-p]]` | `a through z, and not m through p: [a-lq-z] (subtraction)` |
| |  |
| **Predefined character classes** | |
| `.` | `Any single character  ` |
| `\d` | `A digit: [0-9] ` |
| `\D` | `A non-digit: [^0-9] ` |
| `\s` | `A whitespace character: [ \t\n\x0B\f\r] ` |
| `\S` | `A non-whitespace character: [^\s] ` |
| `\w` | `A word character: [a-zA-Z_0-9] ` |
| `\W` | `A non-word character: [^\w] ` |
| | |
| **POSIX character classes (US-ASCII only)** | |
| `\p{Lower}` | `A lower-case alphabetic character: [a-z] ` |
| `\p{Upper}` | `An upper-case alphabetic character:[A-Z] ` |
| `\p{ASCII}` | `All ASCII:[\x00-\x7F] ` |
| `\p{Alpha}` | `An alphabetic character:[\p{Lower}\p{Upper}] ` |
| `\p{Digit}` | `A decimal digit: [0-9] ` |
| `\p{Alnum}` | `An alphanumeric character:[\p{Alpha}\p{Digit}] ` |
| `\p{Punct}` | `Punctuation: One of !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~ ` |
| `\p{Graph}` | `A visible character: [\p{Alnum}\p{Punct}] ` |
| `\p{Print}` | `A printable character: [\p{Graph}\x20] ` |
| `\p{Blank}` | `A space or a tab: [ \t] ` |
| `\p{Cntrl}` | `A control character: [\x00-\x1F\x7F] ` |
| `\p{XDigit}` | `A hexadecimal digit: [0-9a-fA-F] ` |
| `\p{Space}` | `A whitespace character: [ \t\n\x0B\f\r] ` |
| | |
| **Greedy quantifiers** | |
| `X?` | `X, once or not at all ` |
| `X*` | `X, zero or more times ` |
| `X+` | `X, one or more times ` |
| `X{n}` | `X, exactly n times ` |
| `X{n,}` | `X, at least n times ` |
| `X{n,m}` | `X, at least n but not more than m times ` |
| | |
| **Logical operators** | |
| `XY` | `X followed by Y ` |
| `X`&#124;`Y` | `Either X or Y ` |
| `(XY)` | `XY, as a group` |

If you want to use special characters listed above you need to escape them with a 
prepended ```\ ``` like ```\&``` for the & sign. \
Remember that the backslash ```\ ``` is a special character in Java, and so you need
to escape **every** backslash listed above with another one when entering it in Java source code: 
`\d` becomes `\\d`, `\p{Graph}` becomes `\\p{Graph}` and `\$` becomes `\\$`.

With very complex regex patterns there is a small chance that loading the xeger value fails 
and an Exception will be thrown. Then try to gradually simplify your pattern.
When your pattern always generates unexpected empty Strings, check if you misused
a special character in above list.

Note that with short patterns you should prefer using ```randomContentString()``` as 
it has a better performance and is less error prone.


## Now methods

These methods return ObjectGenerator that generates current time:
```java
public static ObjectGenerator<Long> now()   //returns UTC time in milliseconds
public static ObjectGenerator<Date> nowDate()
public static ObjectGenerator<LocalDate> nowLocalDate()
public static ObjectGenerator<LocalDateTime> nowLocalDateTime()
```

## Arithmetic methods

These methods can be used to perform arithmetic operations on ObjectGenerators:

```java
public static <T extends Number> ObjectGenerator<T> add(Class<T> type, ObjectGenerator<? extends Number> summand1, ObjectGenerator<? extends Number> summand2)
public static <T extends Number> ObjectGenerator<T> subtract(Class<T> type, ObjectGenerator<? extends Number> minuend, ObjectGenerator<? extends Number> subtrahend)
public static <T extends Number> ObjectGenerator<T> multiply(Class<T> type, ObjectGenerator<? extends Number> factor1, ObjectGenerator<? extends Number> factor2)
public static <T extends Number> ObjectGenerator<T> divide(Class<T> type, ObjectGenerator<? extends Number> dividend, ObjectGenerator<? extends Number> divisor)
```

The `type` argument specifies the output type `T` of ObjectGenerator performing the arithmetic operation.
The operands can be of any `Number` type and don't need to match `T` because both operands are automatically converted into `T` before calculation. 
Note that this can lead to precision loss (e.g. when converting double `0.5` into integer `0`). \
Possible types for `type` are `Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class`.

Examples:

```java
ObjectGenerator<Byte> first = add(Byte.class, randomWithin(1, 10), constant(4));                //generates Byte from 5 to 13
ObjectGenerator<Integer> second = add(Integer.class, constant(10), constant(5));                //generates Integer 15
ObjectGenerator<Long> third = add(Long.class, constant(86400000), now());                       //generates Long current millis one day in future
ObjectGenerator<Float> fourth = add(Float.class, constant(1), constant(2));                     //generates Float 3.0f
ObjectGenerator<Integer> fifth = subtract(Integer.class, randomWithin(0, 100), constant(10));   //generates Integer from -10 to 89
ObjectGenerator<Long> sixth = subtract(Long.class, now(), constant(345600000L));                //generates Long current millis 4 days in the past
ObjectGenerator<Integer> seventh = multiply(Integer.class, constant(60), constant(60));         //generates Integer 3600
ObjectGenerator<Long> eighth = multiply(Long.class, circular(1, 2, 3), constant(5));            //generates Long 5, 10, 15, 5, ....
ObjectGenerator<Float> ninth = multiply(Float.class, constant(44), constant(0.05));             //generates Float 2.2f
ObjectGenerator<Double> tenth = divide(Double.class, constant(4), constant(2));                 //generates Double 2.0
ObjectGenerator<Integer> eleventh = add(Integer.class, constant(0.5), constant(0.5));           //generates Integer 0 (precision loss!)
```


## UUID

Generates UUID strings.

```java
ObjectGenerator<String> uuid = uuid();
```

Possible sequence is:
```
"27dbc38f-cadf-4d42-b18a-44c839e8b8f1", "575fb812-bb98-4f76-b31b-bf42e3ac2d62", "a7e229f3-875d-4a6a-9a5d-fb0670c3afdf", ...
```


## List

`list()` generates list out of specified values.
There are two parameter variations:

```java
public static ObjectGenerator<List<T>> list(T... values)
public static ObjectGenerator<List<T>> list(List<T> values)
```

Examples:

```java
ObjectGenerator<List<Object>> names = list("Emma", circular("Mike", "Steve", "John"), "Ned", circular("Jessica", "Lisa"));

List nameList = Arrays.asList("Ema", circular("Mike", "Steve", "John"), "Ned", circular("Jessica", "Lisa"));
ObjectGenerator<List<String>> names = list(nameList);
```

Any variation would create `ObjectGenerator` which will generate following sequence:
```
["Emma", "Mike", "Ned", "Jessica"]
["Emma", "Steve", "Ned", "Lisa"]
["Emma", "John", "Ned", "Jessica"]
["Emma", "Mike", "Ned", "Lisa"]
...
```


## Random Length List

Generates random length list out of specified `ObjectGenerator`.
There are two parameter variations:

```java
public static ObjectGenerator<List<T>> list(int minLength, int maxLength, ObjectGenerator<T> elementGenerator)
public static ObjectGenerator<List<T>> list(int minLength, int maxLength, ObjectGenerator<T> elementGenerator, Distribution distribution)
```

`minLength` is inclusive and `maxLength` is exclusive.

Examples:

```java
ObjectGenerator<List<Integer>> numbers = list(3, 6, randomWithin(10, 100));
ObjectGenerator<List<Integer>> numbers = list(3, 6, randomWithin(10, 100), new UniformDistribution());
```

Any variation would create `ObjectGenerator` which will generate lists with minimum 3 and maximum 5 members:
```
[16, 36, 44]
[92, 96, 33, 25]
[12, 14, 54]
[78, 79, 35, 88, 96]
...
```

Note that `elementGenerator` used within random list generator should not be used in any other 
ObjectGenerator within hierarchy since its values are reset and regenerated multiple times while list is constructed. 
Using it in any other place would result in having different values across value hierarchy. \
If you want to reuse an existing generator anyway, you can clone it (see [Clone ObjectGenerator](#clone-objectgenerator) section).


## Empty list

Generates empty list every time:

```java
public static ObjectGenerator<List<T>> emptyList()
```


## Empty map

Generates empty map every time:

```java
public static ObjectGenerator<Map<K, V>> emptyMap()
```

May be useful in some cases when outputting generated objects as `json()`.


## CSV functions

It is also possible to use CSV file as source of data and to combine it with other Ranger functions and values.
The sections below describe the sequential, circular, random and weighted csv functions.

### CSV Sequential

the `csv()` function processes CSV files record-by-record. \
There are four parameter variations. 
These four variations are equal, while the first three are shorthand versions 
with reasonable defaults:

```java
public static ObjectGenerator<Map<String, String>> csv(String path)
public static ObjectGenerator<Map<String, String>> csv(String path, char delimiter)
public static ObjectGenerator<Map<String, String>> csv(String path, char delimiter, boolean withHeader)
public static ObjectGenerator<Map<String, String>> csv(String path, char delimiter, boolean withHeader, String recordSeparator, boolean trim, Character quote, char commentMarker, boolean ignoreEmptyLines, String nullString)
```

Arguments:

| argument | description | default |
| -------- | ----------- | ------- |
| `path` | absolute or relative path to the csv file you want to import (required) | - |
| `delimiter` | character that separates the columns | `','` |
| `withHeader` | `true` if first record is to be interpreted as header, `false` if first record is to be interpreted as plain record. Must be `true` if you want to access the columns by their names | `true` |
| `recordSeparator` | delimiter that separates the records | `"\n"` |
| `trim` | `true` if each value is to be trimmed for leading and trailing whitespaces | `true` |
| `quote` | character that will be stripped from beginning and end of each column if present. If set to `null`, no characters will be stripped | `null` |
| `commentMarker` | character to use as a comment marker, everything after it is considered comment | `'#'` |
| `ignoreEmptyLines` | `true` if empty lines are to be ignored, otherwise `false` | `true` |
| `nullString` | converts string with given value to null. If set to `null`, no conversion will be done | `null` |

Ranger will provide the CSV file's data by parsing one CSV record after another and returning record values as Strings. \
If for example we have a CSV with following values:

```csv
firstname,lastname,zip,city,country
John,Smith,555-1331,New York,US
Peter,Braun,133-1123,Berlin,DE

# Commented line,Should, not be taken,into,account
Jose,Garcia,328-3221,Madrid,ES
```

and following code:

```java
ObjectGenerator<Map<String, String>> csv = csv("filePath", ',', true, "\n", true, '"', '#', true, "NULL");
for (int i = 0; i < 3; i++) {
    Map<String, String> val = csv.next();
    System.out.println("Name: "+val.get("firstname")+" "+val.get("lastname")+" - "+val.get("zip")+" "+val.get("city"));
}
```

It would generate following lines:

```
John Smith - New York US
Peter Braun - Berlin DE
Jose Gercia - Madrid ES
```

If you set ```withHeader``` to ```false``` because your CSV file does not have a header, you can retrieve CSV's 
columns with ```c0,c1..cn``` syntax.

In this example our CSV has no header record:

```csv
John,Smith,555-1331,New York,US
Peter,Braun,133-1123,Berlin,DE

# Commented line,Should, not be taken,into,account
Jose,Garcia,328-3221,Madrid,ES
```

Following code will produce the same output as above:


```java
ObjectGenerator<Map<String, String>> csv = csv("filePath", ',', false, "\n", true, '"', '#', true, "NULL");
for (int i = 0; i < 3; i++) {
    Map<String, String> val = csv.next();
    System.out.println("Name: "+val.get("c0")+" "+val.get("c1")+" - "+val.get("c3")+" "+val.get("c4"));
}
```

### CSV Circular

Using the ```csv()``` function above limits the amount of creatable objects to the amount of CSV records.
Alternatively, you can use the ```csvCircular()``` function. It does the same as the ```csv()``` function but when 
Ranger reaches the CSV's last record, it just starts again at the first record.
The argument syntax is exactly the same:

```java
public static ObjectGenerator<Map<String, String>> csvCircular(String path)
public static ObjectGenerator<Map<String, String>> csvCircular(String path, char delimiter)
public static ObjectGenerator<Map<String, String>> csvCircular(String path, char delimiter, boolean withHeader)
public static ObjectGenerator<Map<String, String>> csvCircular(String path, char delimiter, boolean withHeader, String recordSeparator, boolean trim, Character quote, char commentMarker, boolean ignoreEmptyLines, String nullString)
```

Please refer to [CSV Sequential](#csv-sequential) section for the meaning of the arguments.

### CSV Random

The ```csvRandom()``` function generates a *random* record out of the CSV file. \
The usage is equal to ```csv()``` and ```csvCircular()```:

```java
public static ObjectGenerator<Map<String, String>> csvRandom(String path)
public static ObjectGenerator<Map<String, String>> csvRandom(String path, char delimiter)
public static ObjectGenerator<Map<String, String>> csvRandom(String path, char delimiter, boolean withHeader)
public static ObjectGenerator<Map<String, String>> csvRandom(String path, char delimiter, String recordSeparator, boolean trim, Character quote, char commentMarker, boolean ignoreEmptyLines, String nullString, boolean withHeader)
```

Please refer to [CSV Sequential](#csv-sequential) section for the meaning of the arguments above.

Be aware that ```csvRandom()``` loads the whole CSV file in memory before generating the first record. Thus it can be slower than
```csv()``` and ```csvCircular()``` which read the CSV file record by record. But this only has a noticeable effect when reading CSV files with 
hundred thousands records and more.

CSV Random never stops generating new records and due to the randomness it likely generates records multiple times.

### CSV Weighted

Lastly, there is the ```csvWeighted()``` function to read CSV files and randomly generate records out of it with respect to a predefined weight.
This weight is given as a column in this CSV.

```java
public static ObjectGenerator<Map<String, String>> csvWeighted(String path, String weightField)
public static ObjectGenerator<Map<String, String>> csvWeighted(String path, char delimiter, String weightField)
public static ObjectGenerator<Map<String, String>> csvWeighted(String path, char delimiter, boolean withHeader, String weightField)
public static ObjectGenerator<Map<String, String>> csvWeighted(String path, char delimiter, String recordSeparator, boolean trim, Character quote, char commentMarker, boolean ignoreEmptyLines, String nullString, boolean withHeader, String weightField)
```


The first arguments equal the ones described in [CSV Sequential](#csv-sequential) section. The only difference is that you must additionally supply 
the column key for the weight values (```weightField```) as the last argument of either function variation. 
The key can either be in ```c0,c1...cn``` syntax or it can be a header key (if ```withHeader``` = ```true```).

Take this CSV file as an example
```csv
city,state,population
Munich,BY,1400000       # 1.4 million
Bonn,NW,310000          # 310 thousand
Bad Honnef,NW,25000     # 25 thousand
```

with this ObjectGenerator

```java
ObjectGenerator<Map<String, String>> csv = csvWeighted("filePath.csv", "population");
```

Ranger will generate ```Munich```'s record the most of the times, ```Bonn```'s a few times and ```Bad Honnef```'s only with a proportion of about 2%. 

As described in ```CSV Random``` section, ```CSV Weighted``` also loads the whole CSV file in memory at the beginning which could lead to performance
impacts when reading very big files.


## Dependent Values

There are two ways to define Values that relate to other Values: `switcher()` function and `map()` function.

### Switch

```switcher()``` chooses one of your custom arguments dependently on source's value. 

Take an example:

```java
ObjectGenerator<String> femaleFirstname = random("Anna", "Lena", "Maria");
ObjectGenerator<String> maleFirstname = random("Jan", "Louis", "Paul");
ObjectGenerator<String> gender = random("female", "male");
ObjectGenerator<String> genderFirstname = switcher(gender, femaleFirstname, maleFirstname);

ObjectGenerator<Map<String, Object>> person = new ObjectGeneratorBuilder()
        .prop("gender", gender)
        .prop("firstname", genderFirstname)
        .build();
``` 

`person` ObjectGenerator will generate such objects:

```
{gender=female,  firstname=Anna}
{gender=male, firstname=Jan}
{gender=female, firstname=Maria}
{gender=female, firstname=Anna}
{gender=male, firstname=Louis}
...
```

where each person has a firstname matching their gender. 

The ```switcher()``` function thereby enables you
to create relations between different random values which can be extremely useful in lots of situations.
The usage is

```java
public static ObjectGenerator<T> switcher(ObjectGenerator<?> sourceGenerator, Object... values)
public static ObjectGenerator<T> switcher(ObjectGenerator<?> sourceGenerator, List<Object> values)
```

where ```sourceGenerator``` must be a reference to a switchable ObjectGenerator. These are currently: 
```circular()```, ```random()```, ```weighted()```, ```exactly()```, all ```csvX()```
ObjectGenerators and ```switcher()``` ObjectGenerators themselves. So basically all ObjectGenerators holding a list of values. 
Note that you **cannot** switch from `randomWithin()` and `circularWithin()`. 

`values` must be the list of constant or ObjectGenerator values you want `switcher()` to choose of dependently on ```sourceGenerator```'s value. \
Technically speaking, on each generate ```sourceGenerator``` is evaluated first, then ```switcher()``` retrieves the list index of 
```sourceGenerator```'s current value and after that simply chooses based on this index from your `values` list. 

That also means that your ```values``` list must **always** be equally sized to source's list,
else an Exception will be thrown. 

### Map

```map()``` is the alternative to ```switcher()``` when you want to create relations between your values.
The difference is that ```map()``` assigns values depending on source's concrete value rather
than it's list index. 

The usage is

```java
public static ObjectGenerator<T> map(ObjectGenerator<?> sourceGenerator, Map<?, ?> valueMap)
```

where `sourceGenerator` must be a reference to an ObjectGenerator of **any type** and `valueMap` a Map with several entries 
representing your dependent values. 
Keys in `valueMap` must be constant and of same type as `sourceGenerator` while values
in `valuesMap` can be constant values or ObjectGenerator values of any type. 

On each generate, the concrete value of `sourceGenerator` is evaluated and then `map()` evaluates to the value in `valueMap` 
that has the matching key. 

If Ranger does not find a matching key in ```valueMap```, it picks the value for ```"default"``` key if defined. 
If ```"default"``` key is not defined, an Exception will be thrown.

Example:

```java
ObjectGenerator<String> state_short = circular("AL", "AZ", "DE", "CA", "ID");
ObjectGenerator<String> state_long = map(state_short, Map.of("AL", "Alabama", "AZ", "Arizona", "DE", "Delaware", "default", "NULL"));

ObjectGenerator<Map<String, Object>> state = new ObjectGeneratorBuilder()
        .prop("short", state_short)
        .prop("long", state_long)
        .build();
```

`state` ObjectGenerator will generate

```
{short=AL, long=Alabama}
{short=AZ, long=Arizona}
{short=DE, long=Delaware}
{short=CA, long=NULL}
{short=ID, long=NULL}
{short=AL, long=Alabama}
...
```

The advantage from ```map()``` over ```switcher()``` is that you can use all ObjectGenerators as sources and don't need to specify a key value pair for 
each possible value but instead define a fallback value. 
It's disadvantage is that it's more verbose than ```switch()``` and you must carefully enter your `valueMap`'s keys.


## Transformer

You can change the appearance of ObjectGenerators by using Transformers. `lower()`, `upper()`, 
`string()`, `stringf()`, `time()`, `json()` and `get()`  Transformers are available. Each Transformer
creates a new ObjectGenerator not affecting it's source's Value.

### Case transformer

There might be some use cases where you already have a String ObjectGenerator and want to convert it to lower case or upper case. 
You can simply do that by using the ```lower()``` and ```upper()``` functions:

```java
public static ObjectGenerator<String> lower(ObjectGenerator<String> source)
public static ObjectGenerator<String> upper(ObjectGenerator<String> source)
```

Example:

```java
ObjectGenerator<String> firstname = random("Max", "Moritz", "Julius");
ObjectGenerator<String> lastname = random("Muller", "Meier", "Hertz");
ObjectGenerator<String> email = string("{}.{}@gmail.com", lower(firstname), lower(lastname));
```

`email` ObjectGenerator will generate something like this

```
"max.hertz@gmail.com", "moritz.muller@gmail.com", "julius.muller@gmail.com", ...
```

while

```java
ObjectGenerator<String> username = random("jon823", "richard007", "walter_q0");
ObjectGenerator<String> upper = upper(username);
```

`upper` ObjectGenerator will produce these Strings

```
"WALTER_Q0", "JON823", "RICHARD007", ...
```

### Ascii transformer

Sometimes you need to strip off all non-standard characters like accents from your String. Examples requiring ascii content are folder names, e-mail-addresses and passwords. \
This is what ascii transformer does.

```java
public static ObjectGenerator<String> ascii(ObjectGenerator<String> source)
```

Example:

```java
ObjectGenerator<String> germanAscii = ascii(constant("Zwölf Boxkämpfer jagen Viktor quer über den großen Sylter Deich"));
ObjectGenerator<String> frenchAscii = ascii(constant("Le canapé"));
```

`germanAscii` ObjectGenerator will generate `"Zwoelf Boxkaempfer jagen Viktor quer ueber den grossen Sylter Deich"` while `frenchAscii`
will generate `"Le canape"`.

### String transformer

`string()` transformer creates a formatted string using the specified format string and values:

```java
public static ObjectGenerator<String> string(String format, Object... values)
public static ObjectGenerator<String> string(String format, List<Object> values)
```

Example:

```java
ObjectGenerator<String> name = random("Peter", "Stephen", "Charles");
ObjectGenerator<Integer> age = randomWithin(15, 40);
ObjectGenerator<String> text = string("{} is {} years old.", name, age);
```
`text` ObjectGenerator will possibly generate:

```
"Peter is 18 years old.", "Peter is 34 years old.", "Charles is 27 years old.", ...
```

`{}` acts as placeholder and will be replaced with corresponding value in `values` list.

### Stringf transformer

```stringf()``` creates a formatted string using the specified format string and values. In contrast to ```string()``` function above,
 it uses the more powerful printf syntax:

```java
public static ObjectGenerator<String> stringf(String format, Object... values)
public static ObjectGenerator<String> stringf(String format, List<Object> values)
```

Example:

```java
ObjectGenerator<String> name = random("Peter", "Stephen", "Charles");
ObjectGenerator<Integer> age = randomWithin(15, 40);
ObjectGenerator<Double> height = randomWithin(1.5, 2.0);
ObjectGenerator<String> text = stringf("%s is %d years old and %.2f m tall.", name, age, height);
```
`text` ObjectGenerator will possibly generate:

```
"Peter is 23 years old and 1.67 m tall.", "Stephen is 19 years old and 1.82 m tall.", "Peter is 37 years old and 1.88 m tall.", ...
```

```stringf()``` gives you many more options to format your String. But if you aren't familiar with it's syntax,
```string()``` transformer is easier to understand. Also ```string()``` has a slightly better performance. \
You can lookup the exact usage of ```stringf()``` at https://docs.oracle.com/javase/7/docs/api/java/util/Formatter.html

### Time transformer

`time()` transforms ObjectGenerators of type long, Date, LocalDate and 
LocalDateTime into String representation.

```java
public static ObjectGenerator<String> time(String format, ObjectGenerator<T> generator)
```

First argument is the format string and second argument the time ObjectGenerator. 

This example

```java
ObjectGenerator<String> date = time("yyyy-MM-dd", randomWithin(1483228800000L, 1514764800000L));
```

generates date Strings like
```
"2017-03-25", "2017-08-08", "2017-10-11", ...
```

while this ObjectGenerator generates time stamps, which can be helpful in many cases

```java
ObjectGenerator<String> timestamp = time("yyyy-MM-dd HH:mm:ss.SSS", nowDate());
```

For the format string following characters are allowed:

| character | description | example |
| --------- | ----------- | ------- |
| `G` | Era designator | `AD  ` |
| `y` | Year | `1996; 96  ` |
| `Y` | Week year | `2009; 09  ` |
| `M` | Month in year | `July; Jul; 07  ` |
| `w` | Week in year | `27  ` |
| `W` | Week in month | `2  ` |
| `D` | Day in year | `189  ` |
| `d` | Day in month | `10  ` |
| `F` | Day of week in month | `2  ` |
| `E` | Day name in week | `Tuesday; Tue  ` |
| `u` | Day number of week (1 = Monday, ..., 7 = Sunday) | `1  ` |
| `a` | Am/pm marker | `PM  ` |
| `H` | Hour in day (0-23) | `0  ` |
| `k` | Hour in day (1-24) | `24  ` |
| `K` | Hour in am/pm (0-11) | `0  ` |
| `h` | Hour in am/pm (1-12) | `12` |
| `m` | Minute in hour | `30` |
| `s` | Second in minute | `55` |
| `S` | Millisecond | `978` |
| `z` | Time zone | `Pacific Standard Time; PST; GMT-08:00  ` |
| `Z` | Time zone RFC 822 | `-0800  ` |
| `X` | Time zone ISO 8601 | `-08; -0800; -08:00            ` |

More information at https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html.


## Getter Transformer

`get()` Extracts property value from complex `ObjectGenerator`.

```java
public static ObjectGenerator<T> get(String keyName, Class<T> keyType, ObjectGenerator<?> generator)
```
Example:

```java
ObjectGenerator<Map<String, Object>> address = new ObjectGeneratorBuilder()
    .prop("city", random("New York", "Washington", "San Francisco"))
    .prop("street", random("2nd St", "5th Avenue", "21st St", "Main St"))
    .prop("houseNumber", random(range(1, 55))).build();

ObjectGenerator<String> city = get("city", String.class, address);
```

Possible generated values are:

```
"New York", "Washington", "San Francisco", ...
```


## JSON transformer

`json()` transforms value of complex `ObjectGenerator` into JSON.

```java
public static ObjectGenerator<String> json(ObjectGenerator<?> generator)
public static ObjectGenerator<String> json(ObjectGenerator<?> generator, ObjectMapper objectMapper)
```

Example:

```java
ObjectGenerator<Map<String, Object>> address = new ObjectGeneratorBuilder()
    .prop("city", random("New York", "Washington", "San Francisco"))
    .prop("street", random("2nd St", "5th Avenue", "21st St", "Main St"))
    .prop("houseNumber", randomWithin(1, 55)).build();

ObjectGenerator<Map<String, Object>> user = new ObjectGeneratorBuilder()
    .prop("id", circularWithin(1L, 2_000_000L, 1L))
    .prop("username", string("{}{}", random("aragorn", "johnsnow", "mike", "batman"), randomWithin(1, 100)))
    .prop("firstName", random("Peter", "Rodger", "Michael"))
    .prop("lastName", random("Smith", "Cooper", "Stark", "Grayson", "Atkinson", "Durant"))
    .prop("maried", false)
    .prop("accountBalance", randomWithin(0.0d, 10_000.0d))
    .prop("address", address).build();

ObjectGenerator<String> output = json(user);
```

Possible generated values are:

```
{"id":1,"username":"mike1","firstName":"Michael","lastName":"Cooper","maried":false,"accountBalance":0.0,"address":{"city":"San Francisco","street":"Main St","houseNumber":1}}

{"id":2,"username":"mike99","firstName":"Rodger","lastName":"Smith","maried":false,"accountBalance":9999.99999999999,"address":{"city":"San Francisco","street":"21st St","houseNumber":54}}

{"id":3,"username":"johnsnow35","firstName":"Michael","lastName":"Atkinson","maried":false,"accountBalance":9636.00274910154,"address":{"city":"New York","street":"Main St","houseNumber":37}}
```

## Clone ObjectGenerator

It is possible to clone **every** ObjectGenerator by calling it's `clone()` method: 
```java
public ObjectGenerator<T> clone()
```
This returns a new instance of previous defined ObjectGenerator that generates the same type of objects.

Example:

```java
ObjectGenerator<Map<String, Object>> bookA = new ObjectGeneratorBuilder()
                .prop("name", random("Ulysses", "The Great Gatsby", "Moby Dick", "War and Peace", "Lolita", "Hamlet"))
                .prop("genre", random("Action", "Crime", "Comedy", "Fantasy", "Science-Fiction", "Thriller", "Romance", "Mystery"))
                .build();

ObjectGenerator<Map<String, Object>> borrowedBooks = new ObjectGeneratorBuilder()
                .prop("bookA", bookA)
                .prop("bookB", bookA.clone())
                .build();
```

`borrowedBooks` ObjectGenerator will generate something like this 

```
{bookA={name=Moby Dick, genre=Fantasy}, bookB={name=Lolita, genre=Fantasy}}
{bookA={name=Lolita, genre=Action}, bookB={name=The Great Gatsby, genre=Mystery}}
{bookA={name=The Great Gatsby, genre=Comedy}, bookB={name=The Great Gatsby, genre=Mystery}}
{bookA={name=Hamlet, genre=Action}, bookB={name=Ulysses, genre=Fantasy}}
...
```

Note that `bookA` and `bookB` have both **independent** values. \
But however, of course due to the randomness it is possible that sometimes `bookA` 
and `bookB` obtain equal values.