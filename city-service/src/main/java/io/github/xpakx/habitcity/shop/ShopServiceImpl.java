package io.github.xpakx.habitcity.shop;

import io.github.xpakx.habitcity.equipment.EquipmentEntry;
import io.github.xpakx.habitcity.equipment.EquipmentEntryRepository;
import io.github.xpakx.habitcity.equipment.UserEquipment;
import io.github.xpakx.habitcity.equipment.UserEquipmentRepository;
import io.github.xpakx.habitcity.equipment.dto.AccountEvent;
import io.github.xpakx.habitcity.equipment.error.EquipmentFullException;
import io.github.xpakx.habitcity.equipment.error.EquipmentNotFoundException;
import io.github.xpakx.habitcity.money.Money;
import io.github.xpakx.habitcity.money.MoneyRepository;
import io.github.xpakx.habitcity.resource.Resource;
import io.github.xpakx.habitcity.resource.ResourceRepository;
import io.github.xpakx.habitcity.shop.dto.BuyRequest;
import io.github.xpakx.habitcity.shop.dto.ItemResponse;
import io.github.xpakx.habitcity.shop.dto.ShopResponse;
import io.github.xpakx.habitcity.shop.error.NotEnoughMoneyException;
import io.github.xpakx.habitcity.shop.error.ShopItemEmptyException;
import io.github.xpakx.habitcity.shop.error.WrongOwnerException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
                    .map(a -> toShopEntry(a, shop, random.nextFloat(), random.nextFloat(0.2f)-0.1f))
                    .toList();
            entriesToAdd.addAll(entries);
        }
        entryRepository.saveAll(entriesToAdd);
    }

    private ShopEntry toShopEntry(Resource a, Shop shop, float maxAmountPercent, float costBonus) {
        int amount = (int) (a.getMaxStock()*maxAmountPercent);
        int cost = (int) (a.getBaseCost() + a.getBaseCost()*costBonus);
        ShopEntry entry = new ShopEntry();
        entry.setShop(shop);
        entry.setResource(a);
        entry.setAmount(amount > 0 ? amount : 1);
        entry.setPrice(cost > 0 ? cost : 1);
        return entry;
    }

    @Override
    @Transactional
    public ItemResponse buy(BuyRequest request, Long shopEntryId, Long userId) {
        ShopEntry entry = getShopEntry(request, shopEntryId, userId);
        UserEquipment eq = equipmentRepository.getByUserId(userId).orElseThrow(EquipmentNotFoundException::new);
        List<EquipmentEntry> eqEntries = prepareEqEntries(eq, entry, request.getAmount());
        exchangeMoney(entry, userId, entry.getAmount());
        entry.setAmount(entry.getAmount()-request.getAmount());
        if(entry.getAmount() > 0) {
            entryRepository.save(entry);
        } else {
            entryRepository.delete(entry);
        }
        equipmentEntryRepository.saveAll(eqEntries);
        return createItemResponse(request, eqEntries.get(0));
    }

    @Override
    public ShopResponse getShop(Long shopId, Long userId) {
        if(!shopRepository.existsByIdAndUserId(shopId, userId)) {
            throw new WrongOwnerException();
        }
        ShopResponse response = new ShopResponse();
        response.setItems(entryRepository.findByShopId(shopId));
        return response;
    }

    @Override
    public void addUserShop(AccountEvent event) {
        Shop shop = new Shop();
        shop.setUserId(event.getId());
        shop.setMaxSize(5);
        shop.setMaxRarity(1);
        shopRepository.save(shop);
    }

    private List<EquipmentEntry> prepareEqEntries(UserEquipment eq, ShopEntry entry, int amount) {
        List<EquipmentEntry> eqEntries = getEntriesForItem(eq.getId(), entry);
        int pointer = 0;
        while(amount > 0 && pointer < eqEntries.size()) {
            EquipmentEntry eqEntry = eqEntries.get(pointer);
            pointer++;
            int stockSize = getStockSize(eqEntry);
            if(eqEntry.getAmount() < stockSize) {
                int oldAmount = eqEntry.getAmount();
                eqEntry.setAmount(Math.min(eqEntry.getAmount() + amount, stockSize));
                amount -= eqEntry.getAmount() - oldAmount;
            }
        }
        long itemsInEquipment = equipmentEntryRepository.countByEquipmentId(eq.getId());
        if(itemsInEquipment >= eq.getMaxSize()) {
            throw new EquipmentFullException();
        }
        if(amount > 0) {
            eqEntries.add(createEquipmentEntry(amount, eq, entry));
        }
        return eqEntries;
    }

    private int getStockSize(EquipmentEntry entry) {
        if(entry.getResource() != null) {
            return entry.getResource().getMaxStock();
        }
        return 1;
    }

    private List<EquipmentEntry> getEntriesForItem(Long id, ShopEntry entry) {
        if(entry.getResource() != null) {
            return equipmentEntryRepository.findByEquipmentIdAndResourceId(id, entry.getResource().getId());
        }
        if(entry.getBuilding() != null) {
            return equipmentEntryRepository.findByEquipmentIdAndBuildingId(id, entry.getBuilding().getId());
        }
        if(entry.getShip() != null) {
            return equipmentEntryRepository.findByEquipmentIdAndShipId(id, entry.getShip().getId());
        }
        return new ArrayList<>();
    }

    private EquipmentEntry createEquipmentEntry(int amount, UserEquipment eq, ShopEntry entry) {
        EquipmentEntry eqEntry = new EquipmentEntry();
        eqEntry.setAmount(amount);
        eqEntry.setResource(entry.getResource());
        eqEntry.setBuilding(entry.getBuilding());
        eqEntry.setShip(entry.getShip());
        eqEntry.setEquipment(eq);
        return eqEntry;
    }

    private void exchangeMoney(ShopEntry entry, Long userId, int amount) {
        Money money = moneyRepository.findByEquipmentUserId(userId).orElseThrow();
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

    private ShopEntry getShopEntry(BuyRequest request, Long shopEntryId, Long userId) {
        ShopEntry entry = entryRepository.findById(shopEntryId)
                .orElseThrow(WrongOwnerException::new);
        if(!Objects.equals(entry.getShop().getUserId(), userId)) {
            throw new WrongOwnerException();
        }
        if(entry.getAmount() - request.getAmount() < 0) {
            throw new ShopItemEmptyException();
        }
        return entry;
    }
}
