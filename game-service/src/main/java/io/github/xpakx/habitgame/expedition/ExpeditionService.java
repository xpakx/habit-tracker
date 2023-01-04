package io.github.xpakx.habitgame.expedition;

import io.github.xpakx.habitgame.expedition.dto.*;

import java.util.List;

public interface ExpeditionService {
    void addExpedition(ExpeditionEvent event);
    List<ExpeditionSummary> getActiveExpeditions(Long userId);
    ExpeditionResultResponse getResult(Long expeditionId, Long userId);
    ActionResponse completeExpedition(ActionRequest request, Long expeditionId, Long userId);
    ActionResponse returnToCity(ActionRequest request, Long expeditionId, Long userId);
    boolean completeResult(Long expeditionId);
}
