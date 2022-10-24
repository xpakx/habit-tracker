package io.github.xpakx.habitcity.building.dto;

import io.github.xpakx.habitcity.equipment.dto.CraftElem;
import org.springframework.beans.factory.annotation.Value;

public interface CraftBuildingElem extends CraftElem {
    @Value("#{target.resource.id}")
    Long getId();
    Integer getAmount();
}
