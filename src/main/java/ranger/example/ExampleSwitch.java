package ranger.example;

import ranger.BuilderMethods;
import ranger.ObjectGenerator;

public class ExampleSwitch {

    public static void main(String[] args) {
        ObjectGenerator<?> generator = BuilderMethods.importYaml("src/main/java/ranger/example/test-switch.yaml");
        generator.generate(20).forEach(System.out::println);
    }
}
