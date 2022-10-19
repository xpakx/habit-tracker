package io.github.xpakx.habitcity.shop;

import io.github.xpakx.habitcity.equipment.dto.AccountEvent;
import io.github.xpakx.habitcity.shop.dto.BuyRequest;
import io.github.xpakx.habitcity.shop.dto.ItemResponse;
import io.github.xpakx.habitcity.shop.dto.ShopResponse;

import java.util.List;

public interface ShopService {
    void refreshShops();
    ItemResponse buy(BuyRequest request, Long shopEntryId, Long userId);
    ShopResponse getShop(Long shopId, Long userId);
    void addUserShop(AccountEvent event);
    List<Shop> getShops(Long userId);
}
