package io.github.xpakx.habitcity.clients.dto;

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
