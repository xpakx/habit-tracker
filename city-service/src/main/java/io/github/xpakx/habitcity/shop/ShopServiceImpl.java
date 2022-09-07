package io.github.xpakx.habitcity.shop;

import io.github.xpakx.habitcity.equipment.EquipmentEntry;
import io.github.xpakx.habitcity.equipment.EquipmentEntryRepository;
import io.github.xpakx.habitcity.equipment.UserEquipmentRepository;
import io.github.xpakx.habitcity.resource.Resource;
import io.github.xpakx.habitcity.resource.ResourceRepository;
import io.github.xpakx.habitcity.shop.dto.BuyRequest;
import io.github.xpakx.habitcity.shop.dto.ItemResponse;
import io.github.xpakx.habitcity.shop.error.ShopItemEmptyException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {
    private final ShopRepository shopRepository;
    private final ShopEntryRepository entryRepository;
    private final ResourceRepository resourceRepository;
    private final EquipmentEntryRepository equipmentEntryRepository;
    private final UserEquipmentRepository equipmentRepository;

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
                    .map(a -> toShopEntry(a, shop, random.nextFloat()))
                    .toList();
            entriesToAdd.addAll(entries);
        }
        entryRepository.saveAll(entriesToAdd);
    }

    private ShopEntry toShopEntry(Resource a, Shop shop, float maxAmountPercent) {
        int amount = (int) (a.getMaxStock()*maxAmountPercent);
        ShopEntry entry = new ShopEntry();
        entry.setShop(shop);
        entry.setResource(a);
        entry.setAmount( amount > 0 ? amount : 1);
        return entry;
    }

    @Override
    @Transactional
    public ItemResponse buy(BuyRequest request, Long shopEntryId, Long userId) {
        ShopEntry entry = entryRepository.findById(shopEntryId)
                .orElseThrow();
        if(entry.getAmount() - request.getAmount() <= 0) {
            throw new ShopItemEmptyException();
        }

        entry.setAmount(entry.getAmount()-request.getAmount());
        entryRepository.save(entry);

        EquipmentEntry eqEntry = new EquipmentEntry();
        eqEntry.setAmount(request.getAmount());
        eqEntry.setResource(entry.getResource());
        eqEntry.setBuilding(entry.getBuilding());
        eqEntry.setShip(entry.getShip());
        eqEntry.setEquipment(equipmentRepository.getByUserId(userId).orElseThrow());
        equipmentEntryRepository.save(eqEntry);

        ItemResponse response = new ItemResponse();
        response.setAmount(request.getAmount());
        response.setName(eqEntry.getResource() != null ? eqEntry.getResource().getName() : "");

        return response;
    }
}
