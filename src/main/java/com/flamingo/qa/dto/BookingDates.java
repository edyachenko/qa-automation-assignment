package com.flamingo.qa.dto;

import lombok.Builder;

@Builder
public record BookingDates(String checkin, String checkout) {
}
