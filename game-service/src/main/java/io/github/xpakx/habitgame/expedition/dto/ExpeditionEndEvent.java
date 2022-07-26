package io.github.xpakx.habitgame.expedition.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExpeditionEndEvent {
    Long userId;
    List<Long> shipsIds;
    List<Long> destroyedShipsIds;
    List<Long> damagedShipsIds;
    List<Cargo> cargo;
    String treasure;
}
