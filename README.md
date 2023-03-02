# Ranger test data generator

Ranger is a test data generator written in Java that generates test data from simple YAML configuration files. This
document describes how to write these YAML configuration files and how to integrate them in your Java code.

There is also a Ranger [Command line interface](README-CLI.md).

There is also a [Java API](README-JAVA-API.md) available, but it is not actively developed anymore.

> 📝 Ranger is a fork of SmartCat Labs [Ranger](https://github.com/smartcat-labs/ranger) project.


**Table of Contents**
<!--ts-->

- [Configuration](#configuration)
    - [YAML structure](#yaml-structure)
    - [Import YAML in Java](#import-yaml-in-java)
    - [Object mapping](#object-mapping)
- [Value definition](#value-definition)
  - [Value primitives](#value-primitives)
  - [Value references](#value-references)
  - [Value functions](#value-functions)
- [Functions](#functions)

<!--te-->

# Configuration

### YAML structure

Structure of YAML must be following:

```yaml
values:
  firstName: random(["Stephen", "Richard", "Arnold"])
  addr:
    city: random(["New York", "London"])
    street: 2nd St
    houseNumber: random(1..20)
  user:
    name: $firstName
    address: $addr
output: $user
```

The configuration's root object must have two elements below it: 
`values` where all the values are defined, and `output` which points to the return value for constructed `ObjectGenerator`. Any other element below configuration's root will be ignored. \
The configuration's root object must not be the YAML file's root but can also be a sub object:

```yaml
firstLevel:
  configRoot:
    values:
      firstName: random(["Stephen", "Richard", "Arnold"])
      addr:
        city: random(["New York", "London"])
        street: 2nd St
        houseNumber: random(1..20)
      user:
        name: $firstName
        address: $addr
    output: $user
```

### Import YAML in Java

For simple cases, you can go with the helper method available in BuilderMethods class:

```java
public static ObjectGenerator<T> importYaml(String configPath)
public static ObjectGenerator<T> importYaml(String configPath, String yamlRoot)
```

where `configPath` is the file path to the YAML file you want to import and `yamlRoot` (optional) is the JSON path to the root object within YAML file. \
If `yamlRoot` is omitted, Ranger assumes that the root object should be YAML file's root (`$.`). 
Currently, Ranger only parses JSON path in format `$.a.b.c`, so no support for anything else for defining `yamlRoot`.

Note that you have to manually specify the return type `T` of returned `ObjectGenerator` (*unchecked Cast*).  
If your `output` field points to a complex value (object containing sub elements), `ObjectGenerator`'s return type
is `Map<String, Object>`. If your `output` field points to a flat value, `ObjectGenerator`'s return type is 
the type of these flat value (e.g. `String`, `Integer` or `Double`).

Let's import the previous example YAML:

```java
ObjectGenerator<Map<String, Object>> generator = BuilderMethods.importYaml("path/to/config.yaml", "$.firstLevel.configRoot");
```

that is equal to

```java
ConfigurationParser parser = new ConfigurationParser("path/to/config.yaml", "$.firstLevel.configRoot");
ObjectGenerator<Map<String, Object>> generator = parser.build();
```

and will construct `ObjectGenerator` from given YAML file. 

### Object mapping

You have a little more options when importing your YAML config directly with `ConfigurationParser`. 
Especially it is possible to automatically put your configuration's generated objects into instances of your model class. A simple example is following model class
```java
class User {
    public String username;
    public int age;

    @Override
    public String toString() {
        return username+" "+age;
    }
}
```
with this YAML config
```yaml
values:
  user:
    username: random(["max123", "james007", "x_taeke_x"])
    age: random(14..90)
output: $user
```
imported in Java using
```java
ConfigurationParser parser = new ConfigurationParser("path/to/config.yaml");
ObjectGenerator<User> generator = parser.build(User.class);
for (int i = 1; i <= 3; i++) {
    User randomUser = generator.next();
    System.out.println(randomUser);
}
```
that will output something like this
```
x_taeke_x 61
james007 51
james007 76
```

You can find additional information about the object mapping by searching for usage of `ObjectMapper` from `com.fasterxml.jackson`.

# Value definition

Values can be defined as you would normally in a YAML file.

```yaml
values:
  name: Patrick
```

There are three types of Values: [primitives](#value-primitives), [references](#value-references) and [functions](#value-functions).

## Value primitives

Values can be literals of any primitive type (boolean, byte, short, integer, long, float, double, string and Date). Following section depicts type usage.

```yaml
values:
  booleanTrueVal1: true
  booleanTrueVal2: True
  booleanFalseVal1: false
  booleanFalseVal2: False
  byteVal: byte(23)
  shortVal: short(-832)
  implicitIntegerVal: 3242
  explicitIntegerVal: int(3221)
  implicitLongVal: 332848429842932
  explicitLongVal: long(323)
  explicitFloatVal: float(-88.64)
  implicitDoubleVal: 32.23
  explicitDoubleVal: double(-0.11)
  charVal: 'a'
  stringVal1: 'some text'
  stringVal2: "some text"
  explicitDateVal: date("2017-06-21")
```

## Value references

Defined values can be referenced at other places using `'$'` sign, anywhere you can define value you can also use value reference.
Value reference honor local scope and can be referenced with `'.'` dereference operator.

Example:

```yaml
values:
  randomNames: random(["Peter", "Patrick", "Nick"])
  a:
    b:
      c: random(1..10)
  text:
    firstLine: "Global first line"
  user:
    text:
      firstLine: "User first line"
    innerUser:
      firstName: $randomNames
      num: $a.b.c
      userLine: $text.firstLine

output: $user.innerUser
```
In this case `user.innerUser.firstName` would evaluate to a random one of *Peter*, *Patrick* and *Nick*, \
`user.innerUser.num` field would evaluate to a random number between *1* and *10* (```$a.b.c```) \
and `user.innerUser.userLine` field would evaluate to `"User first line"` due to local scope and shading 
(the reference ```$text.firstLine``` equals ```$user.text.firstLine```  in this case as it is hierarchically closer to ```innerUser.userLine```
than global ```text.firstLine```).


## Value functions

Values can be dynamically generated by functions. Example:

```yaml
values:
  age: random(7..77)
output: $age
```

That will output a random number between *7* and *77* on each generation.

All Ranger functions are described in the section below.

# Functions

## Random

`random()` has two meanings depending on the arguments.

### Random with discrete values

`random([<value_1>, ..., <value_n>], <distribution>)`  generates random value from list of possible values. 
The distribution argument is optional and if omitted, `UniformDistribution` will be used.\
Elements of the list can be of any type and it does not need to be same type for 
all the elements, although there are probably rare use cases where different types within the list will be needed. \
In both of the two parameter variations the value arguments must always be in a **bracket list**. 

Example:

```yaml
values:
  name: random(["Mike", "Peter", "Adam", "Mathew"])
  name: random(["Mike", "Peter", "Adam", "Mathew"], uniform())
output: $name
```

Any variation would create `ObjectGenerator` which can generate possible sequence
```
"Peter", "Peter", "Mathew", "Adam", "Mathew", "Peter", "Mike", "Mike", "Adam", ...
```

You can also use discrete random with references or functions as values:
```yaml
values:
  number: random(1..10)
  name: random(["Mike", $number, circular(["first", "second"])])
output: $name
```

which could generate:
```
4, "Mike", 7, "first", "second", "Mike", "Mike", 9, "first", ...
```

### Random with range

`random(<a>..<b>, <useEdgeCases>, <distribution>)`  generates random value within specified range. \
The arguments `useEdgeCases` and `distribution` are optional so there are three parameter variations.
Default value for `useEdgeCases` is `false` and default `distribution` is `UniformDistribution`.

Example:

```yaml
values:
  age: random(1..100)
  age: random(1..100, false)
  age: random(int(1)..int(100), false, uniform())
output: $age
```

Any variation would create `ObjectGenerator` which can generate possible sequence:
```
1, 36, 17, 87, 43, 55, 91, 83, 2, 21, 76
```

The range is inclusive only at beginning: **[a, b)**. So the last number in range is b-1 (with Integer range) and b will never be generated.
So if you want to include b you should higher the right edge by one step. \
If you want to ensure that your result always contains both edge cases (`a` and `b-1`) you can set `useEdgeCases` to `true`. That will cause that the first generated value is always
`a`, the second value `b-1` and after that random numbers within **[a, b)** follow. 

```yaml
values:
  age: random(short(1)..short(100), true)
output: $age
```

This code would generate sequence that always has first two elements 1 and 99 as those are the edge cases. After that, any random value would be picked.

Beneath all numeric data types (int, float, long, double, etc.), you can also use random with range with chars and with Dates.

```yaml
values:
  letter: random('a'..'z')
output: $letter
```

This will generate a random lowercase letter. \
The given range contains all characters between a and b in the **unicode table** with a and b inclusive: **[a, b]**. Verify you char range if the produced chars look different than expected.

```yaml
values:
  date: random(date("2019-01-01")..date("2019-12-31"))
output: $date
```

This will generate a random date within year 2019. The given range contains all dates between a and b with a and b inclusive: **[a, b]**.

## Circular

`circular()` has two meanings depending on the arguments.

### Circular with discrete values

`circular([<value_1>, ..., <value_n>])`  generates values in the order they are specified until the end. Then starts again from beginning. \
The value arguments must be in a **bracket list**.

Example:

```yaml
values:
  serverIpAddress: circular(["10.10.0.1", "10.10.0.2", "10.10.0.3", "10.10.0.4"])
output: $serverIpAddress
```

This would create `ObjectGenerator` which will generate following sequence:
```
"10.10.0.1", "10.10.0.2", "10.10.0.3", "10.10.0.4", "10.10.0.1", "10.10.0.2", "10.10.0.3", "10.10.0.4", "10.10.0.1", ...
```

You can also use discrete circular with references or functions as values:
```yaml
values:
  number: random(1..10)
  value: circular(["Mike", $number, circular(["first", "second"])])
output: $value
```

which will generate these exact sequence of values
```
"Mike", 3, "first", "Mike", 6, "second", "Mike", 1, "first", ...
```
except that the numbers are random.

### Circular with range

`circular(<a>..<b>, <step>)` generates values from the beginning of the range to the end using step as increment. When end is reached, values are generated again from the beginning.
Currently supports all numeric data types (int, float, long, double, etc.), chars and Date.

Example:

```yaml
values:
  temperature: circular(float(12.0)..float(25.0), float(0.2))
  dayOfYear: circular(1..365, 1)
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

An example with chars

```yaml
values:
 letter: circular('a'..'z', 1)
output: $letter
```

would generate:
```
a, b, c, d, e ..., x, y, z, a, b ...
```

Because chars are derived from unicode table, incrementing by 1 means that Ranger adds 1 to the first char's unicode position on every iteration and then 
returns the char on this position. So double check your char range if the result does not look like expected.

An example with Dates

```yaml
values:
 date: circular(date("2019-01-01")..date("2019-12-31"), 1)
output: $date
```

would generate every day of 2019 as Date object starting from 2019-01-01 in natural order. 

## Weighted discrete values

`weighted([(<value_1>, <weight_1>), ..., (<value_n>, <weight_n>)])` generates values with probability based on their weights.
Values can be constants, functions or references and weights must be constant and of any numeric type. 

This example:

```yaml
values:
  names: weighted([("Stephen", 11.5), ("George", 50), ("Charles", 38.5)])
```

would create `ObjectGenerator` which could generate possible sequence:

```
"Stephen", "George", "Charles", "George", "Charles", "George", "George", "Stepen", "Charles", ...
```

where probability for name "George" is 50%, for "Charles" 38.5% and for "Stephen" 11.5%. However, weights do not need to sum up to 100, this example has it just for purpose of calculating the probability easily.


## Exact weighted discrete values

Having `weighted()` is great, at least for some use cases. But there are times where you will need to be precise, 
you cannot go with `weighted()`, especially when working with small numbers (< 1 000 000). \
`exactly()` gives you precision, at the cost of limited number of objects. Syntax is the same as for `weighted()`.

Example:

```yaml
values:
  names: exactly([("Stephen", 11), ("George", 50), ("Charles", 39)])
output: $names
```

Values will be generated by probability specified by weight. Weight in this case needs to be of **long** type. \
If 100 elements are generated in this case, "George" would be generated exactly 50, "Charles" 39 and "Stephen" 11 times.
If generation of more than 100 elements is attempted, Exception will be thrown. 
If less than 100 elements are generated, they will follow weighted distribution. \
In order to provide precision, exact weighted distribution discards particular value from possible generation if value reached its quota. That is the reason that there is a limitation to number of generated values.

## Distributions

You can use Distributions with several functions like `random()` and `csvRandom()`.
Currently only two distributions are supported: [Uniform](#uniform-distribution) and [Normal](#normal-distribution) distribution.

### Uniform distribution

Uniform distribution can be simply used by stating `uniform()`. Usually it is also the default Distribution when Distribution argument is omitted.
With Uniform distribution all values have the same probability.

Example:

```yaml
values:
  age: random([1, 5, 17, 18, 20], uniform())
output: $age
```

### Normal distribution

Normal distribution can be used in two ways:
`normal()` where default values are `mean=0.5`, `standardDeviation=0.125`, `lowerBound=0`, `upperBound=1`
and `normal(<mean>, <standardDeviation>, <lowerBound>, <upperBound>)`. 

`mean` is the mean of your normal distribution, `standardDeviation` the standardDeviation and the bounds
represent the range of values to be generated. 

Example:

```yaml
values:
  age:
    age1: random(byte(1)..byte(100), true, normal())
    age2: random(double(1)..double(100), false, normal(0, 1, -4, 4))
output: $age
```

With `age2` example the **normal distribution** only
generates values in between -4 and 4, while the most values will be around 0. \
But this doesn't mean that `age2` **value** will only generate values between -4 and 4. Instead it scales the mean, 
standard deviation and the range of it's normal distribution to it's own range (`1..100`).
That will lead to `age2` generating values around 50 most of the time (scaled mean) with an approximate standard deviation
of 11 (scaled standard deviation). 

In your own interest you should simplify handling of `normal()` distribution by passing the same range to distribution as to your
`random()` value when possible. Then you can better understand what effect changing mean and standardDeviation has.


## Random content string

`randomContentString(<length>, [<range_1>, ..., <range_n>])` generates random string of specified length and list of Characters. \
`<range_x>` arguments can either be Character ranges like `'a'..'z'` or single Characters like `'a'`. \
The Character list argument is optional and if not specified, 
string will contain only characters from following ranges: `'A'..'Z'`, `'a'..'z'` and `'0'..'9'`. \
`<length>` can be specified as a constant number, but also as reference or function which can evaluate to different number each time. 
Uniform distribution is used to select Characters from Character ranges.

Examples:

```yaml
values:
  randomString1: randomContentString(5)
  randomString2: randomContentString(8, ['A'..'F', '0'..'9'])
  randomString3: randomContentString(random(5..10), ['A'..'Z', '1', '2', '3'])
output: $randomString1
```

`randomString1` will generate strings of length 5 with characters from ranges: `'A'-'Z'`, `'a'-'z'` and `'0'-'9'`.
```
"Ldsfa", "3Jdf0", "AOSyu", "qr4Qe", "sf23c", "sdFfi", "320fS", ...
```

`randomString2` will generate strings of length 8 from specified range of characters.
```
"EF893232", "2E49D0AB", "BE129E15", "938FFC1C", "BB8A43ED", "829D1CA2", ...
```

`randomString3` will generate strings of length from 5 to 10 with characters from range: `'A'-'Z'` and `'1'-'3'`.
```
"SDF2D", "LJ1O3DUF", "DJS3IE1NLS", "KEUXL1NX", "D11AW", "DA3EAN1", ...
```

## Xeger regex generator

Besides ```randomContentString()``` you can generate random Strings in a more advanced way 
with ```xeger(<regex_pattern>)```. Xeger parses your regex and then generates
a random String that matches your pattern. ```<regex_pattern>``` must be a constant
 regex pattern or a reference or function that evaluates to a regex pattern.
 
A simple example is:

```yaml
values:
  german_iban: xeger("DE[0-9]{20}")
output: $german_iban
```

that produces Strings like
```
"DE83726583748374589378", "DE04837264756294738477", "DE88226374004827383492", ...
```

Let's have a look at a more complex example:

```yaml
values:
  email: xeger("[a-z]{6,12}(\.[a-z\d]{2,4})?@[a-z]{4,10}\.(com|de|gb|pl|info)")
output: $email
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
prepended ```\ ``` like ```\&``` for the & sign.

With very complex regex patterns there is a small chance that loading the xeger value fails 
and an Exception will be thrown. Then try to gradually simplify your pattern.
When your pattern always generates unexpected empty Strings, check if you misused
a special character in above list.

Note that with short patterns you should prefer using ```randomContentString()``` as 
it has a better performance and is less error prone.


## Now functions

These functions return current time:
* `now()` returns `long` UTC time in milliseconds
* `nowDate()` returns `Date`
* `nowLocalDate()` returns `LocalDate`
* `nowLocalDateTime()` returns `LocalDateTime`

## Arithmetic functions

The functions `add()`, `subtract()`, `multiply()` and `divide()` perform arithmetic operations on provided Values. Usage is:

```yaml
values:
  sum: add(<type>, <number_1>, <number_2>)
```

The first argument `<type>` is the result type of the calculation and the two numbers are the operands. 
`<number_1>` and `<number_2>` must be either constant numbers or functions generating numbers. \
The input types don't need to match `<type>` because both `<number_1>` and `<number_2>` are converted into `<type>` before calculation. 
Note that this can lead to precision loss (e.g. when converting double `0.5` into integer `0`). \
Possible types are `byte, short, int, long, float, double`. 

Examples:

```
 add("byte", 4, random(1..10))                     #returns byte value from 5 to 13
 add("short", 10, 5)                               #returns short value 15
 add("int", 10, add("int", 3, 4))                  #returns int value 17
 add("long", long(86400000), now())                #returns long current millis one day in future
 add("float", float(3.2), 4.4)                     #returns float value 7.6
 add("double", 3.2, 20)                            #returns double value 23.2
 add("int", 0.5, 0.5)                              #returns int value 0 (precision loss!)
 subtract("byte", 4, 2)                            #returns byte value 2
 subtract("short", 4, 2)                           #returns short value 2
 subtract("int", random(0..100), 10)               #returns int value from -10 to 89
 subtract("long", now(), long(345600000))          #returns long current millis 4 days in past
 subtract("float", 1, 3.2)                         #returns float value -2.2
 subtract("double", 1000.1, 2)                     #returns double value 998.1
 multiply("byte", 4, 2)                            #returns byte value 8
 multiply("short", 1, 10)                          #returns short value 10
 multiply("int", 60, 60)                           #returns int value 3600
 multiply("long", circular([1, 2, 3]), 5)          #returns long values 5, 10, 15, 5, ...
 multiply("float", 44, 0.05)                       #returns float value 2.2
 multiply("double", 4, 2)                          #returns double value 8
 divide("byte", 4, 2)                              #returns byte value 2
 divide("short", 4, 2)                             #returns short value 2
 divide("int", 4, 2)                               #returns int value 2
 divide("long", 4, 2)                              #returns long value 2
 divide("float", 4, 2)                             #returns float value 2
 divide("double", 4, 2)                            #returns double value 2
```


## UUID

`uuid()` Generates UUID strings.

Example:

```yaml
values:
  id: uuid()
output: $id
```

Possible sequence is:
```
"27dbc38f-cadf-4d42-b18a-44c839e8b8f1", "575fb812-bb98-4f76-b31b-bf42e3ac2d62", "a7e229f3-875d-4a6a-9a5d-fb0670c3afdf", ...
```


## Merge

`merge([<reference_1>, ..., <reference_n>])` Creates a merged object out of multiple objects. Arguments must be in bracket list.

```yaml
values:
  accounts:
    userA:
      name: random(["Max", "Jan", "Tom", "Peter"])
      IBAN: random(["DE7294762849278276393", "DE2305837482348382730", "AU9273362273362847322"])
  creditworthiness:
    userA:
      score: random(float(60.0)..float(100.0))
  MergedUser: merge([$accounts.userA, $creditworthiness.userA])
output: $MergedUser
```

This would create `ObjectGenerator` which will generate items similar to this:
```
{name=Peter, IBAN=AU9273362273362847322, score=87.51586}
{name=Tom, IBAN=DE7294762849278276393, score=68.374664}
{name=Jan, IBAN=AU9273362273362847322, score=82.45905}
{Name=Max, IBAN=DE2305837482348382730, score=63.95084}
...
```

It is possible to reference attributes of the merged object and use it in another place (e.g. ```$MergedUser.IBAN```).
Notice that if two or more of the source objects contain an attribute with the same name, the merged object will obtain the attribute of the object placed rightmost in the argument list.


## List

`list([<value_1>, ..., <value_n>])` generates list out of specified values. Arguments must be in bracket list.

This example

```yaml
values:
  names: list(["Emma", circular(["Mike", "Steve", "John"]), "Ned", circular(["Jessica", "Lisa"])])
```

would create `ObjectGenerator` which will generate following sequence:

```
["Emma", "Mike", "Ned", "Jessica"]
["Emma", "Steve", "Ned", "Lisa"]
["Emma", "John", "Ned", "Jessica"]
["Emma", "Mike", "Ned", "Lisa"]
...
```

## Random Length List

Using `list(<min_length>, <max_length>, <generator>, <distribution>)`  generates random length list out of specified `generator` value.
`<min_length>` is inclusive and `max_length` is exclusive.
There is one parameter variation with distribution and one without:

```yaml
values:
  r: random(10..100)
  names: list(3, 6, $r)
  names: list(3, 6, $r, uniform())
output: $names
```

This would generate lists with minimum 3 and maximum 5 members:
```
[16, 36, 44]
[92, 96, 33, 25]
[12, 14, 54]
[78, 79, 35, 88, 96]
...
```

Note that `generator` value used within random list generator (in this case `r`) should not be referenced in any 
other Value within hierarchy since it's values are reset and regenerated multiple times while list is 
constructed. Referencing it in any other place would result in having different values across value hierarchy. \
If you want to reuse an existing generator anyway, you can clone it (see [Clone value](#clone-value) section).

## Empty list

`list()` without any argument generates empty list.

Example:

```yaml
values:
  names: list()
```

## Empty map

`emptyMap()` generates empty map. It is useful when generating JSON with empty object. 

For example:

```yaml
values:
  user:
    username: peter
    email: email_address@domain.com
    address: emptyMap()
  jsonUser: json($user)
```

This will generate:
`{"username:"peter","email":"email_address@domain.com","address":{}}`


## CSV functions

It is also possible to use CSV file as source of data and to combine it with other Ranger functions and values.
The sections below describe the sequential, circular, random and weighted csv functions.

### CSV Sequential

the `csv()` function processes CSV files record-by-record. \
There are four parameter variations. 
These four variations are equal, while the first three are shorthand versions 
with reasonable defaults:

```yaml
values: 
  csv1: csv("my-csv.csv") 
  csv2: csv("my-csv.csv", ',') 
  csv3: csv("my-csv.csv", ',', true) 
  csv4: csv("my-csv.csv", ',', true, "\n", true, null(), '#', true, null())
```


```
arguments: <csvPath> <delimiter> <firstRecAsHeader> <recordSeparator> <trim> <quote> <commentMarker> <ignoreEmptyLines> <nullString>
```

| argument | description | default |
| -------- | ----------- | ------- |
| `<csvPath>` | absolute or relative path to the csv file you want to import (required) | - |
| `<delimiter>` | character (in single quotes) that separates the columns | `','` |
| `<firstRecAsHeader>` | `true` if first record is to be interpreted as header, `false` if first record is to be interpreted as plain record. Must be `true` if you want to access the columns by their names | `true` |
| `<recordSeparator>` | delimiter (in double quotes) that separates the records | `"\n"` |
| `<trim>` | `true` if each value is to be trimmed for leading and trailing whitespaces | `true` |
| `<quote>` | character (in single quotes) that will be stripped from beginning and end of each column if present. If set to `null()`, no characters will be stripped | `null()` |
| `<commentMarker>` | character (in single quotes) to use as a comment marker, everything after it is considered comment | `'#'` |
| `<ignoreEmptyLines>` | `true` if empty lines are to be ignored, otherwise `false` | `true` |
| `<nullString>` | converts string with given value to null. If set to `null()`, no conversion will be done | `null()` |

Ranger will provide the CSV file's data by parsing one CSV Record after another and returning record values as Strings. \
If for example we have a CSV with following values:

```csv
firstname,lastname,zip,city,country
John,Smith,555-1331,New York,US
Peter,Braun,133-1123,Berlin,DE

# Commented line,Should, not be taken,into,account
Jose,Garcia,328-3221,Madrid,ES
```

And following code:

```yaml
values:
  csv: csv("my-csv.csv")
output: string("{} {} - {} {}", $csv.firstname, $csv.lastname, $csv.zip, $csv.city)
```

It would generate following lines in this order:

```
John Smith - New York US
Peter Braun - Berlin DE
Jose Gercia - Madrid ES
```

Of course, it is possible to reference the column values and use it as values for 
attributes or functions. \
If ```<firstRecAsHeader>``` is ```true``` (which is true by default), you can reference
the column values by their header key (like in the example above). Additionally you can reference the column
values by their position: first column is ```$csv.c0```, second ```$csv.c1``` and so on. \
If you set ```<firstRecAsHeader>``` to ```false``` because your CSV file does not have a header, you are limited to 
```c0,c1..cn``` syntax.

In this example our CSV has no header record:

```csv
John,Smith,555-1331,New York,US
Peter,Braun,133-1123,Berlin,DE

# Commented line,Should, not be taken,into,account
Jose,Garcia,328-3221,Madrid,ES
```

Following code will produce the same output as above:

```yaml
values:
  csv: csv("my-csv.csv", ',', false)
output: string("{} {} - {} {}", $csv.c0, $csv.c1, $csv.c3, $csv.c4)
```


Note that you can only generate as much objects as there are records in the CSV file.
If you import a CSV with 20 records and try to generate 21 objects, an Exception will be thrown. This ensures that no record is generated twice.

### CSV Circular

Using the ```csv()``` function above limits the amount of creatable objects to the amount of CSV records.
Alternatively, you can use the ```csvCircular()``` function. It does the same as the ```csv()``` function but when 
Ranger reaches the CSV's last record, it just starts again at the first record.
The syntax is exactly the same:

```yaml
values: 
  csv1: csvCircular("my-csv.csv") 
  csv2: csvCircular("my-csv.csv", ',') 
  csv3: csvCircular("my-csv.csv", ',', true) 
  csv4: csvCircular("my-csv.csv", ',', true, "\n", true, null(), '#', true, null())
```

```
arguments: <csvPath> <delimiter> <firstRecAsHeader> <recordSeparator> <trim> <quote> <commentMarker> <ignoreEmptyLines> <nullString>
```

Please refer to [CSV Sequential](#csv-sequential) section for the meaning of the arguments.

### CSV Random

The ```csvRandom()``` function generates a **random** record out of the CSV file. \
Basically, the usage is equal to ```csv()``` and ```csvCircular()```:

```yaml
values: 
  csv1: csvRandom("my-csv.csv") 
  csv2: csvRandom("my-csv.csv", ',') 
  csv3: csvRandom("my-csv.csv", ',', true) 
  csv4: csvRandom("my-csv.csv", ',', true, "\n", true, null(), '#', true, null())
```

```
arguments: <csvPath> <delimiter> <firstRecAsHeader> <recordSeparator> <trim> <quote> <commentMarker> <ignoreEmptyLines> <nullString>
```

Please refer to ```CSV Sequential``` section for the meaning of the arguments above.

Additionally you can append a distribution argument to any of those four variants that affects generating the records

```yaml
values: 
  csv1: csvRandom("my-csv.csv", uniform()) 
  csv2: csvRandom("my-csv.csv", ',', uniform()) 
  csv3: csvRandom("my-csv.csv", ',', true, normal(0.5, 0.125, 0, 1)) 
  csv4: csvRandom("my-csv.csv", ',', true, "\\n", true, null(), '#', true, null(), normal())
```

```
arguments
csv1: <csvPath> [<distribution>]
csv2: <csvPath> <delimiter> [<distribution>]
csv3: <csvPath> <delimiter> <firstRecAsHeader> [<distribution>]
csv4: <csvPath> <delimiter> <firstRecAsHeader> <recordSeparator> <trim> <quote> <commentMarker> <ignoreEmptyLines> <nullString> [<distribution>]
```

The use of distributions is explained in ```Distributions``` section. \
For example, if you decide to use this normal distribution:

```yaml
values: 
  csv: csvRandom("my-csv.csv", normal(0.2, 0.01, 0, 1)) 
```

Ranger generates records from CSV file's upper area much more frequently than records from CSV file's bottom because the mean is set to ```0.2``` 
(```0.5``` means CSV file's vertical center in this case). Also the spread of generated records is low because of the small standard deviation of ```0.01```. 

The default distribution is the ```uniform()```-distribution that generates every record with the same probability. It is also picked if you
don't supply a distribution argument.

Be aware that ```csvRandom()``` loads the whole CSV file in memory before generating the first record. Thus it can be slower than
```csv()``` and ```csvCircular()``` which read the CSV file record by record. But this only has a noticeable effect when reading CSV files with 
hundred thousands records and more.

CSV Random never stops generating new records and due to the randomness it likely generates records multiple times.


### CSV Weighted

Lastly, there is the ```csvWeighted()``` function to read CSV files and randomly generate records out of it with respect to a predefined weight.
This weight is given as a column in this CSV.

```yaml
values: 
  csv1: csvWeighted("my-csv.csv", "c0") 
  csv2: csvWeighted("my-csv.csv", ',', "c0") 
  csv3: csvWeighted("my-csv.csv", ',', true, "c0") 
  csv4: csvWeighted("my-csv.csv", ',', true, "\\n", true, null(), '#', true, null(), "c0")
```

```
arguments
csv1: <csvPath> <weightField>
csv2: <csvPath> <delimiter> <weightField>
csv3: <csvPath> <delimiter> <firstRecAsHeader> <weightField>
csv4: <csvPath> <delimiter> <firstRecAsHeader> <recordSeparator> <trim> <quote> <commentMarker> <ignoreEmptyLines> <nullString> <weightField>
```

The first arguments equal the ones described in [CSV Sequential](#csv-sequential) section. The only difference is that you must additionally supply 
the column key for the weight values (```<weightField>```) as the last argument of either function variation. 
The key can either be in ```c0,c1...cn``` syntax or it can be a header key (if ```<firstRecAsHeader>``` = ```true```).

Take this CSV file as an example
```csv
city,state,population
Munich,BY,1400000       # 1.4 million
Bonn,NW,310000          # 310 thousand
Bad Honnef,NW,25000     # 25 thousand
```

with this YAML config

```yaml
values: 
  csv: csvRandom("cities.csv", "population") 
output: $csv.city
```

Ranger will generate ```Munich``` the most of the times, ```Bonn``` a few times and ```Bad Honnef``` only with a proportion of about 2%. 

As described in ```CSV Random``` section, ```CSV Weighted``` also loads the whole CSV file in memory at the beginning which could lead to performance
impacts when reading very big files.


## Dependent Values

There are two ways to define Values that relate to other Values: `switch()` function and `map()` function.

### Switch

```switch(<source> => [<value_1>, ..., <value_n>])``` chooses one of your custom value arguments dependently on source's value.
 
 Take an example:

```yaml
values:
  female_firstname: random(["Anna", "Lena", "Maria"])
  male_firstname: random(["Jan", "Louis", "Paul"])
  person:
    gender: random(["female", "male"])
    firstname: switch($gender => [$female_firstname, $male_firstname])
output: $person
``` 

will generate such objects:

```
{gender=female,  firstname=Anna}
{gender=male, firstname=Jan}
{gender=female, firstname=Maria}
{gender=female, firstname=Anna}
{gender=male, firstname=Louis}
...
```

where each person has a firstname matching their gender. 

The ```switch()``` function thereby enables you
to create relations between different random values which can be extremely useful in lots of situations. 

You must supply two arguments to `switch()`:

```
values:
  example: switch($value_reference => ["list", "of", "dependent", "values"])
```

First argument is ```$value_reference``` that must be a reference to a switchable source. These are currently: 
```circular([x,y,...])```, ```random([x,y,...])```, ```weighted()```, ```exactly()```, all ```csvX()```
sources and  ```switch()``` sources themselves. So basically all sources holding a list of values. \
 Note that you **cannot** switch from random and circular with **range**. 

```$value_reference``` is followed by the ```=>``` assignment operator which is followed by a list of values you want the ```switch()``` 
value to  evaluate to. 

Technically speaking, on each generate the source object (```$value_reference```) is evaluated first, then ```switch()``` retrieves the list index of 
source's current value and after that simply chooses based on this index from ```["list", "of", "dependent", "values"]```. \
That also means that your ```["list", "of", "dependent", "values"]```  must **always** be equally sized to source's list,
else an Exception will be thrown. 

The values in your ```["list", "of", "dependent", "values"]``` can be of any type (primitives, functions and references).

### Map

```map(<source> => [<key_1>:<val_1>, ..., <key_n>:<val_n>])``` is the alternative to ```switch()``` when you want to create relations between your values.
The difference is that ```map()``` assigns values depending on source's concrete value rather
than it's list index. 

Example:

```yaml
values:
  state:
    short: circular(["AL", "AZ", "DE", "CA", "ID"])
    long: map($state => ["AL":"Alabama", "AZ":"Arizona", "DE":"Delaware", "default":"NULL"])
output: $state
```

will generate

```
{short=AL, long=Alabama}
{short=AZ, long=Arizona}
{short=DE, long=Delaware}
{short=CA, long=NULL}
{short=ID, long=NULL}
{short=AL, long=Alabama}
...
```

The syntax is similar to ```switch()```'s:

```yaml
values:
  example: map($value_reference => ["list":"of", "key":"value", "pairs":0, "default":"null"])
```

First argument is ```$value_reference``` that must be a reference to a value of **any type**. It
is followed by `=>` assignment operator which is followed by a list of key-value-pairs. 

On each generate the concrete value of ```$value_reference``` is evaluated first, then Ranger looks for the matching key 
in ```["list":"of", "key":"value", "pairs":0, "default":"null"]``` and evaluates ```map()``` to the corresponding value. 

If Ranger does not find a matching key in ```["list":"of", "key":"value", "pairs":0, "default":"null"]```, it picks the 
value for ```"default"``` key if defined. If ```"default"``` key is not defined, an Exception will be thrown.

The keys and values in ```["list":"of", "key":"value", "pairs":0, "default":"null"]``` can also be of any type but however,
keys must always be constant values (like String, char, int, float, ...) while values can also be function values or references.

The advantage from ```map()``` over ```switch()``` is that you can use all values as sources and don't need to specify a key value pair for 
each possible value but instead define a fallback value. 
It's disadvantage is that it's more verbose than ```switch()``` and you must carefully enter your arguments' keys.


## Transformer

You can change the appearance of Values by using Transformers. `lower()`, `upper()`, 
`string()`, `stringf()`, `time()`, `json()` and `get()`  Transformers are available. Each Transformer
creates a new Value not affecting it's source's Value.

### Case transformer

There might be some use cases where you already have a String and want to convert it to lower case or upper case. 
You can simply do that by using the ```lower(<string_source>)``` and ```upper(<string_source>)``` functions.

Example:

```yaml
values:
  firstname: random(["Max", "Moritz", "Julius"])
  lastname: random(["Muller", "Meier", "Hertz"])
  email: string("{}.{}@gmail.com", lower($firstname), lower($lastname))
output: $email
```

will generate something like this

```
"max.hertz@gmail.com", "moritz.muller@gmail.com", "julius.muller@gmail.com", ...
```

while

```yaml
values:
  username: random(["jon823", "richard007", "walter_q0"])
  upper: upper($username)
output: $upper
```

will produce these Strings

```
"WALTER_Q0", "JON823", "RICHARD007", ...
```

### Ascii transformer

Sometimes you need to strip off all non-standard characters like accents from your String. Examples requiring ascii content are folder names, e-mail-addresses and passwords. \
This is what ```ascii(<string_source>)``` function does.

Example:

```yaml
values:
  german: "Zwölf Boxkämpfer jagen Viktor quer über den großen Sylter Deich"
  ascii: ascii($german) 
output: $ascii
```

will generate 

```
"Zwoelf Boxkaempfer jagen Viktor quer ueber den grossen Sylter Deich"
```

while

```yaml
values:
  french: "Le canapé"
  ascii: ascii($french) 
output: $ascii
```

will produce

```
"Le canape"
```

### String transformer

`string(<format_string>, <value_1>, ..., <value_n>))`  creates a formatted string using the specified format string and values.

Example:

```yaml
values:
  name: random(["Peter", "Stephen", "Charles"])
  age: random(15..40)
  text: string("{} is {} years old.", $name, $age)
output: $text
```

Possible generated values are:
```
"Peter is 18 years old.", "Peter is 34 years old.", "Charles is 27 years old.", ...
```

`{}` acts as placeholder and will be replaced with corresponding value in arguments.

The format string can also be another ObjectGenerator that returns String values: 

```yaml
values:
  name: random(["Peter", "Stephen", "Charles"])
  age: random(15..40)
  format_string: random(["{} is {} years old.", "{}' age is {} years.", "{} is {}j"])
  text: string($format_string, $name, $age)
```

### Stringf transformer

```stringf(<format_string>, <value_1>, ..., <value_n>)``` creates a formatted string using the specified format string and values. In contrast to ```string()``` function above,
 it uses the more powerful printf syntax.
 
 Example:

```yaml
values:
  name: random(["Peter", "Stephen", "Charles"])
  age: random(15..40)
  height: random(1.5..2.0)
  text: string("%s is %d years old and %.2f m tall.", $name, $age, $height)
output: $text
```

Possible generated values are:

```
"Peter is 23 years old and 1.67 m tall.", "Stephen is 19 years old and 1.82 m tall.", "Peter is 37 years old and 1.88 m tall.", ...
```

```stringf()``` gives you many more options to format your String. But if you aren't familiar with it's syntax,
```string()``` transformer is easier to understand. Also ```string()``` has a slightly better performance. \
You can lookup the exact usage of ```stringf()``` at https://docs.oracle.com/javase/7/docs/api/java/util/Formatter.html

### Time transformer

`time(<format_string>, <time>)` transforms time objects of type long, Date, LocalDate and 
LocalDateTime into String representation.

First argument is the format string and second argument the time object. 

Example:

```yaml
values:
  date: time("yyyy-MM-dd", random(1483228800000..1514764800000))
output: $date
```

Possible generated values are:
```
"2017-03-25", "2017-08-08", "2017-10-11", ...
```

This configuration will generate string time stamps, which can be helpful in many cases:

```yaml
values:
  timestamp: time("yyyy-MM-dd HH:mm:ss.SSS", nowDate())
output: $timestamp
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


### Getter Transformer

`get(<key_name>, <complex_value>)` extracts property value from complex Value, useful for data correlation.

Example:

```yaml
values:
  lightLoad:
    type: LIGHT
    value: random(1..10)
  mediumLoad:
    type: MEDIUM
    value: random(11..100)
  heavyLoad:
    type: HEAVY
    value: random(101..1000)
  randomLoad: random([$lightLoad, $mediumLoad, $heavyLoad])
  load:
    additionalField: "Some value"
    additionalField2: true
    type: get("type", $randomLoad)
    value: get("value", $randomLoad)
output: $load
```

Possible generated values are maps with values, for brevity, presented here as json:

```
{ "additionalFields": "Some value", "additionalFields2": true, type: "LIGHT", value: 4 },
{ "additionalFields": "Some value", "additionalFields2": true, type: "HEAVY", value: 120 },
{ "additionalFields": "Some value", "additionalFields2": true, type: "MEDIUM", value: 94 },
{ "additionalFields": "Some value", "additionalFields2": true, type: "HEAVY", value: 823 },
...
```


### JSON transformer

`json(<value>)` transforms complex Values into JSON. 

Example:

```yaml
values:
  user:
    id: circular(1..2000000, 1)
    username: string("{}{}", random(["aragorn", "johnsnow", "mike", "batman"]), random(1..100))
    firstName: random(["Peter", "Rodger", "Michael"])
    lastName: random(["Smith", "Cooper", "Stark", "Grayson", "Atkinson", "Durant"])
    maried: false
    accountBalance: random(0.0..10000.0)
    address:
      city: random(["New York", "Washington", "San Francisco"])
      street: random(["2nd St", "5th Avenue", "21st St", "Main St"])
      houseNumber: random(1..55)
output: json($user)
```

Possible generated values are

```
{"id":1,"username":"mike1","firstName":"Michael","lastName":"Cooper","maried":false,"accountBalance":0.0,"address":{"city":"San Francisco","street":"Main St","houseNumber":1}}

{"id":2,"username":"mike99","firstName":"Rodger","lastName":"Smith","maried":false,"accountBalance":9999.99999999999,"address":{"city":"San Francisco","street":"21st St","houseNumber":54}}

{"id":3,"username":"johnsnow35","firstName":"Michael","lastName":"Atkinson","maried":false,"accountBalance":9636.00274910154,"address":{"city":"New York","street":"Main St","houseNumber":37}}
```



## Import YAML config

You can import another config in your YAML file by using the 
```import(<yaml_path>, <yaml_root>)``` function.

```<yaml_path>``` is the relative or absolute file path to the YAML you 
want to import. Relative paths are interpreted as paths relative 
to current yaml configuration's path. \
```<yaml_root>``` is the JSON path to the 
object containing the `values` and `output` fields inside the config file.   
```<yaml_root>``` is optional and if not provided, Ranger assumes 
that ```<yaml_root>``` should be the YAML file's root (```"$."```). 

For example, let's import this YAML config:

```yaml
#user.yaml
values:
  User:
    name: random(["Max", "Moritz", "Paul"])
    age: random(18..60)
output: $User
```

in another YAML config

```yaml
values:
  ImportedUser: import("user.yaml")
  userString: string("{}, {}", $ImportedUser.name, $ImportedUser.age)
output: $userString
```

which will generate something like this

```
"Max, 37"
"Paul, 20"
"Paul, 64"
"Moritz, 49"
...
```

Consider that the name of the imported Value (`ImportedUser`) must not match the name of original value defined in orignal YAML config (`User`).\
Also note that you can reference all sub elements originally defined in the source YAML config (e.g. ```$ImportedUser.name```).

#### Imported YAMLs are singletons

Every imported YAML is a singleton. That means that during import the YAML file is parsed to one Value instance and if you import the same YAML
file another time across Value hierarchy they all get the same Value instance. \
Usually you don't have to care about this behaviour because it doesn't restrict you in any way. 

But let's have a look at an example where this behaviour is advantageous. 

We define a simple YAML that generates names:

```yaml
#names.yaml
values:
  names:
    firstname: random(["Laura", "Emilia", "Lina", "Anna", "Ida", "Elias", "Emil", "Linus", "Theo", "Anton"])
    lastname: random(["Muller", "Schmitz", "Wagner", "Schneider", "Fischer", "Weber"])
output: $names
```

Then we create a YAML that generates a simple e-mail out of that name:

```yaml
#email.yaml
values:
  names: import("names.yaml")
  email: string("{}.{}@gmail.com", $names.firstname, $names.lastname)
output: lower($email)
```

Nothing very special. But maybe after some time we need a YAML that generates User objects:

```yaml
#user.yaml
values:
  names: import("names.yaml")
  User:
    firstname: $names.firstname
    lastname: $names.lastname
    age: random(18..60)
    email: import("email.yaml")
    
output: $User
```

Notice that we reused the `email.yaml` config. \
This `user.yaml` generates Objects like these:

```
{firstname=Theo, lastname=Schneider, age=26, email=theo.schneider@gmail.com}
{firstname=Ida, lastname=Fischer, age=36, email=ida.fischer@gmail.com}
{firstname=Anton, lastname=Weber, age=19, email=anton.weber@gmail.com}
{firstname=Laura, lastname=Schneider, age=40, email=laura.schneider@gmail.com}
{firstname=Linus, lastname=Schmitz, age=21, email=linus.schmitz@gmail.com}
...
```

Note that every e-mail matches user's name. This is possible because `email.yaml` and `user.yaml` obtain the same instance
of `names.yaml`.

Of course, the above one is a simple example and usually you would integrate `email.yaml`'s content into `user.yaml`.
But this behaviour makes it possible to define complex objects in their own YAML file and later on import and merge the needed objects
into one without loosing value relation.

However, if you get into a situation where you explicitly need a different instance of imported yaml, you can simply clone it (see [Clone value](#clone-value) section).

#### Predefined YAML Configurations

There are some predefined yamls bundled with Ranger ready for import:

```
:
├── country.yaml
├── misc.yaml
├── de
│   ├── address.yaml
│   ├── bank.yaml
│   ├── car.yaml
│   ├── name.yaml
│   └── person.yaml
└── us
    ├── address.yaml
    ├── car.yaml
    └── name.yaml
```

You can import them in Ranger by using their path in the list above with a preceding ```:```. 

Example:

```yaml
values:
  name: import(":de/name.yaml")
```

## Clone value

It is possible to clone **every** Value by referencing it with two `$$` signs. \
 A cloned value is a new instance of previous defined Value
that generates the same type of objects.

Example:

```yaml
values:
  borrowedMedia:
    bookA:
      name: random(["Ulysses", "The Great Gatsby", "Moby Dick", "War and Peace", "Lolita", "Hamlet"])
      genre: random(["Action", "Crime", "Comedy", "Fantasy", "Science-Fiction", "Thriller", "Romance", "Mystery"])
    bookB: $$bookA
output: $borrowedMedia
```

which generates something like this 

```
{bookA={name=Moby Dick, genre=Fantasy}, bookB={name=Lolita, genre=Fantasy}}
{bookA={name=Lolita, genre=Action}, bookB={name=The Great Gatsby, genre=Mystery}}
{bookA={name=The Great Gatsby, genre=Comedy}, bookB={name=The Great Gatsby, genre=Mystery}}
{bookA={name=Hamlet, genre=Action}, bookB={name=Ulysses, genre=Fantasy}}
...
```

Consider that `bookA` and `bookB` have both **independent** values. \
But however, of course due to the randomness it is possible that sometimes `bookA` 
and `bookB` obtain equal values. 

Note that you can reference all sub elements of the cloned Value (e.g. `$bookB.name`).
