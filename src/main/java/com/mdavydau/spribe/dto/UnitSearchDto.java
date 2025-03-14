package com.mdavydau.spribe.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;

@Data
@Builder
@Jacksonized
public class UnitSearchDto {
    @Builder.Default
    Integer minCost = 0;
    Integer maxCost;
    @Builder.Default
    LocalDate startDate = LocalDate.now();
    LocalDate endDate;
}
