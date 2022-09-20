package io.github.xpakx.habitcity.city;

import io.github.xpakx.habitcity.building.BuildingRecipeElemRepository;
import io.github.xpakx.habitcity.building.dto.CraftBuildingElem;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuildingRecipeServiceImpl implements BuildingRecipeService {
    private final BuildingRecipeElemRepository recipeRepository;

    @Override
    @Cacheable(cacheNames = "b_recipes", key = "'b_recipe_'.concat(#buildingId)")
    public List<CraftBuildingElem> getRecipe(Long buildingId) {
        return recipeRepository.findByBuildingId(buildingId);
    }
}
