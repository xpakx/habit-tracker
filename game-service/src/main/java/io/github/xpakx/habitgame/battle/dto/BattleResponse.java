package io.github.xpakx.habitgame.battle.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BattleResponse {
    private Long battleId;
    private Integer width;
    private Integer height;
}
