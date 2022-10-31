package io.github.xpakx.habitcity.ship.dto;

import io.github.xpakx.habitcity.equipment.dto.CraftElem;
import io.github.xpakx.habitcity.equipment.dto.CraftList;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class ExpeditionRequest implements CraftList {
    List<ExpeditionShip> ships;
    Long islandId;

    @Override
    public List<CraftElem> asCraftList() {
        return ships.stream()
                .map(ExpeditionShip::getEquipment)
                .flatMap(Collection::stream)
                .map(this::toCraftElem)
                .toList();
    }

    private CraftElem toCraftElem(ExpeditionEquipment a) {
        return a;
    }

    @Override
    public Integer getAmount() {
        return 1;
    }
}
