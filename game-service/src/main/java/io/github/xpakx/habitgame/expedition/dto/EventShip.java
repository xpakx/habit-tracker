package io.github.xpakx.habitgame.expedition.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EventShip {
    private Long shipId;
    private String name;
    private String code;
    private Integer maxCargo;
    private Integer rarity;
    private Integer size;
    private List<Cargo> cargo;
}
