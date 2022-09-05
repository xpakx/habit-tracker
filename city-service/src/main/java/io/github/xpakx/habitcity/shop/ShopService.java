package io.github.xpakx.habitcity.shop;

import io.github.xpakx.habitcity.shop.dto.ItemResponse;

public interface ShopService {
    void refreshShops();
    ItemResponse buy(Long shopEntryId, Long userId);
}
