package io.github.xpakx.habitcity.city;

import io.github.xpakx.habitcity.building.dto.CraftBuildingElem;

import java.util.List;

public interface BuildingRecipeService {
    List<CraftBuildingElem> getRecipe(Long buildingId);
}
