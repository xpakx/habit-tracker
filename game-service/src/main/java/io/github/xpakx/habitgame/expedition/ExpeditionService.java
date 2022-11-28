package io.github.xpakx.habitgame.expedition;

import io.github.xpakx.habitgame.expedition.dto.ActionRequest;
import io.github.xpakx.habitgame.expedition.dto.ActionResponse;
import io.github.xpakx.habitgame.expedition.dto.ExpeditionEvent;
import io.github.xpakx.habitgame.expedition.dto.ExpeditionResultResponse;

import java.util.List;

public interface ExpeditionService {
    void addExpedition(ExpeditionEvent event);
    List<Expedition> getActiveExpeditions(Long userId);
    ExpeditionResultResponse getResult(Long expeditionId, Long userId);
    ActionResponse completeExpedition(ActionRequest request, Long expeditionId, Long userId);
    ActionResponse returnToCity(ActionRequest request, Long expeditionId, Long userId);
    boolean completeExpedition(Long expeditionId);
}
