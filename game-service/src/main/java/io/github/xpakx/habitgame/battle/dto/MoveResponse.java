package io.github.xpakx.habitgame.battle.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoveResponse {
    MoveAction action;
    boolean success;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    MoveResult move;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    AttackResult attack;
}
