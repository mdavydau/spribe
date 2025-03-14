package com.mdavydau.spribe.controller;

import com.mdavydau.spribe.config.BaseSpringBootTestConfig;
import com.mdavydau.spribe.dto.BookingDto;
import com.mdavydau.spribe.dto.UnitDto;
import com.mdavydau.spribe.entity.BookingStatus;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BookingControllerTest extends BaseSpringBootTestConfig {

    @AfterEach
    void tearDown() {
        testService.tearDown();
    }

    @SneakyThrows
    @Test
    @DisplayName("Should cancel booking")
    void bookingCancellation() {
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plusDays(2);
        String email = "test@test.com";
        UnitDto unitDto = testService.saveUnitDto(true);
        BookingDto bookingDto = testService.createBooking(unitDto.getId(), start, end, email);

        ResultActions resultActions =
                mockMvc.perform(
                                post("/bookings/{id}/cancellation", bookingDto.getId())
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print());

        BookingDto response = testService.response(resultActions, BookingDto.class);

        assertThat(response)
                .as("Unit cancelled")
                .extracting(BookingDto::getUnitId, BookingDto::getStatus)
                .contains(unitDto.getId(), BookingStatus.CANCELLED);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should not find booking cancellation")
    void bookingNotFindBookingForCancellation() {
        ResultActions resultActions =
                mockMvc.perform(
                                post("/bookings/{id}/cancellation", UUID.randomUUID())
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print());

        resultActions
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    @DisplayName("Should confirm booking")
    void bookingConfirmation() {
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plusDays(2);
        String email = "test@test.com";
        UnitDto unitDto = testService.saveUnitDto(true);
        BookingDto bookingDto = testService.createBooking(unitDto.getId(), start, end, email);
        ResultActions resultActions =
                mockMvc.perform(
                                post("/bookings/{id}/confirmation", bookingDto.getId())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(unitDto)))
                        .andDo(print());

        BookingDto response = testService.response(resultActions, BookingDto.class);

        assertThat(response)
                .as("Unit confirmed")
                .extracting(BookingDto::getUnitId, BookingDto::getStatus)
                .contains(unitDto.getId(), BookingStatus.CONFIRMED);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should not find booking confirmation")
    void bookingNotFindBookingForConfirmation() {
        ResultActions resultActions =
                mockMvc.perform(
                                post("/bookings/{id}/confirmation", UUID.randomUUID())
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print());

        resultActions
                .andExpect(status().isNotFound());
    }
}