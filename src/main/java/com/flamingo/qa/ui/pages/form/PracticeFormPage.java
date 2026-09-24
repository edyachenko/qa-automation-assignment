package com.flamingo.qa.ui.pages.form;

import com.flamingo.qa.ui.dto.Student;
import com.flamingo.qa.ui.pages.BasePage;

import java.time.LocalDate;

public class PracticeFormPage extends BasePage<PracticeFormPage> {

    public PracticeFormPage fill(Student student) {
        throw new UnsupportedOperationException();
    }

    public PracticeFormPage shouldHaveEnteredData(Student expected) {
        throw new UnsupportedOperationException();
    }

    public PracticeFormPage uploadPicture(String resource) {
        throw new UnsupportedOperationException();
    }

    public PracticeFormPage shouldHaveUploadedFile(String fileName) {
        throw new UnsupportedOperationException();
    }

    public PracticeFormPage pickBirthDate(LocalDate date) {
        throw new UnsupportedOperationException();
    }

    public PracticeFormPage shouldHaveBirthDate(LocalDate expected) {
        throw new UnsupportedOperationException();
    }

    public PracticeFormPage selectState(String state) {
        throw new UnsupportedOperationException();
    }

    public PracticeFormPage selectCity(String city) {
        throw new UnsupportedOperationException();
    }

    public PracticeFormPage shouldHaveStateAndCity(String state, String city) {
        throw new UnsupportedOperationException();
    }

    public SubmissionModal submit() {
        throw new UnsupportedOperationException();
    }
}
