package io.github.xpakx.habitcity.crafting;

import io.github.xpakx.habitcity.crafting.dto.CraftRequest;
import io.github.xpakx.habitcity.shop.dto.ItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {
    private final RecipeRepository recipeRepository;

    @Override
    public ItemResponse craft(CraftRequest request, Long userId) {
        Recipe recipe = recipeRepository.getRecipeByResources(
                request.getElem1().getId(), request.getElem2().getId(), request.getElem3().getId(),
                request.getElem4().getId(), request.getElem5().getId(), request.getElem6().getId(),
                request.getElem7().getId(), request.getElem8().getId(), request.getElem9().getId()
        ).orElseThrow();

        ItemResponse response = new ItemResponse();
        response.setAmount(1);
        response.setName(recipe.getBuilding() != null ? recipe.getBuilding().getName() :
                (recipe.getShip() != null ? recipe.getShip().getName() : recipe.getResource().getName()));
        return response;
    }
}
