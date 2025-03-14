package com.mdavydau.spribe.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;

@Data
@Builder
@Jacksonized
public class BookingRequestDto {
    @Future
    @NotNull
    LocalDate startDate;
    @Future
    @NotNull
    LocalDate endDate;
    @Email
    String email;
}
