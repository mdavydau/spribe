package com.mdavydau.spribe.service;

import com.mdavydau.spribe.config.BaseSpringBootTestConfig;
import com.mdavydau.spribe.dto.BookingDto;
import com.mdavydau.spribe.dto.UnitDto;
import com.mdavydau.spribe.entity.BookingStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BookingServiceTest extends BaseSpringBootTestConfig {

    @Autowired
    BookingService bookingService;

    @AfterEach
    void tearDown() {
        testService.tearDown();
    }

    @Test
    @DisplayName("Should create booking")
    void bookingCreate() {
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plusDays(2);
        String email = "test@test.com";
        UnitDto unitDto = testService.saveUnitDto(true);
        BookingDto bookingDto = testService.createBooking(unitDto.getId(), start, end, email);

        assertThat(bookingDto)
                .as("Booking cancelled")
                .extracting(BookingDto::getUnitId, BookingDto::getStatus, BookingDto::getEmail)
                .contains(unitDto.getId(), BookingStatus.PENDING, email);
    }

    @Test
    @DisplayName("Should cancel booking")
    void bookingCancellation() {
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plusDays(2);
        String email = "test@test.com";
        UnitDto unitDto = testService.saveUnitDto(true);
        BookingDto bookingDto = testService.createBooking(unitDto.getId(), start, end, email);

        BookingDto response = bookingService.bookingCancellation(bookingDto.getId());

        assertThat(response)
                .as("Booking cancelled")
                .extracting(BookingDto::getUnitId, BookingDto::getStatus, BookingDto::getEmail)
                .contains(unitDto.getId(), BookingStatus.CANCELLED, email);
    }

    @Test
    @DisplayName("Should confirm booking")
    void bookingConfirmation() {
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plusDays(2);
        String email = "test@test.com";
        UnitDto unitDto = testService.saveUnitDto(true);
        BookingDto bookingDto = testService.createBooking(unitDto.getId(), start, end, email);

        BookingDto response = bookingService.bookingConfirmation(bookingDto.getId());

        assertThat(response)
                .as("Booking cancelled")
                .extracting(BookingDto::getUnitId, BookingDto::getStatus, BookingDto::getEmail)
                .contains(unitDto.getId(), BookingStatus.CONFIRMED, email);
    }

    @Test
    @DisplayName("Should find all booked units")
    void findAllBooked() {
        LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(LocalDate.now().plusDays(2), LocalTime.MAX);
        String email = "test@test.com";
        UnitDto unitDto1 = testService.saveUnitDto(true);
        UnitDto unitDto2 = testService.saveUnitDto(true);
        UnitDto unitDto3 = testService.saveUnitDto(true);
        UnitDto unitDto4 = testService.saveUnitDto(true);
        UnitDto unitDto5 = testService.saveUnitDto(true);
        testService.saveBooking(unitDto1.getId(), BookingStatus.PENDING, start.toLocalDate(), end.toLocalDate(), email);
        testService.saveBooking(unitDto2.getId(), BookingStatus.MAINTENANCE, start.toLocalDate(), end.toLocalDate(), email);
        testService.saveBooking(unitDto3.getId(), BookingStatus.CONFIRMED, start.toLocalDate(), end.toLocalDate(), email);

        testService.saveBooking(unitDto4.getId(), BookingStatus.CANCELLED, start.toLocalDate(), end.toLocalDate(), email);
        testService.saveBooking(unitDto5.getId(), BookingStatus.PAYMENT_TIMEOUT, start.toLocalDate(), end.toLocalDate(), email);

        List<BookingDto> allBooked = bookingService.findAllBooked(start, end);

        assertThat(allBooked).as("Booked units").hasSize(3);
        assertThat(allBooked).as("Booked units")
                .extracting(BookingDto::getUnitId)
                .contains(unitDto1.getId(), unitDto2.getId(), unitDto3.getId());
    }
}