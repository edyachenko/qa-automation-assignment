package com.flamingo.qa.api;

import com.flamingo.qa.assertions.BookingIdsResponseAssert;
import com.flamingo.qa.assertions.BookingResponseAssert;
import com.flamingo.qa.assertions.CreateBookingResponseAssert;
import com.flamingo.qa.assertions.StatusResponseAssert;
import com.flamingo.qa.dto.request.BookingRequest;
import com.flamingo.qa.dto.request.PartialBookingRequest;
import io.restassured.specification.RequestSpecification;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.With;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingClient extends ApiClient {

    @With
    private final String token;
    private final List<Integer> createdBookingIds;

    public BookingClient() {
        this(null, new ArrayList<>());
    }

    public List<Integer> createdBookingIds() {
        return List.copyOf(createdBookingIds);
    }

    public CreateBookingResponseAssert createBooking(BookingRequest booking) {
        CreateBookingResponseAssert created = new CreateBookingResponseAssert(request().body(booking).post(Endpoints.BOOKINGS));
        if (created.isSuccessful()) {
            createdBookingIds.add(created.bookingId());
        }
        return created;
    }

    public BookingIdsResponseAssert searchBookings(String firstname, String lastname) {
        return new BookingIdsResponseAssert(request()
                .queryParam("firstname", firstname)
                .queryParam("lastname", lastname)
                .get(Endpoints.BOOKINGS));
    }

    public BookingIdsResponseAssert searchBookingsByDates(String checkin, String checkout) {
        return new BookingIdsResponseAssert(request()
                .queryParam("checkin", checkin)
                .queryParam("checkout", checkout)
                .get(Endpoints.BOOKINGS));
    }

    public BookingResponseAssert getBooking(int id) {
        return new BookingResponseAssert(request().get(Endpoints.BOOKING_BY_ID, id));
    }

    public BookingResponseAssert updateBooking(int id, BookingRequest booking) {
        return new BookingResponseAssert(request().body(booking).put(Endpoints.BOOKING_BY_ID, id));
    }

    public BookingResponseAssert partialUpdateBooking(int id, PartialBookingRequest changes) {
        return new BookingResponseAssert(request().body(changes).patch(Endpoints.BOOKING_BY_ID, id));
    }

    public StatusResponseAssert deleteBooking(int id) {
        return new StatusResponseAssert(request().delete(Endpoints.BOOKING_BY_ID, id));
    }

    @Override
    protected RequestSpecification request() {
        return token == null ? super.request() : super.request().cookie("token", token);
    }
}
