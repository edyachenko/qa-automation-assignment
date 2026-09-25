package com.flamingo.qa.ui.pages.form;

import com.flamingo.qa.ui.components.DatePicker;
import com.flamingo.qa.ui.components.ReactSelect;
import com.flamingo.qa.ui.dto.Gender;
import com.flamingo.qa.ui.dto.Hobby;
import com.flamingo.qa.ui.dto.Student;
import com.flamingo.qa.ui.pages.BasePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;
import lombok.SneakyThrows;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class PracticeFormPage extends BasePage<PracticeFormPage> {

    private static final DateTimeFormatter BIRTH_DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

    private final Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Practice Form"));
    private final Locator firstName = page.locator("#firstName");
    private final Locator lastName = page.locator("#lastName");
    private final Locator email = page.locator("#userEmail");
    private final Locator genders = page.locator("#genterWrapper");
    private final Locator mobile = page.locator("#userNumber");
    private final Locator birthDateInput = page.locator("#dateOfBirthInput");
    private final DatePicker birthDate = new DatePicker(birthDateInput);
    private final Locator subjectsField = page.locator("#subjectsContainer");
    private final ReactSelect subjects = new ReactSelect(subjectsField);
    private final Locator hobbies = page.locator("#hobbiesWrapper");
    private final Locator picture = page.locator("#uploadPicture");
    private final Locator currentAddress = page.locator("#currentAddress");
    private final ReactSelect state = new ReactSelect(page.locator("#state"));
    private final ReactSelect city = new ReactSelect(page.locator("#city"));
    private final Locator submitButton = page.locator("#submit");

    public PracticeFormPage(Page page) {
        super(page, "/automation-practice-form");
    }

    @Override
    protected Locator readyMarker() {
        return heading;
    }

    @Step("Fill the registration form with {0}")
    public PracticeFormPage fill(Student student) {
        firstName.fill(student.firstName());
        lastName.fill(student.lastName());
        email.fill(student.email());
        genderOption(student.gender()).check();
        mobile.fill(student.mobile());
        pickBirthDate(student.birthDate());
        student.subjects().forEach(subjects::choose);
        student.hobbies().forEach(hobby -> hobbyOption(hobby).check());
        uploadPicture(student.picture());
        currentAddress.fill(student.currentAddress());
        selectState(student.state());
        selectCity(student.city());
        return this;
    }

    @Step("Form should keep the entered data of {0}")
    public PracticeFormPage shouldHaveEnteredData(Student expected) {
        assertThat(firstName).hasValue(expected.firstName());
        assertThat(lastName).hasValue(expected.lastName());
        assertThat(email).hasValue(expected.email());
        assertThat(genderOption(expected.gender())).isChecked();
        assertThat(mobile).hasValue(expected.mobile());
        expected.subjects().forEach(subject -> assertThat(subjectsField).containsText(subject));
        expected.hobbies().forEach(hobby -> assertThat(hobbyOption(hobby)).isChecked());
        assertThat(currentAddress).hasValue(expected.currentAddress());
        shouldHaveBirthDate(expected.birthDate());
        shouldHaveUploadedFile(Path.of(expected.picture()).getFileName().toString());
        return shouldHaveStateAndCity(expected.state(), expected.city());
    }

    @Step("Upload picture {0}")
    public PracticeFormPage uploadPicture(String resource) {
        picture.setInputFiles(classpathFile(resource));
        return this;
    }

    @Step("Picture field should hold the uploaded file \"{0}\"")
    public PracticeFormPage shouldHaveUploadedFile(String fileName) {
        assertThat(picture).hasValue(Pattern.compile("[\\\\/]" + fileName.replace(".", "\\.") + "$"));
        return this;
    }

    @Step("Pick birth date {0} in the date picker")
    public PracticeFormPage pickBirthDate(LocalDate date) {
        birthDate.pick(date);
        return this;
    }

    @Step("Date of birth field should show {0}")
    public PracticeFormPage shouldHaveBirthDate(LocalDate expected) {
        assertThat(birthDateInput).hasValue(BIRTH_DATE_FORMAT.format(expected));
        return this;
    }

    @Step("Select state \"{0}\"")
    public PracticeFormPage selectState(String value) {
        state.choose(value);
        return this;
    }

    @Step("Select city \"{0}\"")
    public PracticeFormPage selectCity(String value) {
        city.choose(value);
        return this;
    }

    @Step("State should be \"{0}\" and city \"{1}\"")
    public PracticeFormPage shouldHaveStateAndCity(String expectedState, String expectedCity) {
        assertThat(state.selectedValue()).hasText(expectedState);
        assertThat(city.selectedValue()).hasText(expectedCity);
        return this;
    }

    @Step("Submit the form")
    public SubmissionModal submit() {
        submitButton.click();
        return new SubmissionModal(page).waitUntilVisible();
    }

    private Locator genderOption(Gender gender) {
        return genders.getByLabel(gender.getLabel(), new Locator.GetByLabelOptions().setExact(true));
    }

    private Locator hobbyOption(Hobby hobby) {
        return hobbies.getByLabel(hobby.getLabel(), new Locator.GetByLabelOptions().setExact(true));
    }

    @SneakyThrows
    private Path classpathFile(String resource) {
        return Path.of(PracticeFormPage.class.getClassLoader().getResource(resource).toURI());
    }
}
