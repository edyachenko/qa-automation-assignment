package com.flamingo.qa.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateBookingResponse(
        @JsonProperty("bookingid") int bookingid,
        @JsonProperty("booking") BookingResponse booking) {
}
