package io.github.xpakx.habitgame.expedition.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActionResponse {
    private Long expeditionId;
    private boolean completed;
}
