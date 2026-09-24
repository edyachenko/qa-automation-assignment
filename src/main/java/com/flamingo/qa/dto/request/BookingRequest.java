package com.flamingo.qa.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.flamingo.qa.dto.BookingDates;
import lombok.Builder;
import lombok.With;

@Builder
@With
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BookingRequest(
        String firstname,
        String lastname,
        Integer totalprice,
        Boolean depositpaid,
        BookingDates bookingdates,
        String additionalneeds) {
}
