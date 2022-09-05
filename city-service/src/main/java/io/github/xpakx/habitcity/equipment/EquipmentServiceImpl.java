package io.github.xpakx.habitcity.equipment;

import io.github.xpakx.habitcity.equipment.dto.AccountEvent;
import io.github.xpakx.habitcity.money.MoneyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EquipmentServiceImpl implements EquipmentService {
    private final UserEquipmentRepository equipmentRepository;
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
}
