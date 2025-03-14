package com.mdavydau.spribe.dto;

import com.mdavydau.spribe.entity.AccommodationType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UnitDto {
    private UUID id;
    @NotNull
    private Integer rooms;
    @NotNull
    private AccommodationType accommodationType;
    @Positive
    @NotNull
    private Integer cost;
    private String description;
    private Boolean available;
}
