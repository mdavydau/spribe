package com.mdavydau.spribe.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "booking")
public class BookingEntity {
    @Id
    @Generated
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "unit_id")
    private UnitEntity unit;

    private String email;
    private LocalDateTime bookingStartDate;
    private LocalDateTime bookingEndDate;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime created;
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updated;

}
