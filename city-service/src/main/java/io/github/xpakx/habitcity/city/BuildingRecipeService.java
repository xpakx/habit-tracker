package io.github.xpakx.habitcity.city;

import io.github.xpakx.habitcity.building.BuildingRecipeElem;
import io.github.xpakx.habitcity.building.dto.CraftBuildingElem;

import java.util.List;

public interface BuildingRecipeService {
    List<BuildingRecipeElem> getRecipe(Long buildingId);
}
