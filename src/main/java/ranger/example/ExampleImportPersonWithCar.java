package ranger.example;

import ranger.BuilderMethods;
import ranger.ObjectGenerator;

import java.time.Duration;
import java.util.Map;

public class ExampleImportPersonWithCar {

    /**
     * Generiert die vorgefertigte Beispiel-Yaml "src/main/resources/predefined/de/person_with_car.yaml"
     */
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        long started = System.nanoTime();
        ObjectGenerator<Map<String, Object>> generator = BuilderMethods.importYaml(":de/person_with_car.yaml");
        for (int i = 0; i <= 20; i++) {
            Map<String, Object> next = generator.next();
            Map<String, String> address = (Map<String, String>) next.get("address");
            Map<String, String> phone = (Map<String, String>) next.get("phone");
            System.out.printf("%-9s %-12s %-12s  %sj  %-12s %-30s %-10s %.2fm   %-8s %-16s %-16s %-36s Adresse=[ %-25s %-5s %s %-25s ]   %s    %s%n", next.get("gender").equals("male")?"männlich":"weiblich", next.get("firstname"), next.get("lastname"), next.get("age"), next.get("birth_date"), next.get("birth_city"), next.get("eyecolor"), (Integer) next.get("height")/100.0f, next.get("blood_group"), phone.get("home"), phone.get("mobile"), next.get("email"), address.get("street"), address.get("house_number"), address.get("zip"), address.get("city"), next.get("id"), next.get("car"));
        }
        System.out.println("__________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________");
        System.out.printf("%-9s %-12s %-12s  %s  %-12s %-30s %-10s %s   %-8s %-16s %-16s %-36s %74s %s %60s%n", "Geschl.", "Vorname", "Nachname", "Al.", "Geburtsdat.", "Geburtsort", "Augenfb.", "Größe", "Blutgr.", "Telefonnr.", "Handynr.", "E-Mail", "", "Personalausweisnr.", "Auto");
        System.out.println();
        System.out.println("Took " + Duration.ofNanos(System.nanoTime()-started));
    }
}
