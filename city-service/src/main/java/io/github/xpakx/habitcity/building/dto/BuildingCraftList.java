package io.github.xpakx.habitcity.building.dto;

import io.github.xpakx.habitcity.equipment.dto.CraftElem;
import io.github.xpakx.habitcity.equipment.dto.CraftList;


import java.util.List;

public class BuildingCraftList implements CraftList {
    private final Integer amount;
    private final List<CraftBuildingElem> craftList;

    public BuildingCraftList(Integer amount, List<CraftBuildingElem> craftList) {
        this.amount = amount;
        this.craftList = craftList;
    }

    @Override
    public List<CraftElem> asCraftList() {
        return craftList.stream()
                .map(this::toCraftElem)
                .toList();
    }

    private CraftElem toCraftElem(CraftBuildingElem a) {
        return a;
    }

    @Override
    public Integer getAmount() {
        return amount;
    }
}
