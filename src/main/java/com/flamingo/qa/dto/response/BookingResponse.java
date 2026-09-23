package com.flamingo.qa.dto.response;

import com.flamingo.qa.dto.BookingDates;

public record BookingResponse(
        String firstname,
        String lastname,
        Integer totalprice,
        Boolean depositpaid,
        BookingDates bookingdates,
        String additionalneeds) {
}
