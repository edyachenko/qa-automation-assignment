package com.flamingo.qa.assertions;

import com.flamingo.qa.dto.BookingDates;
import com.flamingo.qa.dto.request.BookingRequest;
import com.flamingo.qa.dto.response.BookingResponse;
import io.restassured.response.Response;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class BookingResponseAssert extends ResponseAssert<BookingResponseAssert> {

    public BookingResponseAssert(Response response) {
        super(response);
    }

    public BookingResponseAssert shouldHaveBooking(BookingRequest expected) {
        assertThat(body(BookingResponse.class))
                .as("booking")
                .usingRecursiveComparison()
                .isEqualTo(expected);
        return this;
    }

    public BookingResponseAssert shouldHaveBookingIgnoring(BookingRequest expected, String... ignoredFields) {
        assertThat(body(BookingResponse.class))
                .as("booking")
                .usingRecursiveComparison()
                .ignoringFields(ignoredFields)
                .isEqualTo(expected);
        return this;
    }

    public BookingResponseAssert shouldHaveDates(BookingDates expected) {
        assertThat(body(BookingResponse.class).bookingdates()).as("booking dates").isEqualTo(expected);
        return this;
    }

    public BookingResponseAssert shouldHaveCheckoutAfterCheckin() {
        BookingDates dates = body(BookingResponse.class).bookingdates();
        assertThat(LocalDate.parse(dates.checkout()))
                .as("checkout %s must be after checkin %s", dates.checkout(), dates.checkin())
                .isAfter(LocalDate.parse(dates.checkin()));
        return this;
    }

    public BookingResponseAssert shouldHaveFirstname(String expected) {
        assertThat(body(BookingResponse.class).firstname()).as("firstname").isEqualTo(expected);
        return this;
    }
}
