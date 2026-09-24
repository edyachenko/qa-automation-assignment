package com.flamingo.qa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record BookingDates(
        @JsonProperty("checkin") String checkin,
        @JsonProperty("checkout") String checkout) {
}
