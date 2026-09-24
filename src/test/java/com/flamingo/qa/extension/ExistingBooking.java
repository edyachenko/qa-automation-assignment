package com.flamingo.qa.extension;

import com.flamingo.qa.dto.request.BookingRequest;
import lombok.Builder;

@Builder
public record ExistingBooking(int id, BookingRequest request) {
}
