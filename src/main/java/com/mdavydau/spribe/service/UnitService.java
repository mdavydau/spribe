package com.mdavydau.spribe.service;

import com.mdavydau.spribe.dto.BookingDto;
import com.mdavydau.spribe.dto.BookingRequestDto;
import com.mdavydau.spribe.dto.UnitDto;
import com.mdavydau.spribe.dto.UnitSearchDto;
import com.mdavydau.spribe.entity.UnitEntity;
import com.mdavydau.spribe.mapper.UnitMapper;
import com.mdavydau.spribe.repository.UnitRepository;
import com.mdavydau.spribe.utils.UnitUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UnitService {

    private final BookingService bookingService;
    private final UnitRepository unitRepository;
    private final UnitCacheService unitCacheService;
    private final UnitMapper unitMapper;

    public UnitDto create(UnitDto unitDto) {
        UnitEntity entity = unitMapper.toEntity(unitDto);
        Integer costWithSystemMarkup = UnitUtils.addCostSystemMarkup(entity.getCost());
        entity.setCost(costWithSystemMarkup);
        UnitEntity save = unitRepository.save(entity);
        log.info("available-units cache updated to {}", unitCacheService.updateAllAvailableUnits());
        return unitMapper.toDto(save);
    }

    public UnitDto update(UUID id, UnitDto unitDto) {
        return unitRepository.findById(id)
                .map(unitEntity -> {
                    if (unitDto.getRooms() != null) {
                        unitEntity.setRooms(unitDto.getRooms());
                    }

                    if (unitDto.getDescription() != null) {
                        unitEntity.setDescription(unitDto.getDescription());
                    }

                    if (unitDto.getAccommodationType() != null) {
                        unitEntity.setAccommodationType(unitDto.getAccommodationType());
                    }

                    if (unitDto.getCost() != null) {
                        Integer costWithSystemMarkup = UnitUtils.addCostSystemMarkup(unitDto.getCost());
                        unitEntity.setCost(costWithSystemMarkup);
                    }

                    if (unitDto.getAvailable() != null) {
                        unitEntity.setAvailable(unitDto.getAvailable());
                    }

                    return unitEntity;
                })
                .map(unitRepository::save)
                .map(unitEntity -> {
                    log.info("available-units cache updated to {}", unitCacheService.updateAllAvailableUnits());
                    return unitEntity;
                })
                .map(unitMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Unit not found"));
    }

    public Integer countAllAvailableUnits() {
        return unitCacheService.countAllAvailableUnits();
    }

    public List<UnitDto> search(UnitSearchDto searchDto, Pageable pageable) {
        LocalDateTime start = LocalDateTime.of(searchDto.getStartDate(), LocalTime.MIN);

        LocalDateTime end = null;
        if (searchDto.getEndDate() != null) {
            end = LocalDateTime.of(searchDto.getEndDate(), LocalTime.MAX);
        }

        Set<UUID> alreadyBookedUnits = bookingService.findAllBooked(start, end)
                .stream().map(BookingDto::getUnitId)
                .collect(Collectors.toSet());
        return unitRepository.search(searchDto.getMinCost(), searchDto.getMaxCost(), alreadyBookedUnits, pageable)
                .stream().map(unitMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingDto book(UUID id, BookingRequestDto bookingRequestDto) {
        if (bookingRequestDto.getStartDate().isAfter(bookingRequestDto.getEndDate())) {
            throw new RuntimeException("Invalid booking start and end date");
        }

        LocalDateTime start = LocalDateTime.of(bookingRequestDto.getStartDate(), LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(bookingRequestDto.getEndDate(), LocalTime.MAX);

        return unitRepository.findByIdAndAvailableTrue(id)
                .map(unitEntity -> {
                    validateBookingDateRange(unitEntity.getId(), start, end);
                    return unitEntity;
                })
                .map(unit -> bookingService.create(unit, start, end, bookingRequestDto.getEmail()))
                .orElseThrow(() -> new RuntimeException("Unit not found"));
    }

    protected void validateBookingDateRange(UUID unitId, LocalDateTime start, LocalDateTime end) {
        if (bookingService.findAllBooked(start, end)
                .stream().map(BookingDto::getUnitId)
                .anyMatch(unitId::equals)) {
            throw new RuntimeException(String.format("Current unit %s is already booked for provided start %s end %s dates", unitId, start, end));
        }
    }
}
