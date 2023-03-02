package ranger.example;

import ranger.BuilderMethods;
import ranger.ObjectGenerator;

public class ExampleAsciiTransformer {

    public static void main(String[] args) {
        ObjectGenerator<?> generator =
                BuilderMethods.importYaml("src/main/java/ranger/example/test-asciitransformer.yaml");
        generator.generate(20).forEach(System.out::println);
    }
}
