package com.mdavydau.spribe.service;

import com.mdavydau.spribe.dto.BookingDto;
import com.mdavydau.spribe.dto.BookingRequestDto;
import com.mdavydau.spribe.dto.UnitDto;
import com.mdavydau.spribe.entity.AccommodationType;
import com.mdavydau.spribe.utils.UnitRandomUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UnitRandomService {

    private final UnitService unitService;
    private final BookingService bookingService;

    public void initRandomUnits(int size) {
        IntStream.range(0, size).forEach(i -> {
            AccommodationType randomAccommodationType = UnitRandomUtil.randomAccommodationType();
            int randomRooms = ThreadLocalRandom.current().nextInt(1, 6);
            String description = UnitRandomUtil.randomAccommodationDescription(randomAccommodationType);
            Integer cost = ThreadLocalRandom.current().nextInt(200, 1000);
            UnitDto unitDto = UnitDto.builder()
                    .rooms(randomRooms)
                    .description(String.format(description, randomRooms))
                    .cost(cost)
                    .accommodationType(randomAccommodationType)
                    .available(ThreadLocalRandom.current().nextBoolean())
                    .build();
            UnitDto response = unitService.create(unitDto);

            int randomBookings = ThreadLocalRandom.current().nextInt(0, 5);
            initRandomBooking(response.getId(), randomBookings);
        });
    }

    public void initRandomBooking(UUID unitId, int size) {
        IntStream.range(0, size).forEach(i -> {
            LocalDate start = LocalDate.now().plusDays(ThreadLocalRandom.current().nextInt(-7, 7));
            LocalDate end = start.plusDays(ThreadLocalRandom.current().nextInt(1, 7));
            BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                    .startDate(start)
                    .endDate(end)
                    .email(String.format("test-%s@test.com", i))
                    .build();
            try {
                BookingDto booked = unitService.book(unitId, bookingRequestDto);
                log.info("Create booking {} for unit {} start {} and end {} dates", booked.getId(), unitId, start, end);
            } catch (Exception e) {
                log.info("Booking failed for unit {} start {} and end {} dates", unitId, start, end);
            }
        });
    }
}
