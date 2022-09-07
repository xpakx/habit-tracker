package io.github.xpakx.habitcity.shop;

import io.github.xpakx.habitcity.shop.dto.BuyRequest;
import io.github.xpakx.habitcity.shop.dto.ItemResponse;

public interface ShopService {
    void refreshShops();
    ItemResponse buy(BuyRequest reqquest, Long shopEntryId, Long userId);
}
