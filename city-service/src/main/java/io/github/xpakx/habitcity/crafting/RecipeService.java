package io.github.xpakx.habitcity.crafting;

import io.github.xpakx.habitcity.crafting.dto.CraftRequest;

public interface RecipeService {
    Recipe getRecipe(CraftRequest request);
}
