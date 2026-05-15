package io.github.xpakx.habitcity.city;

import io.github.xpakx.habitcity.building.BuildingRecipeElem;

import java.util.List;

public interface BuildingRecipeService {
    List<BuildingRecipeElem> getRecipe(Long buildingId);
}
