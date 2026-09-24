package com.flamingo.qa.api.data;

import com.flamingo.qa.api.dto.request.BookingRequest;
import lombok.RequiredArgsConstructor;

import java.util.function.UnaryOperator;

@RequiredArgsConstructor
public enum RequiredBookingField {

    FIRSTNAME(booking -> booking.withFirstname(null)),
    LASTNAME(booking -> booking.withLastname(null)),
    TOTALPRICE(booking -> booking.withTotalprice(null)),
    DEPOSITPAID(booking -> booking.withDepositpaid(null)),
    BOOKINGDATES(booking -> booking.withBookingdates(null));

    private final UnaryOperator<BookingRequest> remover;

    public BookingRequest removeFrom(BookingRequest booking) {
        return remover.apply(booking);
    }
}
