package com.mdavydau.spribe.controller;

import com.mdavydau.spribe.dto.BookingDto;
import com.mdavydau.spribe.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class BookingController {

    public final BookingService bookingService;

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Booking cancellation")
    @PostMapping(value = "/bookings/{id}/cancellation", produces = MediaType.APPLICATION_JSON_VALUE)
    public BookingDto bookingCancellation(@PathVariable UUID id) {
        return bookingService.bookingCancellation(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Booking confirmation (payment)")
    @PostMapping(value = "/bookings/{id}/confirmation", produces = MediaType.APPLICATION_JSON_VALUE)
    public BookingDto bookingConfirmation(@PathVariable UUID id) {
        return bookingService.bookingConfirmation(id);
    }

}
