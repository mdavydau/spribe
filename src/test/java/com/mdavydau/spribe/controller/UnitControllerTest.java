package com.mdavydau.spribe.controller;

import com.mdavydau.spribe.config.BaseSpringBootTestConfig;
import com.mdavydau.spribe.dto.BookingDto;
import com.mdavydau.spribe.dto.BookingRequestDto;
import com.mdavydau.spribe.dto.UnitDto;
import com.mdavydau.spribe.dto.UnitSearchDto;
import com.mdavydau.spribe.entity.BookingStatus;
import com.mdavydau.spribe.utils.UnitUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UnitControllerTest extends BaseSpringBootTestConfig {

    @AfterEach
    void tearDown() {
        testService.tearDown();
    }

    @SneakyThrows
    @Test
    @DisplayName("Should create new unit")
    void create() {
        UnitDto unitDto = testService.createUnitDto(true);

        ResultActions resultActions =
                mockMvc.perform(
                                post("/units")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(unitDto)))
                        .andDo(print());

        UnitDto response = testService.response(resultActions, UnitDto.class);

        assertThat(response)
                .as("Unit created")
                .extracting(UnitDto::getRooms, UnitDto::getAvailable, UnitDto::getAccommodationType, UnitDto::getDescription, UnitDto::getCost)
                .contains(unitDto.getRooms(), unitDto.getAvailable(), unitDto.getAccommodationType(), unitDto.getDescription(), UnitUtils.addCostSystemMarkup(unitDto.getCost()));
    }

    @SneakyThrows
    @Test
    @DisplayName("Should display all available units based on cost and dates")
    void search() {
        testService.saveUnitDto(true);
        testService.saveUnitDto(false);

        UnitSearchDto unitSearchDto = UnitSearchDto.builder().build();

        ResultActions resultActions =
                mockMvc.perform(
                                post("/units/search")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(unitSearchDto)))
                        .andDo(print());

        List<UnitDto> response = testService.responseList(resultActions, UnitDto.class);

        assertThat(response)
                .as("All available units based on cost and dates")
                .hasSize(1);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should display all available units based on cost")
    void searchByCost() {
        testService.saveUnitDto(100, true);
        testService.saveUnitDto(200, true);
        testService.saveUnitDto(300, true);
        testService.saveUnitDto(false);

        UnitSearchDto unitSearchDto = UnitSearchDto.builder()
                .minCost(50)
                .maxCost(250)
                .build();

        ResultActions resultActions =
                mockMvc.perform(
                                post("/units/search")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(unitSearchDto)))
                        .andDo(print());

        List<UnitDto> response = testService.responseList(resultActions, UnitDto.class);

        assertThat(response)
                .as("All available units based on cost and dates")
                .hasSize(2);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should display all available units based on dates")
    void searchByDates() {
        String email = "test@test.com";
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plusDays(1);
        testService.saveUnitDto(100, true);
        UnitDto unitDto = testService.saveUnitDto(200, true);
        testService.createBooking(unitDto.getId(), start, end, email);
        testService.saveUnitDto(300, true);
        testService.saveUnitDto(false);

        UnitSearchDto unitSearchDto = UnitSearchDto.builder()
                .minCost(50)
                .maxCost(250)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .build();

        ResultActions resultActions =
                mockMvc.perform(
                                post("/units/search")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(unitSearchDto)))
                        .andDo(print());

        List<UnitDto> response = testService.responseList(resultActions, UnitDto.class);

        assertThat(response)
                .as("All available units based on cost and dates")
                .hasSize(1);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should display all available units based on cost and dates and booked status")
    void searchBooked() {
        LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(LocalDate.now().plusDays(2), LocalTime.MAX);
        String email = "test@test.com";
        UnitDto unitDto1 = testService.saveUnitDto(true);
        testService.saveUnitDto(false);
        testService.saveBooking(unitDto1.getId(), BookingStatus.PENDING, start.toLocalDate(), end.toLocalDate(), email);

        UnitSearchDto unitSearchDto = UnitSearchDto.builder().build();

        ResultActions resultActions =
                mockMvc.perform(
                                post("/units/search")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(unitSearchDto)))
                        .andDo(print());

        List<UnitDto> response = testService.responseList(resultActions, UnitDto.class);

        assertThat(response)
                .as("All available units based on cost and dates")
                .hasSize(1);
    }

    @SneakyThrows
    @Test
    void countAllAvailableUnits() {
        testService.saveUnitDto(true);
        testService.saveUnitDto(true);

        ResultActions resultActions =
                mockMvc.perform(get("/units/count")
                                .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print());

        Integer response = testService.response(resultActions, Integer.class);

        assertThat(response).isEqualTo(2);

        testService.saveUnitDto(true);
        testService.saveUnitDto(false);

        ResultActions resultActions2 =
                mockMvc.perform(get("/units/count")
                                .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print());

        Integer response2 = testService.response(resultActions2, Integer.class);

        assertThat(response2).isEqualTo(3);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should update existed unit availability")
    void updateAvailability() {
        UnitDto unitDto = testService.saveUnitDto(true);
        Integer cost = unitDto.getCost();
        unitDto.setAvailable(false);
        unitDto.setCost(null);

        ResultActions resultActions =
                mockMvc.perform(
                                put("/units/{id}", unitDto.getId())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(unitDto)))
                        .andDo(print());

        UnitDto response = testService.response(resultActions, UnitDto.class);

        assertThat(response)
                .as("Unit created")
                .extracting(UnitDto::getRooms, UnitDto::getAvailable, UnitDto::getAccommodationType, UnitDto::getDescription, UnitDto::getCost)
                .contains(unitDto.getRooms(), unitDto.getAvailable(), unitDto.getAccommodationType(), unitDto.getDescription(), cost);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should book available unit")
    void book() {
        UnitDto unitDto = testService.saveUnitDto(true);

        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = start.plusDays(2);
        String email = "test@test.com";

        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .startDate(start)
                .endDate(end)
                .email(email)
                .build();

        ResultActions resultActions =
                mockMvc.perform(
                                post("/units/{id}/book", unitDto.getId())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                        .andDo(print());

        BookingDto response = testService.response(resultActions, BookingDto.class);

        assertThat(response)
                .as("Booking")
                .extracting(BookingDto::getUnitId, BookingDto::getBookingStartDate, BookingDto::getBookingEndDate, BookingDto::getEmail)
                .contains(unitDto.getId(), start, end, email);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should not book un-available unit")
    void bookUnavailable() {
        UnitDto unitDto = testService.saveUnitDto(true);

        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = start.plusDays(2);
        String email = "test@test.com";

        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .startDate(start)
                .endDate(end)
                .email(email)
                .build();

        ResultActions resultActions =
                mockMvc.perform(
                                post("/units/{id}/book", unitDto.getId())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                        .andDo(print());

        BookingDto response = testService.response(resultActions, BookingDto.class);

        assertThat(response)
                .as("Booking")
                .extracting(BookingDto::getUnitId, BookingDto::getBookingStartDate, BookingDto::getBookingEndDate, BookingDto::getEmail)
                .contains(unitDto.getId(), start, end, email);

        ResultActions resultActions2 =
                mockMvc.perform(
                                post("/units/{id}/book", unitDto.getId())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                        .andDo(print());

        resultActions2
                .andExpect(status().isBadRequest());
    }
}