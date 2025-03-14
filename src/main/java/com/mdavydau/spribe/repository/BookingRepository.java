package com.mdavydau.spribe.repository;

import com.mdavydau.spribe.entity.BookingEntity;
import com.mdavydau.spribe.entity.BookingStatus;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface BookingRepository extends CrudRepository<BookingEntity, UUID> {

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<BookingEntity> findAllByStatusAndCreatedIsBefore(BookingStatus status, LocalDateTime createdAfter);

    @Query(value = """
            select b from BookingEntity b where
                        ((b.bookingStartDate >= :startDate AND b.bookingStartDate < :endDate)
                        or (b.bookingEndDate > :startDate and b.bookingEndDate <= :endDate)
                        or (b.bookingStartDate <= :startDate and b.bookingEndDate >= :endDate))
            and b.status not in ('CANCELLED', 'PAYMENT_TIMEOUT')
            order by b.status asc
            """)
    List<BookingEntity> findAllBooked(LocalDateTime startDate, LocalDateTime endDate);

    @Transactional
    @Modifying
    @Query(value = "update BookingEntity b set b.created = :created where b.id = :id")
    Integer updateBookingCreated(UUID id, LocalDateTime created);

    Optional<BookingEntity> findByIdAndStatusIn(UUID id, Set<BookingStatus> statusSet);
}
