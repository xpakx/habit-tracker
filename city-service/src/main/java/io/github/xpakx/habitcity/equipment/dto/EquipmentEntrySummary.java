package io.github.xpakx.habitcity.equipment.dto;

import org.springframework.beans.factory.annotation.Value;

public interface EquipmentEntrySummary {
    Long getId();
    Integer getAmount();

    @Value("#{target.building != null ? target.building.name : " +
            "(target.ship != null ? target.ship.name : " +
            "(target.resource != null ? target.resource.name : ''))}")
    String getName();
}
