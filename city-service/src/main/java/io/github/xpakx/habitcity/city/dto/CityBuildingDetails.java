package io.github.xpakx.habitcity.city.dto;

import org.springframework.beans.factory.annotation.Value;

public interface CityBuildingDetails {
    Long getId();

    @Value("#{target.building.name}")
    String getName();

    @Value("#{target.building.code}")
    String getCode();

    @Value("#{target.building.id}")
    Long getBuildingId();
}
