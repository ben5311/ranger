package ranger.example;

import ranger.BuilderMethods;
import ranger.ObjectGenerator;

public class ExampleImportYaml {

    public static void main(String[] args) {
        ObjectGenerator<?> generator = BuilderMethods.importYaml("src/main/java/ranger/example/test-import.yaml");
        generator.generate(15).forEach(System.out::println);
    }
}
