package com.mdavydau.spribe.dto;

import com.mdavydau.spribe.entity.BookingStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@Jacksonized
public class BookingDto {
    UUID id;
    @NotNull
    UUID unitId;
    @Future
    @NotNull
    LocalDate bookingStartDate;
    @Future
    @NotNull
    LocalDate bookingEndDate;
    @Email
    String email;
    BookingStatus status;
}
