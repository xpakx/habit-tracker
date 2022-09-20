package io.github.xpakx.habitcity.city;

import io.github.xpakx.habitcity.building.BuildingRecipeElemRepository;
import io.github.xpakx.habitcity.building.dto.CraftBuildingElem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuildingRecipeServiceImpl implements BuildingRecipeService {
    private final BuildingRecipeElemRepository recipeRepository;

    @Override
    public List<CraftBuildingElem> getRecipe(Long buildingId) {
        return recipeRepository.findByBuildingId(buildingId);
    }
}
