package com.mdavydau.spribe.repository;

import com.mdavydau.spribe.entity.UnitEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface UnitRepository extends CrudRepository<UnitEntity, UUID> {
    List<UnitEntity> findAllByAvailableTrue();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<UnitEntity> findByIdAndAvailableTrue(UUID id);

    @Query(value = """
            select u
            from UnitEntity u
            where u.cost >= :costMin
              and (:costMax is null
                or u.cost <= :costMax)
              and u.available is true
              and u.id not in (:excludedUnits)
            """)
    List<UnitEntity> search(Integer costMin, Integer costMax, Set<UUID> excludedUnits, Pageable pageable);
}
