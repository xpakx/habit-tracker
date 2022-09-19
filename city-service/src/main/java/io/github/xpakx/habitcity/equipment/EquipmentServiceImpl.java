package io.github.xpakx.habitcity.equipment;

import io.github.xpakx.habitcity.crafting.dto.CraftElem;
import io.github.xpakx.habitcity.crafting.dto.CraftRequest;
import io.github.xpakx.habitcity.crafting.error.NotEnoughResourcesException;
import io.github.xpakx.habitcity.equipment.dto.AccountEvent;
import io.github.xpakx.habitcity.equipment.dto.CraftList;
import io.github.xpakx.habitcity.equipment.dto.EquipmentResponse;
import io.github.xpakx.habitcity.money.MoneyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EquipmentServiceImpl implements EquipmentService {
    private final UserEquipmentRepository equipmentRepository;
    private final EquipmentEntryRepository entryRepository;
    private final MoneyService moneyService;

    @Override
    @Transactional
    public void addUserEquipment(AccountEvent event) {
        UserEquipment equipment = new UserEquipment();
        equipment.setUserId(event.getId());
        equipment.setMaxSize(30);
        equipment = equipmentRepository.save(equipment);
        moneyService.addMoneyToEquipment(equipment);
    }

    @Override
    public EquipmentResponse getEquipment(Long userId) {
        UserEquipment equipment = equipmentRepository.getByUserId(userId).orElseThrow();
        EquipmentResponse response = new EquipmentResponse();
        response.setItems(entryRepository.findByEquipmentId(equipment.getId()));
        return response;
    }

    @Override
    public void subtractResources(CraftList request, List<EquipmentEntry> eqEntries) {
        List<CraftElem> craftElems = request.asCraftList();
        for(CraftElem elem : craftElems) {
            int amount = request.getAmount();
            List<EquipmentEntry> entriesWithElem = eqEntries.stream().filter((a) -> Objects.equals(a.getResource().getId(), elem.getId())).toList();
            int pointer = 0;
            while(amount > 0 && pointer < entriesWithElem.size()) {
                EquipmentEntry eqEntry = entriesWithElem.get(pointer);
                pointer++;
                int oldAmount = eqEntry.getAmount();
                eqEntry.setAmount(Math.max(eqEntry.getAmount() - amount, 0));
                amount -= oldAmount - eqEntry.getAmount();
            }
            if(amount > 0) {
                throw new NotEnoughResourcesException();
            }
        }
    }
}
