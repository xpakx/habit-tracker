package io.github.xpakx.habitcity.ship.dto;

import io.github.xpakx.habitcity.clients.dto.Cargo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExpeditionEndEvent {
    List<Long> shipsIds;
    List<Long> destroyedShipsIds;
    List<Long> damagedShipsIds;
    List<Cargo> cargo;
}
