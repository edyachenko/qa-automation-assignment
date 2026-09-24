package com.flamingo.qa.api;

import com.flamingo.qa.assertions.BookingIdsResponseAssert;
import com.flamingo.qa.assertions.BookingResponseAssert;
import com.flamingo.qa.assertions.CreateBookingResponseAssert;
import com.flamingo.qa.assertions.StatusResponseAssert;
import com.flamingo.qa.dto.request.BookingRequest;
import com.flamingo.qa.dto.request.PartialBookingRequest;
import io.qameta.allure.Step;
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

    @Step("Create booking {0}")
    public CreateBookingResponseAssert createBooking(BookingRequest booking) {
        CreateBookingResponseAssert created = new CreateBookingResponseAssert(request().body(booking).post(Endpoints.BOOKINGS));
        if (created.isSuccessful()) {
            createdBookingIds.add(created.bookingId());
        }
        return created;
    }

    @Step("Search bookings by name: {0} {1}")
    public BookingIdsResponseAssert searchBookings(String firstname, String lastname) {
        return new BookingIdsResponseAssert(request()
                .queryParam("firstname", firstname)
                .queryParam("lastname", lastname)
                .get(Endpoints.BOOKINGS));
    }

    @Step("Search bookings by dates: {0} - {1}")
    public BookingIdsResponseAssert searchBookingsByDates(String checkin, String checkout) {
        return new BookingIdsResponseAssert(request()
                .queryParam("checkin", checkin)
                .queryParam("checkout", checkout)
                .get(Endpoints.BOOKINGS));
    }

    @Step("Get booking {0}")
    public BookingResponseAssert getBooking(int id) {
        return new BookingResponseAssert(request().get(Endpoints.BOOKING_BY_ID, id));
    }

    @Step("Update booking {0} with {1}")
    public BookingResponseAssert updateBooking(int id, BookingRequest booking) {
        return new BookingResponseAssert(request().body(booking).put(Endpoints.BOOKING_BY_ID, id));
    }

    @Step("Partially update booking {0} with {1}")
    public BookingResponseAssert partialUpdateBooking(int id, PartialBookingRequest changes) {
        return new BookingResponseAssert(request().body(changes).patch(Endpoints.BOOKING_BY_ID, id));
    }

    @Step("Delete booking {0}")
    public StatusResponseAssert deleteBooking(int id) {
        return new StatusResponseAssert(request().delete(Endpoints.BOOKING_BY_ID, id));
    }

    @Override
    protected RequestSpecification request() {
        return token == null ? super.request() : super.request().cookie("token", token);
    }
}
