package com.flamingo.qa.api.assertions;

import com.flamingo.qa.common.ResponseAssert;
import com.flamingo.qa.api.dto.BookingDates;
import com.flamingo.qa.api.dto.request.BookingRequest;
import com.flamingo.qa.api.dto.response.BookingResponse;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.assertj.core.api.SoftAssertions;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class BookingResponseAssert extends ResponseAssert<BookingResponseAssert> {

    public BookingResponseAssert(Response response) {
        super(response);
    }

    @Step("Should have the expected booking {0}")
    public BookingResponseAssert shouldHaveBooking(BookingRequest expected) {
        assertThat(body(BookingResponse.class))
                .as("booking")
                .usingRecursiveComparison()
                .isEqualTo(expected);
        return this;
    }

    @Step("Should have the expected booking {0} ignoring {1}")
    public BookingResponseAssert shouldHaveBookingIgnoring(BookingRequest expected, String... ignoredFields) {
        assertThat(body(BookingResponse.class))
                .as("booking")
                .usingRecursiveComparison()
                .ignoringFields(ignoredFields)
                .isEqualTo(expected);
        return this;
    }

    @Step("Should have dates {0}")
    public BookingResponseAssert shouldHaveDates(BookingDates expected) {
        assertThat(body(BookingResponse.class).bookingdates()).as("booking dates").isEqualTo(expected);
        return this;
    }

    @Step("Should have every field of {0} and check-out after check-in")
    public BookingResponseAssert shouldHaveEveryField(BookingRequest expected) {
        BookingResponse actual = body(BookingResponse.class);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(actual.firstname()).as("firstname").isEqualTo(expected.firstname());
            softly.assertThat(actual.lastname()).as("lastname").isEqualTo(expected.lastname());
            softly.assertThat(actual.totalprice()).as("totalprice").isEqualTo(expected.totalprice()).isPositive();
            softly.assertThat(actual.depositpaid()).as("depositpaid").isEqualTo(expected.depositpaid());
            softly.assertThat(actual.bookingdates()).as("bookingdates").isEqualTo(expected.bookingdates());
            softly.assertThat(actual.additionalneeds()).as("additionalneeds").isEqualTo(expected.additionalneeds());
            softly.assertThat(LocalDate.parse(actual.bookingdates().checkout()))
                    .as("checkout %s must be after checkin %s", actual.bookingdates().checkout(), actual.bookingdates().checkin())
                    .isAfter(LocalDate.parse(actual.bookingdates().checkin()));
        });
        return this;
    }
}
