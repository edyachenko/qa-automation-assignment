package com.flamingo.qa.tests.api;

import com.flamingo.qa.data.BookingData;
import com.flamingo.qa.data.RequiredBookingField;
import com.flamingo.qa.dto.BookingDates;
import com.flamingo.qa.dto.request.BookingRequest;
import com.flamingo.qa.extension.BookingDataExtension;
import com.flamingo.qa.extension.ExistingBooking;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.apache.http.HttpStatus.*;

/*
   Notes for reviewers:
   - different approaches for parameterized tests are presented in order to describe flexibility of different methods
   - JUnit5 extensions are used to show how can we wrap input data for tests
   - Nested classes used to separate the CRUD methods though it can be also done through different test classes
   - All comments added in this project only for informational purposes and will not be used in real projects
 */
@DisplayName("Bookings")
class BookingTests extends BaseApiTest {

    @Nested
    @DisplayName("POST /booking")
    class Create {

        @Test
        @DisplayName("creates a booking and returns it with a new id")
        @ExtendWith(BookingDataExtension.class)
        void createBookingReturnsItWithNewId(BookingRequest booking) {
            bookingClient.createBooking(booking)
                    .shouldHaveStatus(SC_OK)
                    .shouldHaveBookingId()
                    .shouldHaveBooking(booking);
        }

        @DisplayName("Creating a booking without")
        @ParameterizedTest(name = "{0} answers with 500")
        @EnumSource(RequiredBookingField.class)
        void createBookingWithoutRequiredFieldReturnsServerError(RequiredBookingField field) {
            bookingClient.createBooking(field.removeFrom(BookingData.random()))
                    .shouldHaveStatus(SC_INTERNAL_SERVER_ERROR);
        }

        @Test
        @DisplayName("rejects the same booking for the same guest and dates twice")
        @ExtendWith(BookingDataExtension.class)
        void createDuplicateBookingForSameGuestAndDatesIsRejected(ExistingBooking booking) {
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
        void getExistingBookingReturnsIt(ExistingBooking booking) {
            bookingClient.getBooking(booking.id())
                    .shouldHaveStatus(SC_OK)
                    .shouldHaveBooking(booking.request())
                    .shouldHaveCheckoutAfterCheckin();
        }

        @Test
        @DisplayName("answers 404 for an unknown id")
        void getBookingByUnknownIdReturnsNotFound() {
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
        void searchByNameFindsMatchingBooking(ExistingBooking booking) {
            bookingClient.searchBookings(booking.request().firstname(), booking.request().lastname())
                    .shouldHaveStatus(SC_OK)
                    .shouldContainOnly(booking.id());
        }

        @Test
        @DisplayName("returns an empty list for a name nobody booked under")
        void searchByUnknownNameReturnsEmptyList() {
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
        void updateBookingWithTokenReplacesItAndIsSearchableByNewName(ExistingBooking booking, BookingRequest changed) {
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

        static Stream<Named<BookingDates>> invalidDates() {
            //in real projects dates can be added via DateUtils depending on current date.
            return Stream.of(
                    Named.of("check-out before check-in", dates("2027-03-15", "2027-03-10")),
                    Named.of("check-out on the check-in day", dates("2027-03-10", "2027-03-10")),
                    Named.of("dates in the past", dates("2001-01-01", "2001-01-05")),
                    Named.of("dates not in ISO format", dates("10/03/2027", "15/03/2027")));
        }

        //another usage example of parameterized test
        @DisplayName("Updating a booking with")
        @ParameterizedTest(name = "{0} is rejected and changes nothing")
        @MethodSource("invalidDates")
        @ExtendWith(BookingDataExtension.class)
        void updateBookingWithInvalidDatesIsRejected(BookingDates invalidDates, ExistingBooking booking) {
            asAdmin().updateBooking(booking.id(), booking.request().withBookingdates(invalidDates))
                    .shouldHaveStatus(SC_BAD_REQUEST);

            bookingClient.getBooking(booking.id())
                    .shouldHaveStatus(SC_OK)
                    .shouldHaveBooking(booking.request());
        }

        //another usage example of parameterized test
        @DisplayName("Updating a booking with")
        @ParameterizedTest(name = "an invalid token \"{0}\" is forbidden and changes nothing")
        @ValueSource(strings = {"not-a-real-token", "", "   ", "abc123def456789"})
        @ExtendWith(BookingDataExtension.class)
        void updateBookingWithInvalidTokenIsForbidden(String invalidToken, ExistingBooking booking, BookingRequest changed) {
            bookingClient.withToken(invalidToken).updateBooking(booking.id(), changed)
                    .shouldHaveStatus(SC_FORBIDDEN);

            bookingClient.getBooking(booking.id())
                    .shouldHaveStatus(SC_OK)
                    .shouldHaveBooking(booking.request());
        }

        private static BookingDates dates(String checkin, String checkout) {
            return BookingDates.builder()
                    .checkin(checkin)
                    .checkout(checkout)
                    .build();
        }

        @Test
        @DisplayName("is forbidden without a token and changes nothing")
        @ExtendWith(BookingDataExtension.class)
        void updateBookingWithoutTokenIsForbidden(ExistingBooking booking, BookingRequest changed) {
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
        void partialUpdateChangesOnlyDatesAndIsSearchableByNewDates(ExistingBooking booking) {
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
        void partialUpdateWithoutTokenIsForbidden(ExistingBooking booking) {
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
        void deleteBookingWithTokenRemovesIt(ExistingBooking booking) {
            asAdmin().deleteBooking(booking.id())
                    .shouldHaveStatus(SC_CREATED);

            bookingClient.getBooking(booking.id())
                    .shouldHaveStatus(SC_NOT_FOUND);
        }

        @Test
        @DisplayName("is forbidden without a token and keeps the booking")
        @ExtendWith(BookingDataExtension.class)
        void deleteBookingWithoutTokenIsForbidden(ExistingBooking booking) {
            bookingClient.deleteBooking(booking.id())
                    .shouldHaveStatus(SC_FORBIDDEN);

            bookingClient.getBooking(booking.id())
                    .shouldHaveStatus(SC_OK);
        }
    }
}
