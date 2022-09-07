package io.github.xpakx.habitcity.shop;

import io.github.xpakx.habitcity.equipment.EquipmentEntry;
import io.github.xpakx.habitcity.equipment.EquipmentEntryRepository;
import io.github.xpakx.habitcity.equipment.UserEquipment;
import io.github.xpakx.habitcity.equipment.UserEquipmentRepository;
import io.github.xpakx.habitcity.money.Money;
import io.github.xpakx.habitcity.money.MoneyRepository;
import io.github.xpakx.habitcity.resource.Resource;
import io.github.xpakx.habitcity.resource.ResourceRepository;
import io.github.xpakx.habitcity.shop.dto.BuyRequest;
import io.github.xpakx.habitcity.shop.dto.ItemResponse;
import io.github.xpakx.habitcity.shop.error.NotEnoughMoneyException;
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
    private final MoneyRepository moneyRepository;

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
        entry.setPrice(a.getBaseCost());
        return entry;
    }

    @Override
    @Transactional
    public ItemResponse buy(BuyRequest request, Long shopEntryId, Long userId) {
        ShopEntry entry = getShopEntry(request, shopEntryId);
        UserEquipment eq = equipmentRepository.getByUserId(userId).orElseThrow();
        testEqSpace(eq, entry);
        exchangeMoney(entry, userId, entry.getAmount());
        entry.setAmount(entry.getAmount()-request.getAmount());
        entryRepository.save(entry);
        EquipmentEntry eqEntry = createEquipmentEntry(request, eq, entry);
        equipmentEntryRepository.save(eqEntry);
        return createItemResponse(request, eqEntry);
    }

    private void testEqSpace(UserEquipment eq, ShopEntry entry) {
        // TODO
    }

    private void exchangeMoney(ShopEntry entry, Long userId, int amount) {
        Money money = moneyRepository.findByUserId(userId).orElseThrow();
        int price = entry.getPrice()*amount;
        if(money.getAmount()-price < 0) {
            throw new NotEnoughMoneyException();
        }
        money.setAmount(money.getAmount()-price);
        moneyRepository.save(money);
    }

    private ItemResponse createItemResponse(BuyRequest request, EquipmentEntry eqEntry) {
        ItemResponse response = new ItemResponse();
        response.setAmount(request.getAmount());
        response.setName(getItemName(eqEntry));
        return response;
    }

    private String getItemName(EquipmentEntry eqEntry) {
        if(eqEntry.getResource() != null) {
            return eqEntry.getResource().getName();
        }
        if(eqEntry.getBuilding() != null) {
            return eqEntry.getBuilding().getName();
        }
        if(eqEntry.getShip() != null) {
            return eqEntry.getShip().getName();
        }
        return "";
    }

    private EquipmentEntry createEquipmentEntry(BuyRequest request, UserEquipment eq, ShopEntry entry) {
        EquipmentEntry eqEntry = new EquipmentEntry();
        eqEntry.setAmount(request.getAmount());
        eqEntry.setResource(entry.getResource());
        eqEntry.setBuilding(entry.getBuilding());
        eqEntry.setShip(entry.getShip());
        eqEntry.setEquipment(eq);
        return eqEntry;
    }

    private ShopEntry getShopEntry(BuyRequest request, Long shopEntryId) {
        ShopEntry entry = entryRepository.findById(shopEntryId)
                .orElseThrow();
        if(entry.getAmount() - request.getAmount() < 0) {
            throw new ShopItemEmptyException();
        }
        return entry;
    }
}
