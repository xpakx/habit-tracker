package io.github.xpakx.habitcity.ship.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExpeditionShip {
    Long shipId;
    List<ExpeditionEquipment> equipment;
}
