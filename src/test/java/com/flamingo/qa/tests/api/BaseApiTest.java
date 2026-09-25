package com.flamingo.qa.tests.api;

import com.flamingo.qa.api.client.AdminToken;
import com.flamingo.qa.api.client.AuthClient;
import com.flamingo.qa.api.client.BookingClient;
import com.flamingo.qa.config.TestTag;
import com.flamingo.qa.report.ParentSuite;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;

@Tag(TestTag.API)
@ParentSuite("REST: Restful Booker")
public abstract class BaseApiTest {

    protected final AuthClient authClient = new AuthClient();
    protected final BookingClient bookingClient = new BookingClient();

    protected BookingClient asAdmin() {
        return bookingClient.withToken(AdminToken.INSTANCE.getValue());
    }

    @AfterEach
    void deleteCreatedBookings() {
        if (!bookingClient.createdBookingIds().isEmpty()) {
            BookingClient admin = asAdmin();
            bookingClient.createdBookingIds().forEach(admin::deleteBooking);
        }
    }
}
