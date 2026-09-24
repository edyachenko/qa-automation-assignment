package com.flamingo.qa.assertions;

import com.flamingo.qa.dto.response.BookingIdResponse;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class BookingIdsResponseAssert extends ResponseAssert<BookingIdsResponseAssert> {

    public BookingIdsResponseAssert(Response response) {
        super(response);
    }

    @Step("Should contain only bookings {0}")
    public BookingIdsResponseAssert shouldContainOnly(Integer... ids) {
        assertThat(body(BookingIdResponse[].class))
                .as("booking ids")
                .extracting(BookingIdResponse::bookingid)
                .containsExactlyInAnyOrder(ids);
        return this;
    }

    @Step("Should contain booking {0}")
    public BookingIdsResponseAssert shouldContain(Integer id) {
        assertThat(body(BookingIdResponse[].class))
                .as("booking ids")
                .extracting(BookingIdResponse::bookingid)
                .contains(id);
        return this;
    }

    @Step("Should be empty")
    public BookingIdsResponseAssert shouldBeEmpty() {
        assertThat(body(BookingIdResponse[].class)).as("booking ids").isEmpty();
        return this;
    }
}
