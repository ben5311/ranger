package ranger.example;

import ranger.BuilderMethods;
import ranger.ObjectGenerator;

public class ExampleDateRange {

    public static void main(String[] args) {
        ObjectGenerator<?> generator = BuilderMethods.importYaml("src/main/java/ranger/example/test-date_range.yaml");
        generator.generate(28).forEach(System.out::println);
    }
}
