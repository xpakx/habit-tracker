package io.github.xpakx.habitgame.battle.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BattleShip {
    private Long id;
    private Long shipId;
    private String name;
    private String code;
    private Integer rarity;
    private Integer size;
    private Integer hp;
    private Integer strength;
    private Integer hitRate;
    private Integer criticalRate;
    private boolean destroyed;
    private boolean damaged;
    private boolean prepared;
    private boolean action;
    private boolean movement;
    private boolean enemy;
    private BattlePosition position;
}
