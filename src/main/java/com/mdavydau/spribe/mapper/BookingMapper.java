package com.mdavydau.spribe.mapper;

import com.mdavydau.spribe.dto.BookingDto;
import com.mdavydau.spribe.entity.BookingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class BookingMapper {
    @Mapping(source = "unit.id", target = "unitId")
    public abstract BookingDto toDto(BookingEntity source);
}
