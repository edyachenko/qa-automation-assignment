package com.flamingo.qa.ui.pages.form;

import com.flamingo.qa.ui.dto.Hobby;
import com.flamingo.qa.ui.dto.Student;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class SubmissionModal {

    private static final DateTimeFormatter BIRTH_DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMMM,yyyy", Locale.ENGLISH);

    private final Locator dialog;
    private final Locator title;

    public SubmissionModal(Page page) {
        this.dialog = page.locator(".modal-content");
        this.title = dialog.locator(".modal-title");
    }

    SubmissionModal waitUntilVisible() {
        assertThat(dialog).isVisible();
        return this;
    }

    @Step("Success modal title should be \"{0}\"")
    public SubmissionModal shouldHaveTitle(String expected) {
        assertThat(title).hasText(expected);
        return this;
    }

    @Step("Success modal should show the submitted data of {0}")
    public SubmissionModal shouldShow(Student student) {
        assertThat(value("Student Name")).hasText(student.firstName() + " " + student.lastName());
        assertThat(value("Student Email")).hasText(student.email());
        assertThat(value("Gender")).hasText(student.gender().getLabel());
        assertThat(value("Mobile")).hasText(student.mobile());
        assertThat(value("Date of Birth")).hasText(BIRTH_DATE_FORMAT.format(student.birthDate()));
        assertThat(value("Subjects")).hasText(String.join(", ", student.subjects()));
        assertThat(value("Hobbies")).hasText(student.hobbies().stream().map(Hobby::getLabel).collect(Collectors.joining(", ")));
        assertThat(value("Picture")).hasText(Path.of(student.picture()).getFileName().toString());
        assertThat(value("Address")).hasText(student.currentAddress());
        assertThat(value("State and City")).hasText(student.state() + " " + student.city());
        return this;
    }

    private Locator value(String label) {
        return dialog.locator("xpath=.//tr[td[1][normalize-space()='%s']]/td[2]".formatted(label));
    }
}
