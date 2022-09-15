package io.github.xpakx.habitcity.crafting;

import io.github.xpakx.habitcity.crafting.dto.CraftRequest;
import io.github.xpakx.habitcity.shop.dto.ItemResponse;

public interface RecipeService {
    ItemResponse craft(CraftRequest request, Long userId);
}
