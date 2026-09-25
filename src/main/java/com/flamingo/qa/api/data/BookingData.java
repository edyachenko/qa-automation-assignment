package com.flamingo.qa.api.data;

import com.flamingo.qa.api.dto.BookingDates;
import com.flamingo.qa.api.dto.request.BookingRequest;
import com.flamingo.qa.api.dto.request.PartialBookingRequest;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@UtilityClass
public class BookingData {

    public BookingRequest random() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return BookingRequest.builder()
                .firstname("Guest" + uniqueSuffix())
                .lastname("Traveler" + uniqueSuffix())
                .totalprice(random.nextInt(50, 1000))
                .depositpaid(random.nextBoolean())
                .bookingdates(randomDates())
                .additionalneeds("Breakfast")
                .build();
    }

    public BookingDates randomDates() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        LocalDate checkin = LocalDate.now().plusDays(random.nextInt(1, 180));
        return BookingDates.builder()
                .checkin(checkin.toString())
                .checkout(checkin.plusDays(random.nextInt(1, 14)).toString())
                .build();
    }

    public PartialBookingRequest datesOnly(BookingDates dates) {
        return PartialBookingRequest.builder()
                .bookingdates(dates)
                .build();
    }

    public PartialBookingRequest firstnameOnly(String firstname) {
        return PartialBookingRequest.builder()
                .firstname(firstname)
                .build();
    }

    private String uniqueSuffix() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
}
