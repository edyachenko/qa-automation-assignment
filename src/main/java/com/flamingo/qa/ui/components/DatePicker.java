package com.flamingo.qa.ui.components;

import com.microsoft.playwright.Locator;

import java.time.LocalDate;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class DatePicker {

    private final Locator input;
    private final Locator calendar;

    public DatePicker(Locator input) {
        this.input = input;
        this.calendar = input.page().locator(".react-datepicker");
    }

    public void pick(LocalDate date) {
        input.click();
        calendar.locator(".react-datepicker__month-select").selectOption(String.valueOf(date.getMonthValue() - 1));
        calendar.locator(".react-datepicker__year-select").selectOption(String.valueOf(date.getYear()));
        calendar.locator(".react-datepicker__day:not(.react-datepicker__day--outside-month)")
                .getByText(String.valueOf(date.getDayOfMonth()), new Locator.GetByTextOptions().setExact(true))
                .click();
        assertThat(calendar).isHidden();
    }
}
