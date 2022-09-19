package io.github.xpakx.habitcity.crafting;

import io.github.xpakx.habitcity.crafting.dto.CraftRequest;
import io.github.xpakx.habitcity.crafting.error.NoSuchRecipeException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {
    private final RecipeRepository recipeRepository;

    @Override
    @Cacheable(cacheNames = "recipes", key = "T(io.github.xpakx.habitcity.crafting.RecipeServiceImpl).getCacheKey(#request)")
    public Recipe getRecipe(CraftRequest request) {
        return recipeRepository.getRecipeByResources(
                request.getElem1().getId(), request.getElem2().getId(), request.getElem3().getId(),
                request.getElem4().getId(), request.getElem5().getId(), request.getElem6().getId(),
                request.getElem7().getId(), request.getElem8().getId(), request.getElem9().getId()
        ).orElseThrow(NoSuchRecipeException::new);
    }

    public static String getCacheKey(CraftRequest request){
        return "recipe_" + request.getElem1().getId() + '_' +
                request.getElem2().getId() + '_' +
                request.getElem3().getId() + '_' +
                request.getElem4().getId() + '_' +
                request.getElem5().getId() + '_' +
                request.getElem6().getId() + '_' +
                request.getElem7().getId() + '_' +
                request.getElem8().getId() + '_' +
                request.getElem9().getId();
    }
}
