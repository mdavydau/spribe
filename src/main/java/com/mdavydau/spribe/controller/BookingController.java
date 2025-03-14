package com.mdavydau.spribe.controller;

import com.mdavydau.spribe.dto.BookingDto;
import com.mdavydau.spribe.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class BookingController {

    public final BookingService bookingService;

    @PostMapping(value = "/bookings/{id}/cancellation", produces = MediaType.APPLICATION_JSON_VALUE)
    public BookingDto bookingCancellation(@PathVariable UUID id) {
        return bookingService.bookingCancellation(id);
    }

    @PostMapping(value = "/bookings/{id}/confirmation", produces = MediaType.APPLICATION_JSON_VALUE)
    public BookingDto bookingConfirmation(@PathVariable UUID id) {
        return bookingService.bookingConfirmation(id);
    }

}
