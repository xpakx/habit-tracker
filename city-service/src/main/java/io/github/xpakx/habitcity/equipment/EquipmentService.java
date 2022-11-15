package io.github.xpakx.habitcity.equipment;

import io.github.xpakx.habitcity.equipment.dto.AccountEvent;
import io.github.xpakx.habitcity.equipment.dto.CraftList;
import io.github.xpakx.habitcity.equipment.dto.EquipmentResponse;

import java.util.List;

public interface EquipmentService {
    void addUserEquipment(AccountEvent event);
    EquipmentResponse getEquipment(Long userId);
    void subtractResources(CraftList request, List<EquipmentEntry> eqEntries);
    EquipmentResponse getBuildingPlans(Long userId);
    EquipmentResponse getShips(Long userId);
}
