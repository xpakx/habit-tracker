package io.github.xpakx.habitgame.battle.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttackResult {
    Long shipId;
    Integer x;
    Integer y;
    Integer damage;
}
