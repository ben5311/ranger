package ranger.example;

import ranger.BuilderMethods;
import ranger.ObjectGenerator;

public class ExampleXeger {

    public static void main(String[] args) {
        ObjectGenerator<?> generator = BuilderMethods.importYaml("src/main/java/ranger/example/test-xeger.yaml");
        generator.generate(20).forEach(System.out::println);
    }
}
