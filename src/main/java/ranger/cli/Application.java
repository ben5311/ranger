package ranger.cli;

import picocli.CommandLine;
import ranger.cli.option.InputOptions;
import ranger.cli.option.RangerCLIOptions;

import java.io.FileNotFoundException;

public class Application {

    public static void main(String... args) {
        // try to pre parse YAML config path from args and set default parameters from it
        try {
            InputOptions inputOptions = new InputOptions();
            new CommandLine(inputOptions).parseArgs(args);
            RangerCLIOptions.YamlValueProvider.loadConfigValues(inputOptions.getYamlFile());
        } catch (FileNotFoundException e) {
            System.err.printf("ERROR: %s: %s%n", Constants.ERROR_FILE_NOT_FOUND, e.getMessage());
            System.exit(1);
        } catch (Exception ignored) {
        }
        // parse args again and show error message if parameters are missing or invalid
        CommandLine commandLine = new CommandLine(new RangerCLI())
                .setUsageHelpWidth(90)
                .setUsageHelpLongOptionsMaxWidth(40)
                .setUsageHelpAutoWidth(true);
        int exitCode = commandLine.execute(args);
        System.exit(exitCode);
    }

}
