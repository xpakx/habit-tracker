package io.github.xpakx.habitcity.building.dto;

import io.github.xpakx.habitcity.building.BuildingRecipeElem;
import io.github.xpakx.habitcity.equipment.dto.CraftElem;
import lombok.Getter;

@Getter
public class CraftBuildingElem implements CraftElem {
    private Long id;
    private Integer amount;

    public CraftBuildingElem(BuildingRecipeElem elem) {
        this.id = elem.getResource().getId();
        this.amount = elem.getAmount();
    }
}
