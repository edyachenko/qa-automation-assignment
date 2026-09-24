package com.flamingo.qa.ui.data;

import com.flamingo.qa.ui.dto.Gender;
import com.flamingo.qa.ui.dto.Hobby;
import com.flamingo.qa.ui.dto.Student;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@UtilityClass
public class Students {

    public final String PICTURE = "uploads/sample-picture.png";

    private final List<String> FIRST_NAMES = List.of("Olena", "Taras", "Maria", "Andrii", "Sofia");
    private final List<String> LAST_NAMES = List.of("Kovalenko", "Shevchenko", "Bondar", "Melnyk", "Tkachenko");
    private final List<String> SUBJECTS = List.of("Maths", "Physics", "Chemistry", "English", "Computer Science");
    private final Map<String, List<String>> CITIES_BY_STATE = Map.of(
            "NCR", List.of("Delhi", "Gurgaon", "Noida"),
            "Uttar Pradesh", List.of("Agra", "Lucknow", "Merrut"),
            "Haryana", List.of("Karnal", "Panipat"),
            "Rajasthan", List.of("Jaipur", "Jaiselmer"));

    public Student valid() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        String firstName = pick(FIRST_NAMES);
        String lastName = pick(LAST_NAMES);
        String state = pick(List.copyOf(CITIES_BY_STATE.keySet()));
        return Student.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email("%s.%s%d@example.com".formatted(firstName, lastName, random.nextInt(1000, 10000)).toLowerCase())
                .gender(pick(List.of(Gender.values())))
                .mobile(String.valueOf(random.nextLong(1_000_000_000L, 10_000_000_000L)))
                .birthDate(LocalDate.of(random.nextInt(1970, 2006), random.nextInt(1, 13), random.nextInt(1, 29)))
                .subjects(List.of(pick(SUBJECTS)))
                .hobbies(List.of(pick(List.of(Hobby.values()))))
                .picture(PICTURE)
                .currentAddress(random.nextInt(1, 200) + " Khreshchatyk Street, Kyiv")
                .state(state)
                .city(pick(CITIES_BY_STATE.get(state)))
                .build();
    }

    private <T> T pick(List<T> values) {
        return values.get(ThreadLocalRandom.current().nextInt(values.size()));
    }
}
