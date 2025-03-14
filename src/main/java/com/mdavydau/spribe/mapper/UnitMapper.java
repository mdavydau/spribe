package com.mdavydau.spribe.mapper;

import com.mdavydau.spribe.dto.UnitDto;
import com.mdavydau.spribe.entity.UnitEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class UnitMapper {
    public abstract UnitDto toDto(UnitEntity source);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "updated", ignore = true)
    public abstract UnitEntity toEntity(UnitDto source);
}
