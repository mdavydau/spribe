package com.mdavydau.spribe.service;

import com.mdavydau.spribe.config.BaseSpringBootTestConfig;
import com.mdavydau.spribe.dto.BookingDto;
import com.mdavydau.spribe.dto.BookingRequestDto;
import com.mdavydau.spribe.dto.UnitDto;
import com.mdavydau.spribe.dto.UnitSearchDto;
import com.mdavydau.spribe.entity.BookingStatus;
import com.mdavydau.spribe.exception.BusinessException;
import com.mdavydau.spribe.exception.NotFoundException;
import com.mdavydau.spribe.utils.UnitUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class UnitServiceTest extends BaseSpringBootTestConfig {

    @Autowired
    UnitService unitService;

    @AfterEach
    void tearDown() {
        testService.tearDown();
    }

    @Test
    @DisplayName("Should create new unit")
    void create() {
        UnitDto unitDto = testService.createUnitDto(true);
        UnitDto response = unitService.create(unitDto);

        assertThat(response)
                .as("Unit created")
                .extracting(UnitDto::getRooms, UnitDto::getAvailable, UnitDto::getAccommodationType, UnitDto::getDescription, UnitDto::getCost)
                .contains(unitDto.getRooms(), unitDto.getAvailable(), unitDto.getAccommodationType(), unitDto.getDescription(), UnitUtils.addCostSystemMarkup(unitDto.getCost()));
    }

    @Test
    @DisplayName("Should update existed unit availability")
    void updateAvailability() {
        UnitDto unitDto = testService.saveUnitDto(true);
        Integer cost = unitDto.getCost();
        unitDto.setAvailable(false);
        unitDto.setCost(null);

        UnitDto response = unitService.update(unitDto.getId(), unitDto);
        assertThat(response)
                .as("Unit updated")
                .extracting(UnitDto::getAvailable, UnitDto::getCost)
                .contains(false, cost);
    }

    @Test
    @DisplayName("Should update existed unit cost")
    void updateCost() {
        UnitDto unitDto = testService.saveUnitDto(true);
        unitDto.setCost(100);

        UnitDto response = unitService.update(unitDto.getId(), unitDto);
        assertThat(response)
                .as("Unit updated")
                .extracting(UnitDto::getCost)
                .isEqualTo(UnitUtils.addCostSystemMarkup(unitDto.getCost()));
    }

    @Test
    @DisplayName("Should display all available units based on cost and dates")
    void search() {
        testService.saveUnitDto(true);
        testService.saveUnitDto(false);

        List<UnitDto> search = unitService.search(UnitSearchDto.builder().build(), Pageable.unpaged());

        assertThat(search).hasSize(1);
    }

    @Test
    @DisplayName("Should book available unit")
    void book() {
        UnitDto unitDto = testService.saveUnitDto(true);

        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plusDays(2);
        String email = "test@test.com";

        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .startDate(start)
                .endDate(end)
                .email(email)
                .build();

        BookingDto booked = unitService.book(unitDto.getId(), bookingRequestDto);

        assertThat(booked)
                .as("Booking")
                .extracting(BookingDto::getUnitId, BookingDto::getBookingStartDate, BookingDto::getBookingEndDate, BookingDto::getEmail)
                .contains(unitDto.getId(), start, end, email);

        BusinessException exception = assertThrows(BusinessException.class, () -> unitService.book(unitDto.getId(), bookingRequestDto));
        String expectedMessage = "is already booked for provided start";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    @DisplayName("Should create booking day by day")
    void bookingCreateDayByDay() {
        LocalDate start = LocalDate.now();
        String email = "test@test.com";
        UnitDto unitDto = testService.saveUnitDto(true);

        for (int i = 0; i < 10; i++) {
            LocalDate bookingStart = start;
            LocalDate bookingEnd = bookingStart.plusDays(2);
            createAndValidate(unitDto.getId(), bookingStart, bookingEnd, email);
            start = bookingEnd.plusDays(1);
        }
    }

    private void createAndValidate(UUID unitId, LocalDate start, LocalDate end, String email) {
        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .startDate(start)
                .endDate(end)
                .email(email)
                .build();

        log.info("Book {} for start {} and end {}", unitId, start, end);

        BookingDto booked = unitService.book(unitId, bookingRequestDto);

        assertThat(booked)
                .as("Booking")
                .extracting(BookingDto::getUnitId, BookingDto::getBookingStartDate, BookingDto::getBookingEndDate, BookingDto::getEmail)
                .contains(unitId, start, end, email);
    }


    @Test
    @DisplayName("Should not book un-available unit")
    void bookUnavailable() {
        UnitDto unitDto = testService.saveUnitDto(false);

        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plusDays(2);
        String email = "test@test.com";

        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .startDate(start)
                .endDate(end)
                .email(email)
                .build();

        NotFoundException exception = assertThrows(NotFoundException.class, () -> unitService.book(unitDto.getId(), bookingRequestDto));
        String expectedMessage = "Unit not found";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    @DisplayName("Should book cancelled unit")
    void bookCancelled() {
        UnitDto unitDto = testService.saveUnitDto(true);

        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plusDays(2);
        String email = "test@test.com";
        testService.saveBooking(unitDto.getId(), BookingStatus.CANCELLED, start, end, email);

        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .startDate(start)
                .endDate(end)
                .email(email)
                .build();

        BookingDto booked = unitService.book(unitDto.getId(), bookingRequestDto);

        assertThat(booked)
                .as("Booking")
                .extracting(BookingDto::getUnitId, BookingDto::getBookingStartDate, BookingDto::getBookingEndDate, BookingDto::getEmail)
                .contains(unitDto.getId(), start, end, email);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should book available unit concurrently")
    void bookConcurrently() {
        UnitDto unitDto = testService.saveUnitDto(true);

        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plusDays(2);
        String email = "test@test.com";

        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .startDate(start)
                .endDate(end)
                .email(email)
                .build();

        int numberOfThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CyclicBarrier barrier = new CyclicBarrier(numberOfThreads);
        List<Future<?>> futures = new ArrayList<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < numberOfThreads; i++) {
            final int threadNum = i;
            futures.add(executorService.submit(() -> {
                try {
                    barrier.await();
                    try {
                        BookingDto booked = unitService.book(unitDto.getId(), bookingRequestDto);
                        successCount.incrementAndGet();
                        log.info("Booking from thread {}", threadNum);
                        verifyBooking(booked, unitDto.getId(), start, end, email);
                    } catch (BusinessException e) {
                        failureCount.incrementAndGet();
                        log.info("Booking failed from thread {}", threadNum);
                        verifyBookingException(bookingRequestDto, unitDto.getId());
                    }
                } catch (Exception e) {
                    log.info("Thread {} failed with unexpected exception", threadNum);
                }
                return null;
            }));
        }

        for (Future<?> future : futures) {
            future.get();
        }

        executorService.shutdown();
        assertThat(executorService.awaitTermination(10, TimeUnit.SECONDS))
                .isTrue();

        assertEquals(1, successCount.get(), "Only one thread should successfully book the unit");
        assertEquals(numberOfThreads - 1, failureCount.get(), "All other threads should fail with an exception");
    }

    private void verifyBooking(BookingDto booked, UUID unitId, LocalDate start, LocalDate end, String email) {
        assertThat(booked)
                .as("Unit booking")
                .extracting(BookingDto::getUnitId, BookingDto::getBookingStartDate,
                        BookingDto::getBookingEndDate, BookingDto::getEmail)
                .contains(unitId, start, end, email);
    }

    private void verifyBookingException(BookingRequestDto bookingRequestDto, UUID unitId) {
        String expectedMessage = "is already booked for provided start";
        BusinessException exception = assertThrows(BusinessException.class, () -> unitService.book(unitId, bookingRequestDto));
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
}