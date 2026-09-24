package com.flamingo.qa.ui.pages.form;

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
    private final Locator birthDate = page.locator("#dateOfBirthInput");
    private final Locator calendar = page.locator(".react-datepicker");
    private final Locator calendarMonth = calendar.locator(".react-datepicker__month-select");
    private final Locator calendarYear = calendar.locator(".react-datepicker__year-select");
    private final Locator calendarDays = calendar.locator(".react-datepicker__day:not(.react-datepicker__day--outside-month)");
    private final Locator subjects = page.locator("#subjectsContainer");
    private final Locator hobbies = page.locator("#hobbiesWrapper");
    private final Locator picture = page.locator("#uploadPicture");
    private final Locator currentAddress = page.locator("#currentAddress");
    private final Locator state = page.locator("#state");
    private final Locator city = page.locator("#city");
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
        student.subjects().forEach(this::addSubject);
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
        expected.subjects().forEach(subject -> assertThat(subjects).containsText(subject));
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
        birthDate.click();
        calendarMonth.selectOption(String.valueOf(date.getMonthValue() - 1));
        calendarYear.selectOption(String.valueOf(date.getYear()));
        calendarDays.getByText(String.valueOf(date.getDayOfMonth()), new Locator.GetByTextOptions().setExact(true)).click();
        assertThat(calendar).isHidden();
        return this;
    }

    @Step("Date of birth field should show {0}")
    public PracticeFormPage shouldHaveBirthDate(LocalDate expected) {
        assertThat(birthDate).hasValue(BIRTH_DATE_FORMAT.format(expected));
        return this;
    }

    @Step("Select state \"{0}\"")
    public PracticeFormPage selectState(String value) {
        chooseOption(state, value);
        return this;
    }

    @Step("Select city \"{0}\"")
    public PracticeFormPage selectCity(String value) {
        chooseOption(city, value);
        return this;
    }

    @Step("State should be \"{0}\" and city \"{1}\"")
    public PracticeFormPage shouldHaveStateAndCity(String expectedState, String expectedCity) {
        assertThat(selectedValue(state)).hasText(expectedState);
        assertThat(selectedValue(city)).hasText(expectedCity);
        return this;
    }

    @Step("Submit the form")
    public SubmissionModal submit() {
        submitButton.click();
        return new SubmissionModal(page).waitUntilVisible();
    }

    private void addSubject(String subject) {
        chooseOption(subjects, subject);
    }

    private void chooseOption(Locator select, String option) {
        select.locator("input").pressSequentially(option);
        select.getByRole(AriaRole.OPTION, new Locator.GetByRoleOptions().setName(option).setExact(true)).click();
    }

    private Locator selectedValue(Locator select) {
        return select.locator("[class*='singleValue']");
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
