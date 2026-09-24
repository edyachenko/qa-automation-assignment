package com.flamingo.qa.tests.ui;

import com.flamingo.qa.ui.data.Students;
import com.flamingo.qa.ui.dto.Student;
import com.flamingo.qa.ui.pages.form.PracticeFormPage;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;

@Feature("Student registration form")
@DisplayName("Student registration form")
class RegistrationFormTests extends BaseUiTest {

    PracticeFormPage practiceForm;

    @Test
    @DisplayName("filled registration form keeps all entered student data")
    void filledFormKeepsEnteredStudentData() {
        Student student = Students.valid();

        practiceForm.open()
                .fill(student)
                .shouldHaveEnteredData(student);
    }

    @Test
    @DisplayName("uploaded file is attached to the form")
    void uploadedFileIsAttachedToForm() {
        practiceForm.open()
                .uploadPicture(Students.PICTURE)
                .shouldHaveUploadedFile("sample-picture.png");
    }

    @Test
    @DisplayName("date picker puts the chosen date into the date of birth field")
    void datePickerSetsChosenBirthDate() {
        LocalDate birthDate = LocalDate.of(1990, Month.MARCH, 15);

        practiceForm.open()
                .pickBirthDate(birthDate)
                .shouldHaveBirthDate(birthDate);
    }

    @Test
    @DisplayName("state and city dropdowns keep the chosen values")
    void stateAndCityDropdownsKeepChosenValues() {
        practiceForm.open()
                .selectState("NCR")
                .selectCity("Delhi")
                .shouldHaveStateAndCity("NCR", "Delhi");
    }

    @Test
    @DisplayName("submitted form shows the success modal with all student data")
    void submittedFormShowsSuccessModalWithStudentData() {
        Student student = Students.valid();

        practiceForm.open()
                .fill(student)
                .submit()
                .shouldHaveTitle("Thanks for submitting the form")
                .shouldShow(student);
    }
}
