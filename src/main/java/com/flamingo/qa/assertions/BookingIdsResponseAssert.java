package com.flamingo.qa.assertions;

import com.flamingo.qa.dto.response.BookingIdResponse;
import io.restassured.response.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class BookingIdsResponseAssert extends ResponseAssert<BookingIdsResponseAssert> {

    public BookingIdsResponseAssert(Response response) {
        super(response);
    }

    public BookingIdsResponseAssert shouldContainOnly(Integer... ids) {
        assertThat(body(BookingIdResponse[].class))
                .as("booking ids")
                .extracting(BookingIdResponse::bookingid)
                .containsExactlyInAnyOrder(ids);
        return this;
    }

    public BookingIdsResponseAssert shouldContain(Integer id) {
        assertThat(body(BookingIdResponse[].class))
                .as("booking ids")
                .extracting(BookingIdResponse::bookingid)
                .contains(id);
        return this;
    }

    public BookingIdsResponseAssert shouldBeEmpty() {
        assertThat(body(BookingIdResponse[].class)).as("booking ids").isEmpty();
        return this;
    }
}
