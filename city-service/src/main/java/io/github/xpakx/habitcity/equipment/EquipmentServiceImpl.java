package io.github.xpakx.habitcity.equipment;

import io.github.xpakx.habitcity.equipment.dto.AccountEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EquipmentServiceImpl implements EquipmentService {
    private final UserEquipmentRepository equipmentRepository;


    @Override
    public void addUserEquipment(AccountEvent event) {
        UserEquipment equipment = new UserEquipment();
        equipment.setUserId(event.getId());
        equipment.setMaxSize(30);
        equipmentRepository.save(equipment);
    }
}
