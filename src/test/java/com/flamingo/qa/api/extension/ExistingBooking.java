package com.flamingo.qa.api.extension;

import com.flamingo.qa.api.dto.request.BookingRequest;
import lombok.Builder;

@Builder
public record ExistingBooking(int id, BookingRequest request) {
}
