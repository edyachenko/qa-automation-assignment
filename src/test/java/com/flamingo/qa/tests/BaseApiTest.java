package com.flamingo.qa.tests;

import com.flamingo.qa.api.AuthClient;
import com.flamingo.qa.api.BookingClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;

@Tag(TestTag.Names.API)
public abstract class BaseApiTest {

    protected final AuthClient authClient = new AuthClient();
    protected final BookingClient bookingClient = new BookingClient();

    protected BookingClient asAdmin() {
        return bookingClient.withToken(authClient.adminToken());
    }

    @AfterEach
    void deleteCreatedBookings() {
        BookingClient admin = asAdmin();
        bookingClient.createdBookingIds().forEach(admin::deleteBooking);
    }
}
