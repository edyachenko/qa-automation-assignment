package com.flamingo.qa.data;

import com.flamingo.qa.dto.BookingDates;
import com.flamingo.qa.dto.request.BookingRequest;
import com.flamingo.qa.dto.request.PartialBookingRequest;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class BookingData {

    private BookingData() {
    }

    public static BookingRequest random() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        LocalDate checkin = LocalDate.now().plusDays(random.nextInt(1, 180));
        return new BookingRequest(
                "Guest" + uniqueSuffix(),
                "Traveler" + uniqueSuffix(),
                random.nextInt(50, 1000),
                random.nextBoolean(),
                new BookingDates(checkin.toString(), checkin.plusDays(random.nextInt(1, 14)).toString()),
                "Breakfast");
    }

    public static BookingRequest withoutFirstname() {
        BookingRequest booking = random();
        return new BookingRequest(null, booking.lastname(), booking.totalprice(), booking.depositpaid(),
                booking.bookingdates(), booking.additionalneeds());
    }

    public static PartialBookingRequest firstnameOnly(String firstname) {
        return new PartialBookingRequest(firstname, null, null, null, null, null);
    }

    private static String uniqueSuffix() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
}
