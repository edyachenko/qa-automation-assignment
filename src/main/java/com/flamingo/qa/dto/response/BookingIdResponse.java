package com.flamingo.qa.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BookingIdResponse(
        @JsonProperty("bookingid") int bookingid) {
}
