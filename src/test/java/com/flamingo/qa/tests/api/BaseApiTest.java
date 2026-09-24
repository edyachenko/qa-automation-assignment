package com.flamingo.qa.tests.api;

import com.flamingo.qa.api.client.AdminToken;
import com.flamingo.qa.api.client.AuthClient;
import com.flamingo.qa.api.client.BookingClient;
import com.flamingo.qa.config.TestTag;
import com.flamingo.qa.report.AllureReportExtension;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;

import static io.qameta.allure.util.ResultsUtils.PARENT_SUITE_LABEL_NAME;

@Tag(TestTag.Names.API)
@ExtendWith(AllureReportExtension.class)
public abstract class BaseApiTest {

    protected final AuthClient authClient = new AuthClient();
    protected final BookingClient bookingClient = new BookingClient();

    protected BookingClient asAdmin() {
        return bookingClient.withToken(AdminToken.INSTANCE.getValue());
    }

    @BeforeEach
    void labelParentSuite() {
        Allure.label(PARENT_SUITE_LABEL_NAME, "REST: Restful Booker");
    }

    @AfterEach
    void deleteCreatedBookings() {
        if (!bookingClient.createdBookingIds().isEmpty()) {
            BookingClient admin = asAdmin();
            bookingClient.createdBookingIds().forEach(admin::deleteBooking);
        }
    }
}
