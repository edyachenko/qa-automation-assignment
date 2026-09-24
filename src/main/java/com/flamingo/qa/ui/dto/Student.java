package com.flamingo.qa.ui.dto;

import lombok.Builder;
import lombok.With;

import java.time.LocalDate;
import java.util.List;

@Builder(toBuilder = true)
@With
public record Student(
        String firstName,
        String lastName,
        String email,
        Gender gender,
        String mobile,
        LocalDate birthDate,
        List<String> subjects,
        List<Hobby> hobbies,
        String picture,
        String currentAddress,
        String state,
        String city) {
}
