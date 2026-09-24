package com.flamingo.qa.assertions;

import com.flamingo.qa.dto.request.BookingRequest;
import com.flamingo.qa.dto.response.CreateBookingResponse;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateBookingResponseAssert extends ResponseAssert<CreateBookingResponseAssert> {

    public CreateBookingResponseAssert(Response response) {
        super(response);
    }

    @Step("Should have a booking id")
    public CreateBookingResponseAssert shouldHaveBookingId() {
        assertThat(bookingId()).as("bookingid").isPositive();
        return this;
    }

    @Step("Should have the created booking {0}")
    public CreateBookingResponseAssert shouldHaveBooking(BookingRequest expected) {
        assertThat(body(CreateBookingResponse.class).booking())
                .as("created booking")
                .usingRecursiveComparison()
                .isEqualTo(expected);
        return this;
    }

    public int bookingId() {
        return body(CreateBookingResponse.class).bookingid();
    }
}
