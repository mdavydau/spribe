package com.mdavydau.spribe.service;

import com.mdavydau.spribe.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UnitCacheService {

    private final UnitRepository unitRepository;

    @Cacheable("available-units")
    public Integer countAllAvailableUnits() {
        return unitRepository.findAllByAvailableTrue().size();
    }

    @CachePut("available-units")
    public Integer updateAllAvailableUnits() {
        return unitRepository.findAllByAvailableTrue().size();
    }
}
