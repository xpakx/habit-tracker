package io.github.xpakx.habitcity.ship.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShipResponse {
    Long id;
    String name;
    String code;
    Long cityId;
    Long shipId;
}
