package com.flamingo.qa.extension;

import com.flamingo.qa.api.AuthClient;
import com.flamingo.qa.api.BookingClient;
import com.flamingo.qa.data.BookingData;
import com.flamingo.qa.dto.request.BookingRequest;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.Set;

import static org.apache.http.HttpStatus.SC_OK;

public class BookingDataExtension implements ParameterResolver, AfterEachCallback {

    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(BookingDataExtension.class);
    private static final Set<Class<?>> SUPPORTED = Set.of(BookingRequest.class, ExistingBooking.class);

    @Override
    public boolean supportsParameter(ParameterContext parameter, ExtensionContext context) {
        return SUPPORTED.contains(parameter.getParameter().getType());
    }

    @Override
    public Object resolveParameter(ParameterContext parameter, ExtensionContext context) {
        if (parameter.getParameter().getType() == ExistingBooking.class) {
            BookingRequest booking = BookingData.random();
            int id = client(context).createBooking(booking).shouldHaveStatus(SC_OK).bookingId();
            return ExistingBooking.builder()
                    .id(id)
                    .request(booking)
                    .build();
        }
        return BookingData.random();
    }

    @Override
    public void afterEach(ExtensionContext context) {
        BookingClient client = context.getStore(NAMESPACE).get(BookingClient.class, BookingClient.class);
        if (client != null) {
            BookingClient admin = client.withToken(new AuthClient().adminToken());
            client.createdBookingIds().forEach(admin::deleteBooking);
        }
    }

    private BookingClient client(ExtensionContext context) {
        return context.getStore(NAMESPACE).getOrComputeIfAbsent(BookingClient.class, key -> new BookingClient(), BookingClient.class);
    }
}
