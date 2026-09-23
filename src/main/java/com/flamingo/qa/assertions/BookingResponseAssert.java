package com.flamingo.qa.assertions;

import com.flamingo.qa.dto.request.BookingRequest;
import com.flamingo.qa.dto.response.BookingResponse;
import io.restassured.response.Response;

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

    public BookingResponseAssert shouldHaveFirstname(String expected) {
        assertThat(body(BookingResponse.class).firstname()).as("firstname").isEqualTo(expected);
        return this;
    }
}
