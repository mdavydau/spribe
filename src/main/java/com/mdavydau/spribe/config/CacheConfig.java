package com.mdavydau.spribe.config;

import com.mdavydau.spribe.service.UnitCacheService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableCaching
@RequiredArgsConstructor
public class CacheConfig {

    private final UnitCacheService unitCacheService;

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("available-units");
    }

    @PostConstruct
    public void warmCache() {
        log.info("available-units cache updated to {}", unitCacheService.updateAllAvailableUnits());
    }
}
