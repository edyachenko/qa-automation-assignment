package com.flamingo.qa.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.flamingo.qa.dto.BookingDates;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PartialBookingRequest(
        String firstname,
        String lastname,
        Integer totalprice,
        Boolean depositpaid,
        BookingDates bookingdates,
        String additionalneeds) {
}
