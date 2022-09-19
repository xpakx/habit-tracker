package io.github.xpakx.habitcity.crafting;

import io.github.xpakx.habitcity.crafting.dto.CraftRequest;
import io.github.xpakx.habitcity.crafting.error.NoSuchRecipeException;
import io.github.xpakx.habitcity.equipment.*;
import io.github.xpakx.habitcity.equipment.error.EquipmentFullException;
import io.github.xpakx.habitcity.shop.dto.ItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CraftServiceImpl implements CraftService {
    private final RecipeRepository recipeRepository;
    private final UserEquipmentRepository equipmentRepository;
    private final EquipmentEntryRepository entryRepository;
    private final EquipmentService equipment;

    @Override
    @Transactional
    public ItemResponse craft(CraftRequest request, Long userId) {
        Recipe recipe = recipeRepository.getRecipeByResources(
                request.getElem1().getId(), request.getElem2().getId(), request.getElem3().getId(),
                request.getElem4().getId(), request.getElem5().getId(), request.getElem6().getId(),
                request.getElem7().getId(), request.getElem8().getId(), request.getElem9().getId()
        ).orElseThrow(NoSuchRecipeException::new);

        //TODO: test if player unlocked recipe

        UserEquipment eq = equipmentRepository.getByUserId(userId).orElseThrow();
        List<EquipmentEntry> eqEntries = entryRepository.getByEquipmentId(eq.getId());

        equipment.subtractResources(request, eqEntries);
        eqEntries.addAll(prepareEqEntries(eqEntries, eq, recipe, request.getAmount()));
        entryRepository.saveAll(eqEntries.stream().filter((a -> a.getAmount() > 0)).toList());
        entryRepository.deleteAll(eqEntries.stream().filter((a -> a.getAmount() <= 0)).toList());
        return createItemResponse(request, recipe);
    }

    private ItemResponse createItemResponse(CraftRequest request, Recipe recipe) {
        ItemResponse response = new ItemResponse();
        response.setAmount(request.getAmount());
        response.setName(recipe.getShip() != null ? recipe.getShip().getName() : recipe.getResource().getName());
        return response;
    }



    private List<EquipmentEntry> prepareEqEntries(List<EquipmentEntry> eqEntries, UserEquipment eq, Recipe recipe, int amount) {
        amount = fillExistingEntries(eqEntries, amount, recipe);
        if(amount <= 0) {
            return new ArrayList<>();
        }
        return createNewEntries(eq, recipe, amount);
    }

    private List<EquipmentEntry> createNewEntries(UserEquipment eq, Recipe recipe, int amount) {
        List<EquipmentEntry> newEqEntries = new ArrayList<>();
        int stockSize = recipe.getShip() != null ? 1 : recipe.getResource().getMaxStock();
        int requiredSlots = amount /stockSize;
        long itemsInEquipment = entryRepository.countByEquipmentId(eq.getId());
        if(itemsInEquipment + requiredSlots > eq.getMaxSize()) {
            throw new EquipmentFullException();
        }
        while(amount > 0) {
            newEqEntries.add(createEquipmentEntry(Math.min(stockSize, amount), eq, recipe));
            amount -= stockSize;
        }
        return  newEqEntries;
    }

    private int fillExistingEntries(List<EquipmentEntry> eqEntries, int amount, Recipe recipe) {
        List<EquipmentEntry> oldEntries = eqEntries.stream()
                .filter(
                        a -> recipe.getShip() != null ? (a.getShip() != null && Objects.equals(a.getShip().getId(), recipe.getShip().getId())) :
                                (a.getResource() != null && Objects.equals(a.getResource().getId(), recipe.getResource().getId()))
                ).toList();
        int pointer = 0;
        while(amount > 0 && pointer < oldEntries.size()) {
            EquipmentEntry eqEntry = oldEntries.get(pointer);
            pointer++;
            int stockSize = getStockSize(eqEntry);
            if(eqEntry.getAmount() < stockSize) {
                int oldAmount = eqEntry.getAmount();
                eqEntry.setAmount(Math.min(eqEntry.getAmount() + amount, stockSize));
                amount -= eqEntry.getAmount() - oldAmount;
            }
        }
        return amount;
    }

    private int getStockSize(EquipmentEntry entry) {
        if(entry.getResource() != null) {
            return entry.getResource().getMaxStock();
        }
        return 1;
    }

    private EquipmentEntry createEquipmentEntry(int amount, UserEquipment eq, Recipe recipe) {
        EquipmentEntry eqEntry = new EquipmentEntry();
        eqEntry.setAmount(amount);
        eqEntry.setResource(recipe.getResource());
        eqEntry.setShip(recipe.getShip());
        eqEntry.setEquipment(eq);
        return eqEntry;
    }
}
