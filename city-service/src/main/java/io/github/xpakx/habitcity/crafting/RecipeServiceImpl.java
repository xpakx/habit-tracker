package io.github.xpakx.habitcity.crafting;

import io.github.xpakx.habitcity.crafting.dto.CraftElem;
import io.github.xpakx.habitcity.crafting.dto.CraftRequest;
import io.github.xpakx.habitcity.crafting.error.NotEnoughResourcesException;
import io.github.xpakx.habitcity.equipment.EquipmentEntry;
import io.github.xpakx.habitcity.equipment.EquipmentEntryRepository;
import io.github.xpakx.habitcity.equipment.UserEquipment;
import io.github.xpakx.habitcity.equipment.UserEquipmentRepository;
import io.github.xpakx.habitcity.equipment.error.EquipmentFullException;
import io.github.xpakx.habitcity.shop.ShopEntry;
import io.github.xpakx.habitcity.shop.dto.ItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {
    private final RecipeRepository recipeRepository;
    private final UserEquipmentRepository equipmentRepository;
    private final EquipmentEntryRepository entryRepository;

    @Override
    public ItemResponse craft(CraftRequest request, Long userId) {
        Recipe recipe = recipeRepository.getRecipeByResources(
                request.getElem1().getId(), request.getElem2().getId(), request.getElem3().getId(),
                request.getElem4().getId(), request.getElem5().getId(), request.getElem6().getId(),
                request.getElem7().getId(), request.getElem8().getId(), request.getElem9().getId()
        ).orElseThrow();

        //TODO: test if player unlocked recipe

        UserEquipment eq = equipmentRepository.getByUserId(userId).orElseThrow();
        List<EquipmentEntry> eqEntries = entryRepository.getByEquipmentId(eq.getId());

        subtractResources(request, eqEntries);
        eqEntries.addAll(prepareEqEntries(eqEntries, eq, recipe, request.getAmount()));

        entryRepository.saveAll(eqEntries);
        ItemResponse response = new ItemResponse();
        response.setAmount(request.getAmount());
        response.setName(recipe.getShip() != null ? recipe.getShip().getName() : recipe.getResource().getName());
        return response;
    }

    private void subtractResources(CraftRequest request, List<EquipmentEntry> eqEntries) {
        List<CraftElem> craftElems = request.asList();
        for(CraftElem elem : craftElems) {
            int amount = request.getAmount();
            List<EquipmentEntry> entriesWithElem = eqEntries.stream().filter((a) -> Objects.equals(a.getResource().getId(), elem.getId())).toList();
            int pointer = 0;
            while(amount > 0 && pointer < entriesWithElem.size()) {
                EquipmentEntry eqEntry = entriesWithElem.get(pointer);
                pointer++;
                int oldAmount = eqEntry.getAmount();
                eqEntry.setAmount(Math.max(eqEntry.getAmount() - amount, 0));
                amount -= eqEntry.getAmount() - oldAmount;
            }
            if(amount > 0) {
                throw new NotEnoughResourcesException();
            }
        }
    }

    private List<EquipmentEntry> prepareEqEntries(List<EquipmentEntry> eqEntries, UserEquipment eq, Recipe recipe, int amount) {
        List<EquipmentEntry> newEqEntries = new ArrayList<>();
        //TODO: fill existing entries first
        int stockSize = recipe.getShip() != null ? 1 : recipe.getResource().getMaxStock();
        int requiredSlots = amount/stockSize;
        long itemsInEquipment = entryRepository.countByEquipmentId(eq.getId());
        if(itemsInEquipment + requiredSlots > eq.getMaxSize()) {
            throw new EquipmentFullException();
        }
        while(amount > 0) {
            newEqEntries.add(createEquipmentEntry(Math.min(stockSize, amount), eq, recipe));
            amount -= stockSize;
        }
        return newEqEntries;
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
