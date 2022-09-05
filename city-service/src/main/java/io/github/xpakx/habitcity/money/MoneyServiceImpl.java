package io.github.xpakx.habitcity.money;

import io.github.xpakx.habitcity.equipment.UserEquipment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MoneyServiceImpl implements MoneyService {
    private final MoneyRepository moneyRepository;
    @Override
    public void addMoneyToEquipment(UserEquipment equipment) {
        Money money = new Money();
        money.setAmount(2000L);
        money.setEquipment(equipment);
        moneyRepository.save(money);
    }
}
