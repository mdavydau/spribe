package com.mdavydau.spribe.job;

import com.mdavydau.spribe.config.BaseSpringBootTestConfig;
import com.mdavydau.spribe.dto.UnitDto;
import com.mdavydau.spribe.entity.BookingEntity;
import com.mdavydau.spribe.entity.BookingStatus;
import com.mdavydau.spribe.repository.BookingRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
class BookingPaymentTimeoutJobTest extends BaseSpringBootTestConfig {

    @Autowired
    BookingPaymentTimeoutJob bookingPaymentTimeoutJob;
    @Autowired
    BookingRepository bookingRepository;

    @AfterEach
    void tearDown() {
        testService.tearDown();
    }

    @Test
    void startPaymentTimeoutJob() {
        LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(LocalDate.now().plusDays(2), LocalTime.MAX);
        String email = "test@test.com";
        UnitDto unitDto1 = testService.saveUnitDto(true);
        UnitDto unitDto11 = testService.saveUnitDto(true);
        UnitDto unitDto2 = testService.saveUnitDto(true);
        UnitDto unitDto3 = testService.saveUnitDto(true);
        BookingEntity bookingEntity1 = testService.saveBooking(unitDto1.getId(), BookingStatus.PENDING, start.toLocalDate(), end.toLocalDate(), email);
        BookingEntity bookingEntity11 = testService.saveBooking(unitDto11.getId(), BookingStatus.PENDING, start.toLocalDate(), end.toLocalDate(), email);
        BookingEntity bookingEntity2 = testService.saveBooking(unitDto2.getId(), BookingStatus.CANCELLED, start.toLocalDate(), end.toLocalDate(), email);
        BookingEntity bookingEntity3 = testService.saveBooking(unitDto3.getId(), BookingStatus.CONFIRMED, start.toLocalDate(), end.toLocalDate(), email);

        validateBookingStatus(bookingEntity1.getId(), BookingStatus.PENDING);
        validateBookingStatus(bookingEntity11.getId(), BookingStatus.PENDING);

        bookingRepository.updateBookingCreated(bookingEntity1.getId(), LocalDateTime.now().minusMinutes(16));
        bookingRepository.updateBookingCreated(bookingEntity11.getId(), LocalDateTime.now().minusMinutes(10));

        bookingPaymentTimeoutJob.startPaymentTimeoutJob();

        validateBookingStatus(bookingEntity1.getId(), BookingStatus.PAYMENT_TIMEOUT);
        validateBookingStatus(bookingEntity11.getId(), BookingStatus.PENDING);
        validateBookingStatus(bookingEntity2.getId(), BookingStatus.CANCELLED);
        validateBookingStatus(bookingEntity3.getId(), BookingStatus.CONFIRMED);
    }

    private void validateBookingStatus(UUID id, BookingStatus status) {
        bookingRepository.findById(id).map(bookingEntity -> {
            assertThat(bookingEntity)
                    .extracting(BookingEntity::getStatus)
                    .isEqualTo(status);
            return bookingEntity;
        }).orElseThrow();
    }
}