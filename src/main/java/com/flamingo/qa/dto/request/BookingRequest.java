package com.flamingo.qa.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.flamingo.qa.dto.BookingDates;
import lombok.Builder;
import lombok.With;

@Builder
@With
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BookingRequest(
        @JsonProperty("firstname") String firstname,
        @JsonProperty("lastname") String lastname,
        @JsonProperty("totalprice") Integer totalprice,
        @JsonProperty("depositpaid") Boolean depositpaid,
        @JsonProperty("bookingdates") BookingDates bookingdates,
        @JsonProperty("additionalneeds") String additionalneeds) {
}
