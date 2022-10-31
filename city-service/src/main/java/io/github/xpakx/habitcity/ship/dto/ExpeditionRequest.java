package io.github.xpakx.habitcity.ship.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExpeditionRequest {
    List<ExpeditionShip> ships;
    Long islandId;
}
