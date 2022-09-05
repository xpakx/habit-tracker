package io.github.xpakx.habitcity.shop;

import io.github.xpakx.habitcity.resource.Resource;
import io.github.xpakx.habitcity.resource.ResourceRepository;
import io.github.xpakx.habitcity.shop.dto.ItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {
    private final ShopRepository shopRepository;
    private final ShopEntryRepository entryRepository;
    private final ResourceRepository resourceRepository;

    @Override
    @Scheduled(cron = "0 * * * * *")
    public void refreshShops() {
        Random random = new Random();
        entryRepository.deleteAll();
        List<Shop> shops = shopRepository.findAll();
        List<ShopEntry> entriesToAdd = new ArrayList<>();
        for(Shop shop : shops) {
            int shopSize = random.nextInt(shop.getMaxSize()-1) + 1;
            int rarity = shop.getMaxRarity();
            List<ShopEntry> entries = resourceRepository
                    .findRandomResources(shopSize, rarity)
                    .stream()
                    .map(a -> toShopEntry(a, shop))
                    .toList();
            entriesToAdd.addAll(entries);
        }
        entryRepository.saveAll(entriesToAdd);
    }

    private ShopEntry toShopEntry(Resource a, Shop shop) {
        ShopEntry entry = new ShopEntry();
        entry.setShop(shop);
        entry.setResource(a);
        entry.setAmount(1);
        return entry;
    }

    @Override
    public ItemResponse buy(Long shopEntryId, Long userId) {
        return null;
    }
}
