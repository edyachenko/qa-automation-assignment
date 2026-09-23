package com.flamingo.qa.api;

import com.flamingo.qa.assertions.BookingIdsResponseAssert;
import com.flamingo.qa.assertions.BookingResponseAssert;
import com.flamingo.qa.assertions.CreateBookingResponseAssert;
import com.flamingo.qa.assertions.StatusResponseAssert;
import com.flamingo.qa.dto.request.BookingRequest;
import com.flamingo.qa.dto.request.PartialBookingRequest;
import io.restassured.specification.RequestSpecification;

import java.util.ArrayList;
import java.util.List;

public class BookingClient extends ApiClient {

    private final String token;
    private final List<Integer> createdBookingIds;

    public BookingClient() {
        this(null, new ArrayList<>());
    }

    private BookingClient(String token, List<Integer> createdBookingIds) {
        this.token = token;
        this.createdBookingIds = createdBookingIds;
    }

    public BookingClient withToken(String token) {
        return new BookingClient(token, createdBookingIds);
    }

    public List<Integer> createdBookingIds() {
        return List.copyOf(createdBookingIds);
    }

    public CreateBookingResponseAssert createBooking(BookingRequest booking) {
        CreateBookingResponseAssert created = new CreateBookingResponseAssert(request().body(booking).post("/booking"));
        if (created.isSuccessful()) {
            createdBookingIds.add(created.bookingId());
        }
        return created;
    }

    public BookingIdsResponseAssert searchBookings(String firstname, String lastname) {
        return new BookingIdsResponseAssert(request()
                .queryParam("firstname", firstname)
                .queryParam("lastname", lastname)
                .get("/booking"));
    }

    public BookingResponseAssert getBooking(int id) {
        return new BookingResponseAssert(request().get("/booking/{id}", id));
    }

    public BookingResponseAssert updateBooking(int id, BookingRequest booking) {
        return new BookingResponseAssert(request().body(booking).put("/booking/{id}", id));
    }

    public BookingResponseAssert partialUpdateBooking(int id, PartialBookingRequest changes) {
        return new BookingResponseAssert(request().body(changes).patch("/booking/{id}", id));
    }

    public StatusResponseAssert deleteBooking(int id) {
        return new StatusResponseAssert(request().delete("/booking/{id}", id));
    }

    @Override
    protected RequestSpecification request() {
        return token == null ? super.request() : super.request().cookie("token", token);
    }
}
