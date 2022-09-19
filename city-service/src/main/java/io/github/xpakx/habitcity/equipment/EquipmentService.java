package io.github.xpakx.habitcity.equipment;

import io.github.xpakx.habitcity.crafting.dto.CraftRequest;
import io.github.xpakx.habitcity.equipment.dto.AccountEvent;
import io.github.xpakx.habitcity.equipment.dto.EquipmentResponse;

import java.util.List;

public interface EquipmentService {
    void addUserEquipment(AccountEvent event);
    EquipmentResponse getEquipment(Long userId);
    void subtractResources(CraftRequest request, List<EquipmentEntry> eqEntries);
}
