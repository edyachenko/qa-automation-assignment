package com.flamingo.qa.tests;

import com.flamingo.qa.data.BookingData;
import com.flamingo.qa.dto.request.BookingRequest;
import com.flamingo.qa.extension.ExistingBooking;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;

@DisplayName("Bookings")
class BookingTests extends BaseApiTest {

    //todos:
    /*
        APi:
      add auth test
      add crud tests
      At least 3 API tests covering CRUD operations:
      positive (includeing busines logic , like checking cannot be later than checkout, etc)
      1. create booking and retrieve (delete of booking is on after hook)
      2. create booking + update + get + some filter 1 ?firstname=sally&lastname=brown
      3. create booking + patch + get + some filter 2 ?checkin=2014-03-13&checkout=2014-05-21

      negative
      1. create without required fields
      2. unable to update booking dates of checking checkout (swap) or else
      3. create the same bookings on the same time
      4. try to update booking without token (Creates a new auth token to use for access to the PUT and DELETE /booking)




    Basic assertions using AssertJ


     */

    @Nested
    @DisplayName("POST /booking")
    class Create {

        @Test
        @DisplayName("creates a booking and returns it with a new id")
        void createsBooking(BookingRequest booking) {
            bookingClient.createBooking(booking)
                    .shouldHaveStatus(SC_OK)
                    .shouldHaveBookingId()
                    .shouldHaveBooking(booking);
        }

        @Test
        @DisplayName("answers 500 for a booking without a first name")
        void answersServerErrorForBookingWithoutFirstname() {
            bookingClient.createBooking(BookingData.withoutFirstname())
                    .shouldHaveStatus(SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Nested
    @DisplayName("GET /booking/{id}")
    class Get {

        @Test
        @DisplayName("returns an existing booking")
        void returnsExistingBooking(ExistingBooking booking) {
            bookingClient.getBooking(booking.id())
                    .shouldHaveStatus(SC_OK)
                    .shouldHaveBooking(booking.request());
        }

        @Test
        @DisplayName("answers 404 for an unknown id")
        void answersNotFoundForUnknownId() {
            bookingClient.getBooking(999_999_999)
                    .shouldHaveStatus(SC_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("GET /booking")
    class Search {

        @Test
        @DisplayName("finds a booking by first and last name")
        void findsBookingByName(ExistingBooking booking) {
            bookingClient.searchBookings(booking.request().firstname(), booking.request().lastname())
                    .shouldHaveStatus(SC_OK)
                    .shouldContainOnly(booking.id());
        }

        @Test
        @DisplayName("returns an empty list for a name nobody booked under")
        void returnsEmptyListForUnknownName() {
            bookingClient.searchBookings("Nobody" + System.nanoTime(), "Unknown")
                    .shouldHaveStatus(SC_OK)
                    .shouldBeEmpty();
        }
    }

    @Nested
    @DisplayName("PUT /booking/{id}")
    class Update {

        @Test
        @DisplayName("replaces the booking for an authorized user")
        void replacesBookingWithToken(ExistingBooking booking, BookingRequest changed) {
            asAdmin().updateBooking(booking.id(), changed)
                    .shouldHaveStatus(SC_OK)
                    .shouldHaveBooking(changed);
        }

        @Test
        @DisplayName("is forbidden without a token and changes nothing")
        void isForbiddenWithoutToken(ExistingBooking booking, BookingRequest changed) {
            bookingClient.updateBooking(booking.id(), changed)
                    .shouldHaveStatus(SC_FORBIDDEN);

            bookingClient.getBooking(booking.id())
                    .shouldHaveStatus(SC_OK)
                    .shouldHaveBooking(booking.request());
        }
    }

    @Nested
    @DisplayName("PATCH /booking/{id}")
    class PartialUpdate {

        @Test
        @DisplayName("changes only the given field")
        void changesOnlyGivenField(ExistingBooking booking) {
            asAdmin().partialUpdateBooking(booking.id(), BookingData.firstnameOnly("Patched"))
                    .shouldHaveStatus(SC_OK)
                    .shouldHaveFirstname("Patched")
                    .shouldHaveBookingIgnoring(booking.request(), "firstname");
        }

        @Test
        @DisplayName("is forbidden without a token")
        void isForbiddenWithoutToken(ExistingBooking booking) {
            bookingClient.partialUpdateBooking(booking.id(), BookingData.firstnameOnly("Patched"))
                    .shouldHaveStatus(SC_FORBIDDEN);
        }
    }

    @Nested
    @DisplayName("DELETE /booking/{id}")
    class Delete {

        @Test
        @DisplayName("removes the booking for an authorized user")
        void removesBookingWithToken(ExistingBooking booking) {
            asAdmin().deleteBooking(booking.id())
                    .shouldHaveStatus(SC_CREATED);

            bookingClient.getBooking(booking.id())
                    .shouldHaveStatus(SC_NOT_FOUND);
        }

        @Test
        @DisplayName("is forbidden without a token and keeps the booking")
        void isForbiddenWithoutToken(ExistingBooking booking) {
            bookingClient.deleteBooking(booking.id())
                    .shouldHaveStatus(SC_FORBIDDEN);

            bookingClient.getBooking(booking.id())
                    .shouldHaveStatus(SC_OK);
        }
    }
}
