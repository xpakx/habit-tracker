package io.github.xpakx.habitcity.shop;

import io.github.xpakx.habitcity.shop.dto.ItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {
    private final ShopRepository shopRepository;
    private final ShopEntryRepository entryRepository;

    @Override
    @Scheduled(cron = "0 0 * * * *")
    public void refreshShops() {
        Random random = new Random();
        entryRepository.deleteAll();
        List<Shop> shops = shopRepository.findAll();
        for(Shop shop : shops) {
            int shopSize = random.nextInt(shop.getMaxSize()-1) + 1;
            int rarity = shop.getMaxRarity();


        }

    }

    @Override
    public ItemResponse buy(Long shopEntryId, Long userId) {
        return null;
    }
}
