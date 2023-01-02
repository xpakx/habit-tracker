package io.github.xpakx.habitgame.battle.dto;

import io.github.xpakx.habitgame.battle.BattleObjective;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BattleResponse {
    private Long battleId;
    private Integer width;
    private Integer height;
    private boolean finished;
    private boolean started;
    private BattleObjective objective;
    private Integer turn;
    List<BattleShip> ships;
    List<BattleShip> enemyShips;
}
