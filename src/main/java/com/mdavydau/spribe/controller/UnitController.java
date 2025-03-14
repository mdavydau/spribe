package com.mdavydau.spribe.controller;

import com.mdavydau.spribe.dto.BookingDto;
import com.mdavydau.spribe.dto.BookingRequestDto;
import com.mdavydau.spribe.dto.UnitDto;
import com.mdavydau.spribe.dto.UnitSearchDto;
import com.mdavydau.spribe.service.UnitService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UnitController {

    private final UnitService unitService;

    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new unit")
    @PostMapping(value = "/units", produces = MediaType.APPLICATION_JSON_VALUE)
    public UnitDto create(@RequestBody UnitDto unitDto) {
        return unitService.create(unitDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update existing unit")
    @PutMapping(value = "/units/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UnitDto update(@PathVariable UUID id, @RequestBody UnitDto unitDto) {
        return unitService.update(id, unitDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Search available units")
    @PostMapping(value = "/units/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UnitDto> search(@RequestBody UnitSearchDto searchDto,
                                @ParameterObject Pageable pageable) {
        return unitService.search(searchDto, pageable);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Book available unit")
    @PostMapping(value = "/units/{id}/book", produces = MediaType.APPLICATION_JSON_VALUE)
    public BookingDto book(@PathVariable UUID id, @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        return unitService.book(id, bookingRequestDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Units available for booking (cached)")
    @GetMapping(value = "/units/count", produces = MediaType.APPLICATION_JSON_VALUE)
    public Integer countAllAvailableUnits() {
        return unitService.countAllAvailableUnits();
    }
}
