package io.github.xpakx.habitgame.expedition.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExpeditionEvent {
    Long userId;
    Long islandId;
    List<EventShip> ships;

}
