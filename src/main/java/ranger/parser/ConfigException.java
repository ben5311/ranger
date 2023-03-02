package ranger.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Indicates that an error occurred during creation of ConfigurationParser
 */
public class ConfigException extends RuntimeException {
    static final String EXCEPTION_INDENTATION = "  ";
    final List<String> blocks = new ArrayList<>();

    public ConfigException() {
        this.addBlock("");
    }
    public ConfigException(String message) {
        super(Objects.requireNonNull(message));
        addBlock(message);
    }
    public ConfigException(Throwable cause) {
        super(cause);
    }
    public ConfigException(String message, Throwable cause) {
        super(Objects.requireNonNull(message), cause);
        addBlock(message);
    }

    public void addBlock(String block) {
        this.blocks.add(block);
    }

    public List<String> getBlocks() {
        return blocks;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        String indentation = "";
        for (int i = 0; i < blocks.size(); i++) {
            for (String line : blocks.get(i).split("\r?\n")) {
                sb.append(indentation).append(line);
                if (i < blocks.size()-1) {
                    sb.append(System.lineSeparator());
                }
            }
            indentation += EXCEPTION_INDENTATION;
        }
        return sb.toString();
    }
}