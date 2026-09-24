package com.flamingo.qa.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BookingIdResponse(
        @JsonProperty("bookingid") int bookingid) {
}
