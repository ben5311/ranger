package ranger.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TypeConverterValueTest {

    @Test
    void testTypeConverterValueConstantSource() {
        Map<String, Value<?>> valueMap = new HashMap<>();
        valueMap.put("name", ConstantValue.of("Otto"));
        valueMap.put("age", ConstantValue.of(45));
        CompositeValue userComposite = new CompositeValue(valueMap);
        TypeConverterValue<User> userGenerator = new TypeConverterValue<>(User.class, userComposite);

        assertThat(userGenerator.get(), is(equalTo(new User("Otto", 45))));
        userGenerator.reset();
        assertThat(userGenerator.get(), is(equalTo(new User("Otto", 45))));
    }

    @Test
    void testTypeConverterValueVariableSource() {
        Map<String, Value<?>> valueMap = new HashMap<>();
        valueMap.put("name", new DiscreteValue<>(Arrays.asList(ConstantValue.of("Johanna"), ConstantValue.of("Tim"), ConstantValue.of("Thomas"), ConstantValue.of("Gabriel"))));
        valueMap.put("age", new RangeValueInt(new Range<>(18, 90)));
        CompositeValue userComposite = new CompositeValue(valueMap);
        TypeConverterValue<User> userGenerator = new TypeConverterValue<>(User.class, userComposite);

        for (int i = 0; i < 10; i++) {
            User nextUser = userGenerator.get();
            assertThat(nextUser.name, is(oneOf("Johanna", "Tim", "Thomas", "Gabriel")));
            assertThat(nextUser.age, is(both(greaterThanOrEqualTo(18)).and(lessThan(90))));
            userGenerator.reset();
        }
    }

    @Test
    void testErrorConstructWithNullArgument() {
        assertThrows(ValueException.class, () -> new TypeConverterValue<>(String.class, null));
        assertThrows(ValueException.class, () -> new TypeConverterValue<>(null, ConstantValue.of("value")));
        assertThrows(ValueException.class, () -> new TypeConverterValue<>(null, null));
        assertThrows(ValueException.class, () -> new TypeConverterValue<>(String.class, null, null));
        assertThrows(ValueException.class, () -> new TypeConverterValue<>(null, ConstantValue.of("value"), null));
        assertThrows(ValueException.class, () -> new TypeConverterValue<>(null, null, new ObjectMapper()));
        assertThrows(ValueException.class, () -> new TypeConverterValue<>(null, null, null));
    }


    private static class User {
        public String name;
        public int age;

        public User() {

        }

        public User(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            User user = (User) o;
            return Objects.equals(name, user.name) &&
                    Objects.equals(age, user.age);
        }
    }

}