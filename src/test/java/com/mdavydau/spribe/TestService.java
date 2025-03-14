package com.mdavydau.spribe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mdavydau.spribe.dto.BookingDto;
import com.mdavydau.spribe.dto.UnitDto;
import com.mdavydau.spribe.entity.AccommodationType;
import com.mdavydau.spribe.entity.BookingEntity;
import com.mdavydau.spribe.entity.BookingStatus;
import com.mdavydau.spribe.repository.BookingRepository;
import com.mdavydau.spribe.repository.UnitRepository;
import com.mdavydau.spribe.service.BookingService;
import com.mdavydau.spribe.service.UnitService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class TestService {

    @Autowired
    UnitService unitService;
    @Autowired
    BookingService bookingService;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UnitRepository unitRepository;
    @Autowired
    BookingRepository bookingRepository;

    public void tearDown() {
        unitRepository.deleteAll();
    }

    public UnitDto saveUnitDto(boolean available) {
        UnitDto unitDto = UnitDto.builder()
                .rooms(1)
                .available(available)
                .accommodationType(AccommodationType.FLAT)
                .description("Flat description")
                .cost(150)
                .build();
        return unitService.create(unitDto);
    }

    public UnitDto saveUnitDto(Integer cost, boolean available) {
        UnitDto unitDto = UnitDto.builder()
                .rooms(1)
                .available(available)
                .accommodationType(AccommodationType.FLAT)
                .description("Flat description")
                .cost(cost)
                .build();
        return unitService.create(unitDto);
    }

    public UnitDto createUnitDto(boolean available) {
        return UnitDto.builder()
                .rooms(1)
                .available(available)
                .accommodationType(AccommodationType.FLAT)
                .description("Flat description")
                .cost(150)
                .build();
    }

    public BookingDto createBooking(UUID unitId, LocalDate startDate, LocalDate endDate, String email) {
        return unitRepository.findById(unitId).map(unit -> {
                    LocalDateTime start = LocalDateTime.of(startDate, LocalTime.MIN);
                    LocalDateTime end = LocalDateTime.of(endDate, LocalTime.MAX);
                    return bookingService.create(unit, start, end, email);
                })
                .orElseThrow();
    }

    public BookingEntity saveBooking(UUID unitId, BookingStatus bookingStatus, LocalDate startDate, LocalDate endDate, String email) {
        return unitRepository.findById(unitId).map(unit -> {
                    LocalDateTime start = LocalDateTime.of(startDate, LocalTime.MIN);
                    LocalDateTime end = LocalDateTime.of(endDate, LocalTime.MAX);
                    BookingEntity bookingEntity = new BookingEntity();
                    bookingEntity.setUnit(unit);
                    bookingEntity.setBookingStartDate(start);
                    bookingEntity.setBookingEndDate(end);
                    bookingEntity.setEmail(email);
                    bookingEntity.setStatus(bookingStatus);

                    return bookingRepository.save(bookingEntity);
                })
                .orElseThrow();
    }

    @SneakyThrows
    public <T> T response(ResultActions resultActions, Class<T> tClass) {
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(responseBody, tClass);
    }

    @SneakyThrows
    public <T> List<T> responseList(ResultActions resultActions, Class<T> tClass) {
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(
                responseBody, objectMapper.getTypeFactory().constructCollectionType(List.class, tClass));
    }
}
