package com.flamingo.qa.tests;

import com.flamingo.qa.data.BookingData;
import com.flamingo.qa.dto.BookingDates;
import com.flamingo.qa.dto.request.BookingRequest;
import com.flamingo.qa.extension.BookingDataExtension;
import com.flamingo.qa.extension.ExistingBooking;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.apache.http.HttpStatus.*;

@DisplayName("Bookings")
class BookingTests extends BaseApiTest {

    @Nested
    @DisplayName("POST /booking")
    class Create {

        @Test
        @DisplayName("creates a booking and returns it with a new id")
        @ExtendWith(BookingDataExtension.class)
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

        @Test
        @DisplayName("rejects the same booking for the same guest and dates twice")
        @ExtendWith(BookingDataExtension.class)
        void rejectsDuplicateBooking(ExistingBooking booking) {
            bookingClient.createBooking(booking.request())
                    .shouldHaveStatus(SC_CONFLICT);
        }
    }

    @Nested
    @DisplayName("GET /booking/{id}")
    class Get {

        @Test
        @DisplayName("returns an existing booking")
        @ExtendWith(BookingDataExtension.class)
        void returnsExistingBooking(ExistingBooking booking) {
            bookingClient.getBooking(booking.id())
                    .shouldHaveStatus(SC_OK)
                    .shouldHaveBooking(booking.request())
                    .shouldHaveCheckoutAfterCheckin();
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
        @ExtendWith(BookingDataExtension.class)
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
        @DisplayName("replaces the booking for an authorized user and finds it by the new name")
        @ExtendWith(BookingDataExtension.class)
        void replacesBookingWithToken(ExistingBooking booking, BookingRequest changed) {
            asAdmin().updateBooking(booking.id(), changed)
                    .shouldHaveStatus(SC_OK)
                    .shouldHaveBooking(changed);

            bookingClient.getBooking(booking.id())
                    .shouldHaveStatus(SC_OK)
                    .shouldHaveBooking(changed);

            bookingClient.searchBookings(changed.firstname(), changed.lastname())
                    .shouldHaveStatus(SC_OK)
                    .shouldContainOnly(booking.id());
        }

        @Test
        @DisplayName("rejects dates where check-out is before check-in and changes nothing")
        @ExtendWith(BookingDataExtension.class)
        void rejectsSwappedDates(ExistingBooking booking) {
            asAdmin().updateBooking(booking.id(), BookingData.withSwappedDates(booking.request()))
                    .shouldHaveStatus(SC_BAD_REQUEST);

            bookingClient.getBooking(booking.id())
                    .shouldHaveStatus(SC_OK)
                    .shouldHaveBooking(booking.request());
        }

        @Test
        @DisplayName("is forbidden without a token and changes nothing")
        @ExtendWith(BookingDataExtension.class)
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
        @DisplayName("changes only the dates and finds the booking by the new dates")
        @ExtendWith(BookingDataExtension.class)
        void changesOnlyDatesAndFindsBookingByThem(ExistingBooking booking) {
            BookingDates newDates = BookingData.randomDates();

            asAdmin().partialUpdateBooking(booking.id(), BookingData.datesOnly(newDates))
                    .shouldHaveStatus(SC_OK)
                    .shouldHaveDates(newDates)
                    .shouldHaveBookingIgnoring(booking.request(), "bookingdates");

            bookingClient.getBooking(booking.id())
                    .shouldHaveStatus(SC_OK)
                    .shouldHaveDates(newDates)
                    .shouldHaveBookingIgnoring(booking.request(), "bookingdates");

            bookingClient.searchBookingsByDates(newDates.checkin(), newDates.checkout())
                    .shouldHaveStatus(SC_OK)
                    .shouldContain(booking.id());
        }

        @Test
        @DisplayName("is forbidden without a token")
        @ExtendWith(BookingDataExtension.class)
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
        @ExtendWith(BookingDataExtension.class)
        void removesBookingWithToken(ExistingBooking booking) {
            asAdmin().deleteBooking(booking.id())
                    .shouldHaveStatus(SC_CREATED);

            bookingClient.getBooking(booking.id())
                    .shouldHaveStatus(SC_NOT_FOUND);
        }

        @Test
        @DisplayName("is forbidden without a token and keeps the booking")
        @ExtendWith(BookingDataExtension.class)
        void isForbiddenWithoutToken(ExistingBooking booking) {
            bookingClient.deleteBooking(booking.id())
                    .shouldHaveStatus(SC_FORBIDDEN);

            bookingClient.getBooking(booking.id())
                    .shouldHaveStatus(SC_OK);
        }
    }
}
