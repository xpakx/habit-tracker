package io.github.xpakx.habitcity.equipment;

import io.github.xpakx.habitcity.equipment.dto.AccountEvent;
import io.github.xpakx.habitcity.equipment.dto.EquipmentResponse;

public interface EquipmentService {
    void addUserEquipment(AccountEvent event);
    EquipmentResponse getEquipment(Long userId);
}
