package ranger.core;

import nl.flotsam.xeger.Xeger;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Generates Strings that match specified regex pattern.
 */
public class XegerValue extends Value<String> {

    //Constants that are used to validate the regex
    static final String NESTED_CLASS_PATTERN = "\\[([^\\]\\&]*?)\\[([^\\]]*?)\\](.*?)\\]";                          //not allowed
    static final String NESTED_CLASS_INTERSECTION_PATTERN = "\\[([^\\]]*?)\\&{1,2}\\[([^\\]]*?)\\](.*?)\\]";        //allowed
    static final String NEGATED_PREDEFINED_CLASS_INSIDE_CLASS_PATTERN = "\\[[^\\]]*?(\\\\D|\\\\W|\\\\S).*?\\]";     //not allowed
    static final List<String> BOUNDARY_MATCHERS = Arrays.asList("\\b", "\\B", "\\A", "\\G", "\\Z", "\\z");          //not allowed
    static final List<String> RELUCTANT_QUANTIFIERS = Arrays.asList("??", "*?", "+?", "}?");                        //not allowed
    static final List<String> POSSESSIVE_QUANTIFIERS = Arrays.asList("?+", "*+", "++", "}+");                       //not allowed
    static final List<String> NEED_ESCAPE_CHARACTERS = Arrays.asList("#", "@", "<", ">", "~");                      //allowed

    private final Value<String> regexValue;
    private String regexPattern = null;
    private Xeger xeger;

    /**
     * Constructs XegerValue that generates random Strings that match the given regex pattern
     * @param regexValue Value that returns regex pattern String
     * @throws RegexException if an error occurs during validation or generation of regex pattern
     * @throws ValueException if regexValue is null or String generated from regexValue is null or empty
     */
    public XegerValue(Value<String> regexValue) {
        if (regexValue == null) { throw new ValueException("regex value cannot be null"); }
        this.regexValue = regexValue;
    }

    /*
    Copy constructor
     */
    private XegerValue(XegerValue source) {
        super(source);
        this.regexValue = source.regexValue.getClone();
        this.regexPattern = source.regexPattern;
        this.xeger = source.xeger;
    }

    @Override
    public void eval() {
        String nextRegexPattern = regexValue.get();
        if (nextRegexPattern == null || nextRegexPattern.isEmpty()) { throw new ValueException("regexPattern must not be null nor empty"); }
        if (!nextRegexPattern.equals(regexPattern)) {   //only change Xeger when regexPattern changes
            validateRegExPattern(nextRegexPattern);
            regexPattern = nextRegexPattern;
            try {
                xeger = new Xeger(replaceSpecialCharacters(regexPattern));
            } catch (RuntimeException e) {
                throw new RegexException("Error generating xeger value from pattern '" + regexPattern + "': " + e.getMessage());
            }
        }
        try {
            val = xeger.generate();
        } catch (StackOverflowError e) {
            throw new RegexException(regexPattern, "led to an infinite loop during generation. Please try again with a simpler pattern");
        }

    }

    @Override
    public void reset() {
        regexValue.reset();
        super.reset();
    }

    @Override
    protected XegerValue clone() {
        return new XegerValue(this);
    }

    /**
     * Method that ensures that there is no unsupported regex code inside regexPattern
     */
    private static void validateRegExPattern(String regexPattern) {
        try {
            Pattern.compile(regexPattern);      //validate regex using Java parsing logic
        } catch (PatternSyntaxException e) {
            throw new RegexException(regexPattern, "does not seem like a valid regex pattern: "+e.getMessage());
        }
        Matcher nestedClassMatcher = Pattern.compile(NESTED_CLASS_PATTERN).matcher(regexPattern);
        Matcher nestedNegatedClassMatcher = Pattern.compile(NEGATED_PREDEFINED_CLASS_INSIDE_CLASS_PATTERN).matcher(regexPattern);
        if (nestedClassMatcher.find()) {
            throw new RegexException(regexPattern, "contains bracket char class inside char class: '"+nestedClassMatcher.group(0)+"' (not supported)");
        }
        if (nestedNegatedClassMatcher.find()) {
            throw new RegexException(regexPattern, "contains negated predefined class inside char class: '"+nestedNegatedClassMatcher.group(0)+"' (not supported)");
        }
        for (List<String> unsupportedExpressions : Arrays.asList(BOUNDARY_MATCHERS, RELUCTANT_QUANTIFIERS, POSSESSIVE_QUANTIFIERS)) {
            for (String expression : unsupportedExpressions) {
                if (regexPattern.contains(expression)) {
                    int index = -1;
                    do {
                        index = regexPattern.indexOf(expression, index+1);
                        if (index == 0 || index > 0 && regexPattern.charAt(index-1) != '\\') {
                            throw new RegexException(regexPattern, "contains one of these unsupported matchers or quantifiers: "+unsupportedExpressions);
                        }
                    } while (index != -1);
                }
            }
        }
    }



