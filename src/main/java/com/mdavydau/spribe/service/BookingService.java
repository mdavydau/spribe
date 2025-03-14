package com.mdavydau.spribe.service;

import com.mdavydau.spribe.dto.BookingDto;
import com.mdavydau.spribe.entity.BookingEntity;
import com.mdavydau.spribe.entity.BookingStatus;
import com.mdavydau.spribe.entity.UnitEntity;
import com.mdavydau.spribe.mapper.BookingMapper;
import com.mdavydau.spribe.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    public BookingDto create(UnitEntity unit, LocalDateTime start, LocalDateTime end, String email) {
        BookingEntity bookingEntity = new BookingEntity();
        bookingEntity.setUnit(unit);
        bookingEntity.setBookingStartDate(start);
        bookingEntity.setBookingEndDate(end);
        bookingEntity.setEmail(email);
        bookingEntity.setStatus(BookingStatus.PENDING);

        BookingEntity save = bookingRepository.save(bookingEntity);
        return bookingMapper.toDto(save);
    }

    public BookingDto bookingCancellation(UUID id) {
        return bookingRepository.findByIdAndStatusIn(id, Set.of(BookingStatus.PENDING, BookingStatus.CONFIRMED))
                .map(bookingEntity -> {
                    bookingEntity.setStatus(BookingStatus.CANCELLED);
                    return bookingEntity;
                })
                .map(bookingRepository::save)
                .map(bookingMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    public BookingDto bookingConfirmation(UUID id) {
        return bookingRepository.findByIdAndStatusIn(id, Set.of(BookingStatus.PENDING))
                .map(bookingEntity -> {
                    bookingEntity.setStatus(BookingStatus.CONFIRMED);
                    return bookingEntity;
                })
                .map(bookingRepository::save)
                .map(bookingMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    public List<BookingDto> findAllBooked(LocalDateTime startDate, LocalDateTime endDate) {
        return bookingRepository.findAllBooked(startDate, endDate).stream()
                .map(bookingMapper::toDto)
                .toList();
    }
}
