package ranger.example;

import ranger.BuilderMethods;
import ranger.ObjectGenerator;

public class ExampleStringf {

    public static void main(String[] args) {
        ObjectGenerator<?> generator = BuilderMethods.importYaml("src/main/java/ranger/example/test-stringf.yaml");
        generator.generate(20).forEach(System.out::println);
    }
}