    /**
     * Method that replaces predefined character classes like "\d" and "\w" with their equivalent expression
     * @return regex pattern without predefined classes like "\d" and "\w"
     */
    private static String replaceSpecialCharacters(String regexPattern) {
        for (String character : NEED_ESCAPE_CHARACTERS) {
            regexPattern = regexPattern.replace(character, '\\'+character);
        }
        StringBuilder sb = new StringBuilder(regexPattern);
        int openBrackets = 0;   //holds track of opened brackets so that we know if we are in a character class
        for (int i = 0; i < sb.length()-1; i++) {
            if (sb.substring(i, i+2).equals("\\\\")) {  //continue with next char
                i += 2;
                continue;
            }
            if (i+2 < sb.length() && sb.substring(i, i+3).equals("\\p{")) {  //could possibly be a predefined posix class
                int indexClosingBracket = sb.indexOf("}", i+2);
                if (indexClosingBracket == -1) { throw new RegexException(regexPattern, "contains unclosed posix class: '"+sb.substring(i, sb.length())+"'"); }
                String className = sb.substring(i+3, indexClosingBracket);
                String replacement;
                switch (className) {
                    case "Lower":
                        replacement = "[a-z]";
                        break;
                    case "Upper":
                        replacement = "[A-Z]";
                        break;
                    case "ASCII":
                        replacement = "[" + '\u0000' + "-" + '\u007f' + "]";
                        break;
                    case "Alpha":
                        replacement = "[a-zA-Z]";
                        break;
                    case "Digit":
                        replacement = "[0-9]";
                        break;
                    case "Alnum":
                        replacement = "[a-zA-Z0-9]";
                        break;
                    case "Punct":
                        replacement = "[!$%'/;_`~\\&\\^\\|\\,\\.\\+\\?\\*\\=\\{\\}\\[\\]\\@\\#\\.\\\"\\(\\)\\<\\>\\-\\:\\\\]";
                        break;
                    case "Graph":
                        replacement = "[a-zA-Z0-9!$%'/;_`~\\&\\^\\|\\,\\.\\+\\?\\*\\=\\{\\}\\[\\]\\@\\#\\.\\\"\\(\\)\\<\\>\\-\\:\\\\]";
                        break;
                    case "Print":
                        replacement = "[a-zA-Z0-9!$%'/;_`~\\&\\^\\|\\,\\.\\+\\?\\*\\=\\{\\}\\[\\]\\@\\#\\.\\\"\\(\\)\\<\\>\\-\\:\\\\"+'\u0020'+"]";
                        break;
                    case "Blank":
                        replacement = "[ \t]";
                        break;
                    case "Cntrl":
                        replacement = "[" + '\u0000' + "-" + '\u001f' + '\u007f' + "]";
                        break;
                    case "XDigit":
                        replacement = "[0-9a-fA-F]";
                        break;
                    case "Space":
                        replacement = "[ \t\n"+'\u000B'+"\f\r]";
                        break;
                    default:
                        throw new RegexException(regexPattern, "contains invalid posix class \\p{"+className+"}");
                }
                if (openBrackets > 0) {  //we are already in a bracket character class, so strip off brackets
                    replacement = replacement.substring(1, replacement.length()-1);
                }
                sb.replace(i, indexClosingBracket+1, replacement);  //replace character class with it's bracket expression
                i += replacement.length()-1;
            }
            else if (sb.charAt(i) == '\\') {     //could possibly be a predefined char class
                char classSpecifier = sb.charAt(i+1);
                String replacement = null;
                switch(classSpecifier) {
                    case 'd':
                        replacement = "[0-9]";
                        break;
                    case 'D':
                        replacement = "[^0-9]";
                        break;
                    case 's':
                        replacement = "[ \t\n"+'\u000B'+"\f\r]";
                        break;
                    case 'S':
                        replacement = "[^ \t\n"+'\u000B'+"\f\r]";
                        break;
                    case 'w':
                        replacement = "[a-zA-Z_0-9]";
                        break;
                    case 'W':
                        replacement = "[^a-zA-Z_0-9]";
                        break;
                }
                if (replacement != null) {
                    if (openBrackets > 0) {  //we are already in a bracket character class and want to insert a character class, so strip off brackets
                        replacement = replacement.substring(1, replacement.length()-1);
                    }
                    sb.replace(i, i+2, replacement);  //replace character class or special charactere with it's bracket expression
                    i += replacement.length()-1;
                } else {
                    i++;
                }
            }
            else if (sb.charAt(i) == '[') {
                openBrackets++;
            }
            else if (sb.charAt(i) == ']') {
                openBrackets--;
            }
        }
        return  sb.toString().replaceAll(NESTED_CLASS_INTERSECTION_PATTERN, "([$1$3]&[$2])");   //convert Java's intersection syntax to Xeger's syntax
    }


    static class RegexException extends RuntimeException {

        private RegexException(String message) {
            super(message);
        }

        private RegexException(String regexPattern, String reason) {
            super("Regex '"+regexPattern+"' "+reason);
        }
    }

}
