package com.mdavydau.spribe.config;

import com.mdavydau.spribe.utils.UnitRandomUtil;
import com.mdavydau.spribe.repository.UnitRepository;
import com.mdavydau.spribe.service.UnitCacheService;
import com.mdavydau.spribe.service.UnitRandomService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Slf4j
@Profile("local")
@Configuration
@RequiredArgsConstructor
public class InitConfig {

    private final UnitRandomService unitRandomService;
    private final UnitCacheService unitCacheService;
    private final UnitRepository unitRepository;

    @PostConstruct
    public void init() {
        UnitRandomUtil.initAllDescriptions();
//        unitRepository.deleteAll();
        unitRandomService.initRandomUnits(90);
        log.info("available-units cache updated to {}", unitCacheService.updateAllAvailableUnits());
    }

}
