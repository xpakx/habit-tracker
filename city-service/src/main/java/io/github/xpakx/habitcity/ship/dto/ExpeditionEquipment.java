package io.github.xpakx.habitcity.ship.dto;

import io.github.xpakx.habitcity.equipment.dto.CraftElem;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpeditionEquipment implements CraftElem {
    Long id;
    Integer amount;
}
