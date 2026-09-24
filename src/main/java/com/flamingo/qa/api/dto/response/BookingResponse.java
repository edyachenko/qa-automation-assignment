package com.flamingo.qa.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.flamingo.qa.api.dto.BookingDates;

public record BookingResponse(
        @JsonProperty("firstname") String firstname,
        @JsonProperty("lastname") String lastname,
        @JsonProperty("totalprice") Integer totalprice,
        @JsonProperty("depositpaid") Boolean depositpaid,
        @JsonProperty("bookingdates") BookingDates bookingdates,
        @JsonProperty("additionalneeds") String additionalneeds) {
}
