package io.github.xpakx.habitcity.equipment;

import io.github.xpakx.habitcity.equipment.dto.AccountEvent;
import io.github.xpakx.habitcity.equipment.dto.EquipmentResponse;
import io.github.xpakx.habitcity.money.MoneyService;
import io.github.xpakx.habitcity.shop.dto.ShopResponse;
import io.github.xpakx.habitcity.shop.error.WrongOwnerException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
