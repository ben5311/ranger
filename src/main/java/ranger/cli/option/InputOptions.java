package ranger.cli.option;

import picocli.CommandLine.Parameters;
import picocli.CommandLine.Unmatched;

import java.util.List;

public class InputOptions {

    @Parameters()
    public String yamlFile;

    @Unmatched
    List<String> unmatched;

    public String getYamlFile() {
        return yamlFile;
    }
}
