package ranger.example;

import ranger.BuilderMethods;
import ranger.ObjectGenerator;

public class ExampleCsvImport {

    public static void main(String[] args) {
        ObjectGenerator<?> generator = BuilderMethods.importYaml("src/main/java/ranger/example/test-csv.yaml");
        generator.generate(15).forEach(System.out::println);
    }

}
