package com.flamingo.qa.tests.api;

import com.flamingo.qa.api.AdminToken;
import com.flamingo.qa.api.AuthClient;
import com.flamingo.qa.api.BookingClient;
import com.flamingo.qa.config.TestTag;
import com.flamingo.qa.report.AllureReportExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;

@Tag(TestTag.Names.API)
@ExtendWith(AllureReportExtension.class)
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
